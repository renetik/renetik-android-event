package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.kotlin.notImplemented
import renetik.android.core.lang.variable.CSVariable.Companion.variable
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire

class CSRegistrationsList(parent: Any) : CSRegistrations {
    private val id = "$parent"
    private val registrationList = mutableListOf<CSRegistration>()

    @get:Synchronized
    override var isActive by variable(true, ::onActiveChange)
        private set

    @get:Synchronized
    override var isCanceled: Boolean = false
        private set

    override val eventCancel = event()

    @Synchronized
    @AnyThread
    override fun resume() {
        if (isCanceled) {
            logWarnTrace { "Already canceled:$this" }
            return
        }
        if (!isActive) isActive = true
    }

    @Synchronized
    @AnyThread
    override fun pause() {
        if (isCanceled) {
            logWarnTrace { "Already canceled:$this" }
            return
        }
        if (isActive) isActive = false
    }

    @Synchronized
    private fun onActiveChange(isActive: Boolean) {
        if (isCanceled) {
            logWarnTrace { "Already canceled:$id" }
            return
        }
        registrationList.forEach { it.setActive(isActive) }
    }

    @Synchronized
    @AnyThread
    override fun cancel() {
        if (isCanceled) {
            logWarnTrace { "Already canceled:$id" }
            return
        }
        isCanceled = true
        clear()
        eventCancel.fire()
    }

    @Synchronized
    @AnyThread
    override fun register(
        replace: CSRegistration?,
        registration: CSRegistration?,
    ): CSRegistration? {
        replace?.cancel()
        if (isCanceled) logWarnTrace { "Already canceled:$id" }
        return registration?.let { add(it) }
    }

    override fun register(key: String, registration: CSRegistration?): CSRegistration? {
        notImplemented()
    }

    @Synchronized
    @AnyThread
    override fun register(registration: CSRegistration): CSRegistration =
        add(registration)

    @Synchronized
    @AnyThread
    private fun add(registration: CSRegistration): CSRegistration {
        if (isCanceled) logWarnTrace { "Already canceled:$id" }
        if (registration.isCanceled) return registration
        if (isCanceled) return registration.also { it.cancel() }
        registrationList.add(registration)
        return object : CSRegistration {
            override val isActive: Boolean get() = registration.isActive
            override val eventCancel = registration.eventCancel
            override val isCanceled: Boolean get() = registration.isCanceled
            override fun resume() = registration.resume()
            override fun pause() = registration.pause()
            override fun cancel() {
                cancel(registration)
                eventCancel.fire()
            }
        }
    }

    @Synchronized
    @AnyThread
    private fun cancel(registration: CSRegistration) {
        val wasPresent = registrationList.remove(registration)
        if (registration.isCanceled && !wasPresent) return
        if (!wasPresent) logWarnTrace { "Registration not found:$registration" }
        if (registration.isCanceled) return
        registration.cancel()
        if (isCanceled) logWarnTrace { "Already canceled:$this" }
    }

    fun clear() = registrationList.cancelRegistrations()

    val size: Int get() = registrationList.size
}