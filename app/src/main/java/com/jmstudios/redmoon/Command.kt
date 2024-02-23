/*
 * Copyright (c) 2016 Marien Raat <marienraat@riseup.net>
 * Copyright (c) 2017 Stephen Michel <s@smichel.me>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.jmstudios.redmoon

import android.content.ComponentName
import android.content.Intent
import android.os.Build

import com.jmstudios.redmoon.helper.Logger
import com.jmstudios.redmoon.service.FilterService

private const val DURATION_LONG = 1000f // One second
private const val DURATION_SHORT = 250f
private const val DURATION_INSTANT = 0f

enum class Command(val time: Float) {
    ON(DURATION_LONG) {
        override val turnOn: Boolean = true
        override val foreground = true
        override fun onAnimationStart(service: FilterService) {
            service.start(true)
        }
    },
    OFF(DURATION_LONG) {
        override val turnOn: Boolean = false
        override val foreground = false
        override fun onAnimationStart(service: FilterService) {
            service.stopForeground(true)
        }
        override fun onAnimationEnd(service: FilterService) {
            service.stopSelf()
        }
    },
    PAUSE(DURATION_SHORT) {
        override val turnOn: Boolean = false
        override val foreground = true
        override fun onAnimationStart(service: FilterService) {
            service.start(false)
        }
    },
    RESUME(DURATION_SHORT) {
        override val turnOn: Boolean = true
        override val foreground = true
        override fun onAnimationStart(service: FilterService) {
            service.start(true)
        }
    },
    SHOW_PREVIEW(DURATION_INSTANT) {
        override val turnOn: Boolean = true
        override val foreground = true

        override fun onAnimationStart(service: FilterService) {
            if (filterWasOn == null) {
                filterWasOn = filterIsOn
            }
            service.start(true)
        }
    },
    HIDE_PREVIEW(DURATION_INSTANT) {
        override val turnOn: Boolean
            get() = filterWasOn == true
        override val foreground = false

        override fun onAnimationEnd(service: FilterService) {
            if (filterWasOn != true) {
                service.stopForeground(true)
                service.stopSelf()
                filterWasOn = null
            }
        }
    };

    val intent: Intent
        get() = intent(FilterService::class).putExtra(EXTRA_COMMAND, name)

    fun send(): ComponentName? {
        // Starting from SDK 26, startService doesn't allow to start a
        // foreground service from background. Most of the time, FilterService
        // is started from foreground anyways but there are exceptions: for
        // instance when launched from a quick setting tile.
        // See https://developer.android.com/about/versions/oreo/background
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && this.foreground) {
            appContext.startForegroundService(intent)
        } else {
            appContext.startService(intent)
        }
    }

    abstract val turnOn: Boolean
    abstract val foreground: Boolean

    open fun onAnimationStart(service: FilterService) {}
    open fun onAnimationCancel(service: FilterService) {}
    open fun onAnimationEnd(service: FilterService) {}
    open fun onAnimationRepeat(service: FilterService) {}

    companion object : Logger() {
        private const val EXTRA_COMMAND = "jmstudios.bundle.key.command"

        private var filterWasOn: Boolean? = null

        fun getCommand(intent: Intent): Command {
            val commandName = intent.getStringExtra(EXTRA_COMMAND) ?: ""
            return valueOf(commandName)
        }

        fun toggle(on: Boolean = !filterIsOn) {
            if (on) ON.send() else OFF.send()
        }

        fun preview(on: Boolean) {
            if (on) SHOW_PREVIEW.send() else HIDE_PREVIEW.send()
        }
    }
}
