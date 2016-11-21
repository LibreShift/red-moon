/*
 * Copyright (c) 2016  Marien Raat <marienraat@riseup.net>
 *
 *  This file is free software: you may copy, redistribute and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  This file is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 *  details.
 *
 *  You should have received a copy of the GNU General Public License along with
 *  this program.  If not, see <http://www.gnu.org/licenses/>.
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
package com.jmstudios.redmoon.preference

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.preference.Preference
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView

import com.jmstudios.redmoon.R

import com.jmstudios.redmoon.event.moveToState
import com.jmstudios.redmoon.service.ScreenFilterService
import com.jmstudios.redmoon.view.ScreenFilterView

import org.greenrobot.eventbus.EventBus

class DimSeekBarPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    lateinit var mDimLevelSeekBar: SeekBar
    private var mDimLevel: Int = 0
    lateinit private var mView: View

    init {
        layoutResource = R.layout.preference_dim_seekbar
    }

    fun setProgress(progress: Int) {
        mDimLevelSeekBar.progress = progress
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getInteger(index, DEFAULT_VALUE)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            mDimLevel = getPersistedInt(DEFAULT_VALUE)
        } else {
            mDimLevel = (defaultValue as Int?)?: 0
            persistInt(mDimLevel)
        }
    }

    override fun onBindView(view: View) {
        super.onBindView(view)

        mView = view

        mDimLevelSeekBar = view.findViewById(R.id.dim_level_seekbar) as SeekBar
        initLayout()
    }

    private fun initLayout() {
        mDimLevelSeekBar.progress = mDimLevel

        mDimLevelSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mDimLevel = progress
                persistInt(mDimLevel)

                updateMoonIconColor()
                updateProgressText()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                Log.i(TAG, "Touch down on a seek bar")
                val showPreviewCommand = ScreenFilterService.COMMAND_SHOW_PREVIEW
                EventBus.getDefault().postSticky(moveToState(showPreviewCommand))
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Log.d(TAG, "Released a seek bar")
                val hidePreviewCommand = ScreenFilterService.COMMAND_HIDE_PREVIEW
                EventBus.getDefault().postSticky(moveToState(hidePreviewCommand))
            }
        })

        updateMoonIconColor()
        updateProgressText()
    }

    private fun updateMoonIconColor() {
        if (!isEnabled) return

        val lightness = 102 + ((100 - mDimLevel).toFloat() * (2.55f * 0.6f)).toInt()
        val color = Color.rgb(lightness, lightness, lightness)

        val moonIcon = mView.findViewById(R.id.moon_icon_dim) as ImageView

        val colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)

        moonIcon.colorFilter = colorFilter
    }

    private fun updateProgressText() {
        val progress = Integer.toString((mDimLevel.toFloat() * ScreenFilterView.DIM_MAX_ALPHA).toInt())
        val suffix = "%"

        val progressText = mView.findViewById(R.id.current_dim_level) as TextView
        progressText.text = progress + suffix
    }

    companion object {
        val DEFAULT_VALUE = 50
        private val TAG = "DimSeekBarPreference"
    }
}