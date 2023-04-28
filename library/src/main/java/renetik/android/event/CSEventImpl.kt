package renetik.android.event

import renetik.android.core.kotlin.collections.hasItems
import renetik.android.core.kotlin.collections.list
import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.void
import renetik.android.core.logging.CSLog.logError
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistrationImpl

class CSEventImpl<T> : CSEvent<T> {
    private val listeners = list<CSEventListener<T>>()
    private var toRemove = list<CSEventListener<T>>()
    private var toAdd = list<CSEventListener<T>>()
    private var firing = false
    private var paused = false

    @Synchronized
    override fun listen(function: (T) -> Unit): CSRegistration {
        val listener = EventListenerImpl(function)
        if (firing) toAdd.add(listener)
        else listeners.add(listener)
        return listener
    }

    @Synchronized
    override fun fire(argument: T) {
        if (paused) return
        if (firing) logWarnTrace { "Event fired while firing" }
        if (listeners.isEmpty()) return

        firing = true
        for (listener in listeners)
            if (!paused) listener.invoke(argument)
        firing = false

        if (toRemove.hasItems) {
            listeners.removeAll(toRemove)
            toRemove.clear()
        }
        if (toAdd.hasItems) {
            listeners.addAll(toAdd)
            toAdd.clear()
        }
    }

    @Synchronized
    override fun clear() {
        if (firing) logError { "firing" }
        listeners.clear()
    }

    override val isListened get() = listeners.hasItems

    inner class EventListenerImpl(
        private val listener: (T) -> Unit,
    ) : CSEventListener<T>, CSRegistrationImpl(isActive = true) {

        override fun invoke(argument: T) = isActive.isTrue { listener(argument) }

        override fun onCancel() {
            super.onCancel()
            synchronized(this@CSEventImpl) {
                val index = listeners.indexOf(this)
                if (index >= 0) {
                    if (firing) toRemove.add(this) else listeners.removeAt(index)
                } else logWarnTrace {
                    "${this::class} listener:${listener::class} not found"
                }
            }
        }

        override fun toString() = "${super.toString()} listener:${listener::class}"
    }

    @Synchronized
    override fun pause() {
        paused = true
    }

    @Synchronized
    override fun resume() {
        paused = false
    }

    override fun onChange(function: (T) -> void): CSRegistration =
        listen(function)
}
