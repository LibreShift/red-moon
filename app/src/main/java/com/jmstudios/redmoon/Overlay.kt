/*
 * Copyright (c) 2016 Marien Raat <marienraat@riseup.net>
 * Copyright (c) 2017 Stephen Michel <s@smichel.me>
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
package com.jmstudios.redmoon

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager

import com.jmstudios.redmoon.helper.Logger
import com.jmstudios.redmoon.manager.BrightnessManager
import com.jmstudios.redmoon.receiver.OrientationChangeReceiver

import kotlin.properties.Delegates

import org.greenrobot.eventbus.Subscribe

class Overlay(context: Context) : View(context), Filter,
        OrientationChangeReceiver.OnOrientationChangeListener {

    private val mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mOrientationReceiver = OrientationChangeReceiver(context, this)
    private val mBrightnessManager = BrightnessManager(context)

    override fun onCreate() {
        Log.i("onCreate()")
    }

    override var profile: Profile = activeProfile.off
        set(value) {
            Log.i("profile set to: $value")
            field = value
            filtering = !value.isOff
        }

    override fun onDestroy() {
        Log.i("onDestroy()")
        filtering = false
    }

    private var filtering: Boolean by Delegates.observable(false) { _, isOn, turnOn ->
        when {
            !isOn && turnOn -> show()
            isOn && !turnOn -> hide()
            isOn && turnOn -> update()
        }
    }

    private fun show() {
        mWindowManager.addView(this, mLayoutParams)
        mBrightnessManager.brightnessLowered = profile.lowerBrightness
        mOrientationReceiver.register()
        EventBus.register(this)
    }

    private fun hide() {
        mBrightnessManager.brightnessLowered = false
        mWindowManager.removeView(this)
        mOrientationReceiver.unregister()
        EventBus.unregister(this)
    }

    private fun update() {
        invalidate() // Forces call to onDraw
        if (Config.buttonBacklightFlag == "dim") {
            reLayout()
        }
        mBrightnessManager.brightnessLowered = profile.lowerBrightness
    }

    val mLayoutParams = WindowManager.LayoutParams().apply {
        buttonBrightness = Config.buttonBacklightLevel
        // TODO: why is cutout always null?
        // if(atLeastAPI(Build.VERSION_CODES.P)) {
        //     val cutout = WindowInsets.Builder().build().displayCutout
        //     val top = cutout?.boundingRectTop?.height() ?: 0
        //     val bottom = cutout?.boundingRectBottom?.height() ?: 0
        //     val left = cutout?.boundingRectLeft?.width() ?: 0
        //     val right = cutout?.boundingRectRight?.width() ?: 0
        //     height = Resources.getSystem().displayMetrics.heightPixels + top + bottom
        //     width = Resources.getSystem().displayMetrics.widthPixels + left + right
        //     x = -left
        //     y = -top
        // } else {
            height = Resources.getSystem().displayMetrics.heightPixels + 4000
            width = Resources.getSystem().displayMetrics.widthPixels + 4000
            x = -1000
            y = -1000
        // }
        format = PixelFormat.TRANSLUCENT
        type = if (isAccessibilityServiceOn(context) && atLeastAPI(Build.VERSION_CODES.M)) {
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        } else if (atLeastAPI(Build.VERSION_CODES.O)) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }
        flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            .or(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            .or(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            .or(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    private fun reLayout() = mWindowManager.updateViewLayout(this, mLayoutParams)

    override fun onDraw(canvas: Canvas) = canvas.drawColor(profile.filterColor)

    override fun onOrientationChanged() {
        reLayout()
    }

    @Subscribe fun onButtonBacklightChanged(event: buttonBacklightChanged) {
        reLayout()
    }

    companion object : Logger()
}
