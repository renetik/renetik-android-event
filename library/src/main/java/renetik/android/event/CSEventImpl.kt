package renetik.android.event

import renetik.android.core.kotlin.collections.hasItems
import renetik.android.core.kotlin.collections.list
import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.CSList
import renetik.android.core.logging.CSLog.logDebugTrace
import renetik.android.core.logging.CSLog.logError
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistrationImpl

class CSEventImpl<T> : CSEvent<T> {
    private val listeners by lazy<CSList<CSEventListener<T>>>(::list)
    private val toRemove by lazy<CSList<CSEventListener<T>>>(::list)
    private val toAdd by lazy<CSList<CSEventListener<T>>>(::list)
    private var firing = false
    private var paused = false

    @Synchronized
    override fun listen(function: (T) -> Unit): CSRegistration {
        val listener = EventListenerImpl(function)
        if (firing) toAdd.add(listener) else listeners.add(listener)
        return listener
    }

    @Synchronized
    override fun fire(argument: T) {
        if (firing) {
            logDebugTrace { "Event fired while firing" }
            return
        }
        if (paused || listeners.isEmpty()) return

        firing = true
        for (listener in listeners)
            if (!paused && listener.isActive)
                listener.invoke(argument)
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

    @get:Synchronized
    override val isListened get() = listeners.hasItems

    inner class EventListenerImpl(
        private val listener: (T) -> Unit,
    ) : CSEventListener<T>, CSRegistrationImpl(isActive = true) {

        override fun invoke(argument: T) = isActive.isTrue { listener(argument) }

        override fun onCancel() {
            super.onCancel()
            synchronized(this@CSEventImpl) {
                if (this in listeners) {
                    if (firing) toRemove.add(this)
                    else listeners.remove(this)
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

    override fun onChange(function: (T) -> Unit): CSRegistration =
        listen(function)
}
