/*
 * Copyright (c) 2016 Marien Raat <marienraat@riseup.net>
 * Copyright (c) 2017 Stephen Michel <s@smichel.me>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.jmstudios.redmoon.model

import android.graphics.Color

import com.jmstudios.redmoon.R

import com.jmstudios.redmoon.util.EventBus
import com.jmstudios.redmoon.util.getString

import org.json.JSONObject

private const val KEY_COLOR     = "color"
private const val KEY_INTENSITY = "intensity"
private const val KEY_DIM_LEVEL = "dim"
private const val KEY_LOWER_BRIGHTNESS = "lower-brightness"

/**
 * Color, intensity, and dimLevel range from 0 to 100, inclusive. Regardless of
 *                     value, the filter is guaranteed to never be fully opaque.
 *
 * color: 0 is 500k; 100 is 3500k
 *
 * dimLevel: 0 doesn't darken; 100 is the maximum the system allows.
 *
 * intensity: 0 doesn't color the filter; 100 is the maximum the system allows.
 */
data class Profile(
        val color: Int,
        val intensity: Int,
        val dimLevel: Int,
        val lowerBrightness: Boolean)
    : EventBus.Event, Comparable<Profile> {

    override fun toString() = JSONObject().run {
        put(KEY_COLOR, color    )
        put(KEY_INTENSITY, intensity)
        put(KEY_DIM_LEVEL, dimLevel )
        put(KEY_LOWER_BRIGHTNESS, lowerBrightness)
        toString()
    }

    override operator fun compareTo(other: Profile): Int {
        return compareValuesBy(this, other) { Color.alpha(it.filterColor) }
    }

    val name: String
        get() = ProfilesModel.nameOf(this) ?: getString(R.string.filter_name_custom)

    val multFilterColor: Int
        get() {
            val rgbColor = rgbFromColor(color)
            val intensityColor = intensity / 100.0f
            val dim = dimLevel / 100.0f
            return Color.argb(255,
                    Math.round((Color.red(rgbColor) * intensityColor + 255.0f * (1.0f - intensityColor)) * (1.0f - dim)),
                    Math.round((Color.green(rgbColor) * intensityColor + 255.0f * (1.0f - intensityColor)) * (1.0f - dim)),
                    Math.round((Color.blue(rgbColor) * intensityColor + 255.0f * (1.0f - intensityColor)) * (1.0f - dim)))
        }

    val filterColor: Int
        get() {
            val rgbColor = rgbFromColor(color)
            val intensityColor = Color.argb(floatToColorBits(intensity.toFloat() / 100.0f),
                                            Color.red  (rgbColor),
                                            Color.green(rgbColor),
                                            Color.blue (rgbColor))
            val dimColor = Color.argb(floatToColorBits(dimLevel.toFloat() / 100.0f), 0, 0, 0)
            return addColors(dimColor, intensityColor)
        }

    // lazy initialization prevents an infinite loop on init -> stack overflow
    val off: Profile by lazy { this.copy(intensity = 0, dimLevel = 0) }

    val isOff: Boolean = intensity == 0 && dimLevel == 0

    private fun addColors(color1: Int, color2: Int): Int {
        var alpha1 = colorBitsToFloat(Color.alpha(color1))
        var alpha2 = colorBitsToFloat(Color.alpha(color2))
        val red1 = colorBitsToFloat(Color.red(color1))
        val red2 = colorBitsToFloat(Color.red(color2))
        val green1 = colorBitsToFloat(Color.green(color1))
        val green2 = colorBitsToFloat(Color.green(color2))
        val blue1 = colorBitsToFloat(Color.blue(color1))
        val blue2 = colorBitsToFloat(Color.blue(color2))

        // See: http://stackoverflow.com/a/10782314

        // Alpha changed to allow more control
        val fAlpha = alpha2 * INTENSITY_MAX_ALPHA + (DIM_MAX_ALPHA - alpha2 * INTENSITY_MAX_ALPHA) * alpha1
        alpha1 *= ALPHA_ADD_MULTIPLIER
        alpha2 *= ALPHA_ADD_MULTIPLIER

        val alpha = floatToColorBits(fAlpha)
        val red   = floatToColorBits((red1   * alpha1 + red2   * alpha2 * (1.0f - alpha1)) / fAlpha)
        val green = floatToColorBits((green1 * alpha1 + green2 * alpha2 * (1.0f - alpha1)) / fAlpha)
        val blue  = floatToColorBits((blue1  * alpha1 + blue2  * alpha2 * (1.0f - alpha1)) / fAlpha)

        return Color.argb(alpha, red, green, blue)
    }

    companion object {
        const val DIM_MAX_ALPHA = 0.9f
        private const val INTENSITY_MAX_ALPHA  = 0.75f
        private const val ALPHA_ADD_MULTIPLIER = 0.75f

        private fun colorBitsToFloat(bits:  Int): Float = bits.toFloat() / 255.0f
        private fun floatToColorBits(color: Float): Int = (color * 255.0f).toInt()

        internal fun parse(entry: String): Profile = JSONObject(entry).run {
            val color     = optInt(KEY_COLOR)
            val intensity = optInt(KEY_INTENSITY)
            val dim       = optInt(KEY_DIM_LEVEL)
            val lowerBrightness = optBoolean(KEY_LOWER_BRIGHTNESS)
            Profile(color, intensity, dim, lowerBrightness)
        }

        fun getColorTemperature(color: Int): Int = 500 + color * 30

        fun rgbFromColor(color: Int): Int {
            fun Double.truncate(): Int = when {
                this < 0 -> 0
                this > 255 -> 255
                else -> this.toInt()
            }
            val colorTemperature = getColorTemperature(color)
            val alpha = 255 // alpha is managed separately

            // After: http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
            val temp = colorTemperature.toDouble() / 100.0f

            val red: Double = if (temp <= 66) 255.0 else {
                329.698727446 * Math.pow(temp - 60, -0.1332047592)
            }

            val green: Double = if (temp <= 66) {
                99.4708025861 * Math.log(temp) - 161.1195681661
            } else {
                288.1221695283 * Math.pow(temp - 60, -0.0755148492)
            }

            val blue: Double = when {
                temp >= 66 -> 255.0
                temp < 19 -> 0.0
                else -> 138.5177312231 * Math.log(temp - 10) - 305.0447927307
            }

            return Color.argb(alpha, red.truncate(), green.truncate(), blue.truncate())
        }
    }
}
