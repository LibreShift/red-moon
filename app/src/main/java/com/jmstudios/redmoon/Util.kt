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
package com.jmstudios.redmoon

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.preference.Preference
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat

import com.jmstudios.redmoon.helper.KLog
import com.jmstudios.redmoon.helper.KLogging

import java.util.Calendar

import kotlin.reflect.KClass

val appContext = RedMoonApplication.app

var activeProfile: Profile
    get() = EventBus.getSticky(Profile::class) ?: with (Config) {
                Profile(color, intensity, dimLevel, lowerBrightness)
            }
    set(value) = value.let {
        if (it != EventBus.getSticky(Profile::class)) with (Config) {
            val Log = KLogging.logger("Util")
            Log.i("activeProfile set to $it")
            EventBus.postSticky(it)
            color = it.color
            intensity = it.intensity
            dimLevel = it.dimLevel
            lowerBrightness = it.lowerBrightness
        }
    }

var filterIsOn: Boolean = false
    set(value) {
        field = value
        Config.filterIsOn = value
    }

fun isAccessibilityServiceOn(context: Context?): Boolean {
    val prefString = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return prefString != null && prefString.contains("com.jmstudios.redmoon.service.AccessibilityFilterService")
}

fun inActivePeriod(Log: KLog? = null): Boolean {
    val now = Calendar.getInstance()

    val onTime = Config.scheduledStartTime
    val onHour = onTime.substringBefore(':').toInt()
    val onMinute = onTime.substringAfter(':').toInt()
    val on = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, onHour)
        set(Calendar.MINUTE, onMinute)
        if (after(now)) {
            add(Calendar.DATE, -1)
        }
    }

    val offTime = Config.scheduledStopTime
    val offHour = offTime.substringBefore(':').toInt()
    val offMinute = offTime.substringAfter(':').toInt()
    val off = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, offHour)
        set(Calendar.MINUTE, offMinute)
        while (before(on)) {
            add(Calendar.DATE, 1)
        }
    }

    Log?.d("Start: $onTime, stop: $offTime")
    Log?.d("On DAY_OF_MONTH: ${on.get(Calendar.DAY_OF_MONTH)}")
    Log?.d("Off DAY_OF_MONTH: ${off.get(Calendar.DAY_OF_MONTH)}")

    return now.after(on) && now.before(off)
}

fun getString(resId: Int): String = appContext.getString(resId)
fun getColor (resId: Int): Int = ContextCompat.getColor(appContext, resId)

fun atLeastAPI(api: Int): Boolean = android.os.Build.VERSION.SDK_INT >= api
fun belowAPI  (api: Int): Boolean = android.os.Build.VERSION.SDK_INT <  api

fun intent() = Intent()
fun <T: Any>intent(kc: KClass<T>) = Intent(appContext, kc.java)

fun PreferenceFragmentCompat.pref(resId: Int): Preference? {
    return preferenceScreen.findPreference(getString(resId))
}
