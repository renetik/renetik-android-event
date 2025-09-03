@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event

import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.CSEnvironment.isDebug
import renetik.android.core.logging.CSLog.logError
import renetik.android.core.logging.CSLog.logErrorTrace
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistrationImpl
import renetik.android.event.util.CSLater.onMain
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

class CSEventImpl<T> : CSEvent<T> {
    private var onMainParent: CSHasDestruct? = null
    fun onMain(parent: CSHasDestruct) = apply { onMainParent = parent }
    private val listeners = CopyOnWriteArrayList<CSEventListener<T>>()

    @Volatile
    private var paused = false
    private val firing = AtomicBoolean(false)

    override fun listen(function: (T) -> Unit): CSRegistration =
        EventListenerImpl(function).also(listeners::add)

    override fun fire(argument: T) {
        if (paused) return
        if (!firing.compareAndSet(false, true)) {
            logErrorTrace { "Event fired while firing" }
            return
        }
        try {
            listeners.forEach { listener ->
                if (listener.isActive)
                    onMainParent?.onMain { listener.fire(argument) }
                        ?: run { listener.fire(argument) }
            }
        } finally {
            firing.set(false)
        }
    }

    private inline fun CSEventListener<T>.fire(argument: T) {
        if (isDebug) this(argument)
        else runCatching { this(argument) }.onFailure(::logError)
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
