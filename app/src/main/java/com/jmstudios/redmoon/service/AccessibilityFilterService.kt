package com.jmstudios.redmoon.service

import android.accessibilityservice.AccessibilityService
import android.animation.ValueAnimator
import android.graphics.PixelFormat
import android.view.View
import android.view.ViewGroup
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
        (getSystemService(WINDOW_SERVICE) as WindowManager).addView(mFilter, mFilter.mLayoutParams)
        mFilter.visibility = View.GONE;
        EventBus.register(this)
    }

    override fun onDestroy() {
        EventBus.unregister(this);
        (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(mFilter)
        super.onDestroy()
    }

    @Subscribe
    fun turnOnOrOff(cmd: accessibilityServiceCommand) {
        mFilter.setBackgroundColor(activeProfile.filterColor)
        mFilter.visibility = if (cmd.command.turnOn) View.VISIBLE else View.GONE
    }
}
