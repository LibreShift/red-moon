package com.jmstudios.redmoon.filter.surfaceflinger

import android.graphics.Color
import com.topjohnwu.superuser.Shell
import kotlin.properties.Delegates
import com.jmstudios.redmoon.Filter
import com.jmstudios.redmoon.activeProfile
import com.jmstudios.redmoon.helper.Logger

class SurfaceFlinger() : Filter {
    private var filtering: Boolean by Delegates.observable(false) { _, isOn, turnOn ->
        when {
            !isOn && turnOn -> show()
            isOn && !turnOn -> hide()
            isOn && turnOn -> update()
        }
    }

    private fun show() {

    }

    private fun hide() {
        val call = "service call SurfaceFlinger 1015 i32 1 " +
                "f 1 f  0 f  0 f  0 " +
                "f  0 f 1 f  0 f  0 " +
                "f  0 f  0 f 1 f  0 " +
                "f  0 f  0 f  0 f  1"
        Log.d(call)
        Shell.su(call).exec()
    }

    private fun update() {
        val profile = activeProfile
        
        if (profile.monochrome) {
            // Apply grayscale matrix first, then color temperature
            val color = profile.multFilterColor
            val r = Color.red(color) / 255.0f
            val g = Color.green(color) / 255.0f
            val b = Color.blue(color) / 255.0f
            
            // Grayscale transformation using luminosity method
            // Matrix format is column-major: each set of 4 values is a column
            // For grayscale: R_out = 0.299*R + 0.587*G + 0.114*B, then multiply by color
            // Column 0 (R input contribution): affects all output channels weighted by color
            // Column 1 (G input contribution): affects all output channels weighted by color  
            // Column 2 (B input contribution): affects all output channels weighted by color
            // Column 3: offset (all zeros)
            val call = "service call SurfaceFlinger 1015 i32 1 " +
                    "f ${0.299f * r} f ${0.299f * g} f ${0.299f * b} f 0 " +
                    "f ${0.587f * r} f ${0.587f * g} f ${0.587f * b} f 0 " +
                    "f ${0.114f * r} f ${0.114f * g} f ${0.114f * b} f 0 " +
                    "f 0 f 0 f 0 f 1"
            Log.i("Set to monochrome with color temp: $r $g $b")
            Log.d(call)
            Shell.su(call).exec()
        } else {
            // Normal color temperature only
            val color = profile.multFilterColor
            val r = Color.red(color) / 255.0f
            val g = Color.green(color) / 255.0f
            val b = Color.blue(color) / 255.0f
            Log.i("Set to $r $g $b")
            val call = "service call SurfaceFlinger 1015 i32 1 " +
                    "f $r f  0 f  0 f  0 " +
                    "f  0 f $g f  0 f  0 " +
                    "f  0 f  0 f $b f  0 " +
                    "f  0 f  0 f  0 f  1"
            Log.d(call)
            Shell.su(call).exec()
        }
    }

    override fun onCreate() {

    }

    override fun onDestroy() {

    }

    override var profile = activeProfile.off
    set(value) {
        Log.i("profile set to: $value")
        field = value
        filtering = !value.isOff
    }

    companion object : Logger()
}
