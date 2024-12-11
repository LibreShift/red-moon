/*
 * Copyright (c) 2017  Stephen Michel <s@smichel.me>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.jmstudios.redmoon

import kotlin.reflect.KClass

import org.greenrobot.eventbus.EventBus

object EventBus {
    private val bus: EventBus
        get() = EventBus.getDefault()

    fun register  (subscriber: Any) = bus.register  (subscriber)
    fun unregister(subscriber: Any) = bus.unregister(subscriber)

    fun post        (event: Event) = bus.post             (event)
    fun postSticky  (event: Event) = bus.postSticky       (event)
    fun removeSticky(event: Event) = bus.removeStickyEvent(event)

    fun <T: Event>getSticky(eventClass: KClass<T>): T? = bus.getStickyEvent(eventClass.java)
}

interface Event

class filterIsOnChanged        : Event
//class themeChanged             : Event
class profilesUpdated          : Event
class scheduleChanged          : Event
class useLocationChanged       : Event
class locationChanged          : Event
class secureSuspendChanged     : Event
class buttonBacklightChanged   : Event

class overlayPermissionDenied  : Event
class locationAccessDenied     : Event
class changeBrightnessDenied   : Event

class accessibilityServiceCommand(val command: Command) : Event

data class locationService(val isSearching: Boolean, val isRunning: Boolean = true) : Event
