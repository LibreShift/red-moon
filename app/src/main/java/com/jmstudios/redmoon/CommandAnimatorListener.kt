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

import android.animation.Animator
import android.app.Service
import com.jmstudios.redmoon.service.AccessibilityFilterService
import com.jmstudios.redmoon.service.FilterService

class CommandAnimatorListener(
    private val cmd: Command,
    private val svc: FilterService,
)
    : Animator.AnimatorListener {

    override fun onAnimationStart (a: Animator?) = cmd.onAnimationStart (svc)
    override fun onAnimationEnd   (a: Animator?) = cmd.onAnimationEnd   (svc)
    override fun onAnimationCancel(a: Animator?) = cmd.onAnimationCancel(svc)
    override fun onAnimationRepeat(a: Animator?) = cmd.onAnimationRepeat(svc)
}
