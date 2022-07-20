package renetik.android.event

import renetik.android.core.kotlin.collections.hasItems
import renetik.android.core.kotlin.collections.list
import renetik.android.core.logging.CSLog.logError
import renetik.android.core.logging.CSLog.logWarn
import renetik.android.core.logging.CSLogMessage.Companion.traceMessage
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistrationImpl

class CSEventImpl<T> : CSEvent<T> {
    private val listeners = list<CSEventListener<T>>()
    private var toRemove = list<CSEventListener<T>>()
    private var toAdd = list<CSEventListener<T>>()
    private var firing = false
    private var paused = false

    override fun listen(function: (T) -> Unit): CSRegistration {
        val listener = EventListenerImpl(function)
        if (firing) toAdd.add(listener)
        else listeners.add(listener)
        return listener
    }

    override fun fire(argument: T) {
        if (paused) return
        if (firing) logError { traceMessage("Event fired while firing") }
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

    override fun clear() = listeners.clear()

    override val isListened get() = listeners.hasItems

    inner class EventListenerImpl(
        private val listener: (T) -> Unit)
        : CSEventListener<T>, CSRegistrationImpl() {

        override fun invoke(argument: T) {
            if (isActive) listener(argument)
        }

        override fun cancel() {
            if (isCanceled) {
                logWarn { traceMessage("Already canceled:$this") }
                return
            }
            super.cancel()
            remove(this)
        }
    }

    fun remove(listener: CSEventListener<T>) {
        val index = listeners.indexOf(listener)
        if (index >= 0) {
            if (firing) toRemove.add(listener)
            else listeners.removeAt(index)
        } else logWarn { traceMessage("Listener not found") }
    }

    override fun pause() {
        paused = true
    }

    override fun resume() {
        paused = false
    }
}
