package com.jmstudios.redmoon.service

import android.accessibilityservice.AccessibilityService
import android.animation.ValueAnimator
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.jmstudios.redmoon.*
import com.jmstudios.redmoon.helper.Logger
import org.greenrobot.eventbus.Subscribe


class AccessibilityFilterService : AccessibilityService() {
    lateinit var mFilter: Overlay

    override fun onAccessibilityEvent(event: AccessibilityEvent) {}
    override fun onInterrupt() {}

    override fun onServiceConnected() {
        mFilter = Overlay(applicationContext)
        val lp = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.or(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
        (getSystemService(WINDOW_SERVICE) as WindowManager).addView(mFilter, lp)

        mFilter.visibility = View.GONE;

        EventBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.unregister(this);
    }

    @Subscribe
    fun turnOnOrOff(cmd: accessibilityServiceCommand) {
        mFilter.setBackgroundColor(activeProfile.filterColor)
        mFilter.visibility = if (cmd.command.turnOn) View.VISIBLE else View.GONE
    }
}
