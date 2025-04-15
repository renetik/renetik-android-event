package renetik.android.event

import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.atomic.CSAtomic.Companion.atomic
import renetik.android.core.logging.CSLog.logDebugTrace
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistrationImpl
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

class CSEventImpl<T> : CSEvent<T> {
    private val listeners = CopyOnWriteArrayList<CSEventListener<T>>()

    @Volatile
    private var paused = false
    private val firing = AtomicBoolean(false)

    override fun listen(function: (T) -> Unit): CSRegistration =
        EventListenerImpl(function).also(listeners::add)

    override fun fire(argument: T) {
        if (paused) return
        if (!firing.compareAndSet(false, true)) {
            logDebugTrace { "Event fired while firing" }
            return
        }
        try {
            listeners.forEach { listener ->
                if (listener.isActive) listener.invoke(argument)
            }
        } finally {
            firing.set(false)
        }
    }

    override fun clear() = listeners.clear()

    override val isListened get() = listeners.isNotEmpty()

    override fun pause() {
        paused = true
    }

    override fun resume() {
        paused = false
    }

    override fun onChange(function: (T) -> Unit): CSRegistration = listen(function)

    inner class EventListenerImpl(
        private val listener: (T) -> Unit
    ) : CSEventListener<T>, CSRegistrationImpl(isActive = true) {

        override fun invoke(argument: T) = isActive.isTrue { listener(argument) }

        override fun onCancel() {
            super.onCancel()
            listeners.remove(this)
        }

        override fun toString() = "${super.toString()} listener:${listener::class}"
    }
}
