/*
 * Copyright (c) 2016 Marien Raat <marienraat@riseup.net>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.jmstudios.redmoon

import android.os.Bundle
import androidx.fragment.app.Fragment

import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment

import com.jmstudios.redmoon.R
import com.jmstudios.redmoon.model.Config
import com.topjohnwu.superuser.Shell

class Intro : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showStatusBar(false)
        showSkipButton(false)

        addSlide(AppIntroFragment.newInstance(getString(R.string.slide_title_welcome),
                getString(R.string.slide_text_welcome),
                R.drawable.intro_slide_1,
                0xFFD32F2F.toInt()))

        addSlide(AppIntroFragment.newInstance(getString(R.string.slide_title_protect_eyes),
                getString(R.string.slide_text_protect_eyes),
                R.drawable.intro_slide_2,
                0xFF2196F3.toInt()))

        addSlide(AppIntroFragment.newInstance(getString(R.string.slide_title_improve_sleep),
                getString(R.string.slide_text_improve_sleep),
                R.drawable.intro_slide_3,
                0xFF388E3C.toInt()))

        addSlide(AppIntroFragment.newInstance(getString(R.string.slide_title_get_started),
                getString(R.string.slide_text_get_started),
                R.drawable.intro_slide_4,
                0xFFFFB300.toInt()))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        saveAndFinish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        Config.introShown = true
        saveAndFinish()
    }

    private fun saveAndFinish() {
        Config.introShown = true
        Shell.rootAccess()
        finish()
    }
}
