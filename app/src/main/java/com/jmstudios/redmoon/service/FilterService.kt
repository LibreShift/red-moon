/*
 * Copyright (c) 2016  Marien Raat <marienraat@riseup.net>
 * Copyright (c) 2017  Stephen Michel <s@smichel.me>
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 *     Copyright (c) 2015 Chris Nguyen
 *
 *     Permission to use, copy, modify, and/or distribute this software
 *     for any purpose with or without fee is hereby granted, provided
 *     that the above copyright notice and this permission notice appear
 *     in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
 *     CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS
 *     OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 *     NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 *     CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.jmstudios.redmoon.service

import android.animation.ValueAnimator
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.jmstudios.redmoon.*
import java.util.concurrent.Executors

import com.jmstudios.redmoon.filter.surfaceflinger.SurfaceFlinger
import com.jmstudios.redmoon.helper.Logger
import com.jmstudios.redmoon.helper.Permission
import com.jmstudios.redmoon.manager.CurrentAppMonitor

import org.greenrobot.eventbus.Subscribe
import com.topjohnwu.superuser.Shell

class FilterService : Service() {

    private lateinit var mFilter: Filter
    private lateinit var mAnimator: ValueAnimator
    private lateinit var mCurrentAppMonitor: CurrentAppMonitor
    private lateinit var mNotification: Notification
    private val executor = Executors.newSingleThreadScheduledExecutor()

    override fun onCreate() {
        super.onCreate()
        Log.i("onCreate")
        if (Config.useRoot) {
            Log.i("Starting in root mode")
            mFilter = SurfaceFlinger()
        } else {
            Log.i("Starting in overlay mode")
            mFilter = Overlay(this)
        }
        mCurrentAppMonitor = CurrentAppMonitor(this, executor)
        mNotification = Notification(this, mCurrentAppMonitor)
        mAnimator = ValueAnimator.ofObject(ProfileEvaluator(), mFilter.profile).apply {
            addUpdateListener { valueAnimator ->
               mFilter.profile = valueAnimator.animatedValue as Profile
            }
        }
        startForeground(NOTIFICATION_ID, mNotification.build(false))
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("onStartCommand($intent, ${Command.getCommand(intent)}, $flags, $startId)")
        fun fadeInOrOut() {
            val cmd = Command.getCommand(intent)
            val end = if (cmd.turnOn) activeProfile else mFilter.profile.off
            mAnimator.run {
                setObjectValues(mFilter.profile, end)
                val fraction = if (isRunning) animatedFraction else 1f
                duration = (cmd.time * fraction).toLong()
                removeAllListeners()
                addListener(CommandAnimatorListener(cmd, this@FilterService))
                Log.i("Animating from ${mFilter.profile} to $end in $duration")
                start()
            }
        }
        if (Config.useRoot) {
            val hasRoot = Shell.rootAccess()
            if (hasRoot) {
                fadeInOrOut()
            } else {
                Log.i("Root permission denied. Disabling root mode.")
                Toast.makeText(this, R.string.toast_root_unavailable, Toast.LENGTH_SHORT).show()
                Config.useRoot = false;
                stopForeground(false)
            }
        } else if (isAccessibilityServiceOn(applicationContext)) {
            EventBus.post(accessibilityServiceCommand(Command.getCommand(intent)))
            mCurrentAppMonitor.monitoring = Command.getCommand(intent).turnOn && Config.secureSuspend
            Command.getCommand(intent).onAnimationStart(this@FilterService)
            Command.getCommand(intent).onAnimationEnd(this@FilterService)
        } else {
            if (Permission.Overlay.isGranted) {
                fadeInOrOut()
            } else {
                Log.i("Overlay permission denied.")
                EventBus.post(overlayPermissionDenied())
                stopForeground(false)
            }
        }

        // Do not attempt to restart if the hosting process is killed by Android
        return Service.START_NOT_STICKY
    }

    fun start(isOn: Boolean) {
        if (!filterIsOn) {
            EventBus.register(this)
            filterIsOn = true
            mCurrentAppMonitor.monitoring = Config.secureSuspend
        }
        startForeground(NOTIFICATION_ID, mNotification.build(isOn))
    }

    override fun onDestroy() {
        Log.i("onDestroy")
        EventBus.unregister(this)
        if (filterIsOn) {
            Log.w("Service killed while filter was on!")
            filterIsOn = false
            mCurrentAppMonitor.monitoring = false
        }
        if(isAccessibilityServiceOn(applicationContext)) {
            EventBus.post(accessibilityServiceCommand(Command.OFF))
        }
        mFilter.onDestroy()
        executor.shutdownNow()
        super.onDestroy()
    }

    @Subscribe fun onProfileUpdated(profile: Profile) {
        Log.i("onProfileUpdated")
        if(isAccessibilityServiceOn(applicationContext)) {
            EventBus.post(accessibilityServiceCommand(Command.ON))
        } else {
            mFilter.profile = profile
        }
        startForeground(NOTIFICATION_ID, mNotification.build(true))
    }

    @Subscribe fun onSecureSuspendChanged(event: secureSuspendChanged) {
        mCurrentAppMonitor.monitoring = Config.secureSuspend
    }

    override fun onBind(intent: Intent): IBinder? = null // Prevent binding.

    companion object : Logger() {
        private const val NOTIFICATION_ID = 1
    }
}
