package renetik.android.event

import renetik.android.core.kotlin.collections.hasItems
import renetik.android.core.kotlin.collections.list
import renetik.android.core.kotlin.exception
import renetik.android.core.logging.CSLog.logError
import renetik.android.event.registration.CSRegistration
import java.io.Closeable

class CSEventImpl<T> : CSEvent<T> {

    private val listeners = list<CSEventListener<T>>()
    private var toRemove = list<CSEventListener<T>>()
    private var toAdd = list<CSEventListener<T>>()
    private var running = false
    private var paused = false

    override fun listen(function: (T) -> Unit): CSRegistration {
        val listener = EventListenerImpl(function)
        if (running) toAdd.add(listener)
        else listeners.add(listener)
        return listener
    }

    override fun fire(argument: T) {
        if (paused) return
        if (running)
            logError(exception("Event run while running"))
        if (listeners.isEmpty()) return
        running = true
        for (listener in listeners) listener.onEvent(argument)
        if (toRemove.hasItems) {
            for (listener in toRemove) listeners.delete(listener)
            toRemove.clear()
        }
        if (toAdd.hasItems) {
            listeners.addAll(toAdd)
            toAdd.clear()
        }
        running = false
    }

    override fun clear() = listeners.clear()

    override val isListened get() = listeners.hasItems

    internal inner class EventListenerImpl(
        private val listener: (T) -> Unit) : CSEventListener<T> {
        private var canceled = false
        override var isActive = true

        override fun cancel() {
            if (canceled) return
            canceled = true
            isActive = false
            remove(this)
        }

        override fun onEvent(argument: T) {
            if (isActive) listener(argument)
        }
    }

    fun remove(listener: CSEventListener<T>) {
        val index = listeners.indexOf(listener)
        if (index >= 0) {
            if (running) toRemove.add(listener)
            else listeners.removeAt(index)
        } else logError(Throwable(), "Listener not found")
    }

    @Deprecated("Just for debugging")
    override val registrations
        get() = listeners

    override fun pause(): Closeable {
        paused = true
        return Closeable { paused = false }
    }

    override fun resume() {
        paused = false
    }
}
