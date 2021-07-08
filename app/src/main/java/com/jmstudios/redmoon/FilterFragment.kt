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

import android.os.Bundle
import androidx.preference.TwoStatePreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.Preference

import com.jmstudios.redmoon.R

import com.jmstudios.redmoon.model.Profile
import com.jmstudios.redmoon.model.Config
import com.jmstudios.redmoon.preference.SeekBarPreference
import com.jmstudios.redmoon.util.*

import org.greenrobot.eventbus.Subscribe

class FilterFragment : PreferenceFragmentCompat() {
    //private var hasShownWarningToast = false
    companion object : Logger()

    // Preferences
    private val profileSelectorPref: Preference
        get() = pref(R.string.pref_key_profile_spinner)!!

    private val colorPref: SeekBarPreference
        get() = pref(R.string.pref_key_color) as SeekBarPreference

    private val intensityPref: SeekBarPreference
        get() = pref(R.string.pref_key_intensity) as SeekBarPreference

    private val dimLevelPref: SeekBarPreference
        get()= pref(R.string.pref_key_dim) as SeekBarPreference

    private val lowerBrightnessPref: TwoStatePreference
        get() = pref(R.string.pref_key_lower_brightness) as TwoStatePreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.filter_preferences, rootKey)

        if (!Permission.WriteSettings.isGranted) {
            lowerBrightnessPref.isChecked = false
        }

        lowerBrightnessPref.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val checked = newValue as Boolean
                    if (checked) Permission.WriteSettings.request(requireActivity()) else true
                }
    }

    override fun onStart() {
        Log.i("onStart")
        super.onStart()
        preferenceScreen.setEnabled(Permission.Overlay.isGranted || Config.useRoot)
        EventBus.register(profileSelectorPref)
        EventBus.register(this)
    }

    override fun onStop() {
        EventBus.unregister(this)
        EventBus.unregister(profileSelectorPref)
        super.onStop()
    }

    //region presenter
    @Subscribe fun onProfileChanged(profile: Profile) {
        profile.run {
            colorPref.setProgress(color)
            intensityPref.setProgress(intensity)
            dimLevelPref.setProgress(dimLevel)
            lowerBrightnessPref.isChecked = lowerBrightness
        }
    }
    //endregion
}
