package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.lang.variable.CSVariable.Companion.variable
import renetik.android.core.logging.CSLog.logWarn
import renetik.android.core.logging.CSLogMessage.Companion.traceMessage

class CSRegistrationsList(parent: Any) : CSRegistrations, CSHasRegistrations {
    private val id by lazy { "$parent" }
    override val registrations: CSRegistrations = this
    private val registrationList = mutableListOf<CSRegistration>()
    override var isActive by variable(true) { onActiveChange(it) }
    override var isCanceled = false

    private fun onActiveChange(isActive: Boolean) {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$id") }
            return
        }
        registrationList.forEach { it.setActive(isActive) }
    }

    @Synchronized
    @AnyThread
    override fun resume() {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
        if (isPaused) isActive = true
        else logWarn { traceMessage("Already resume:$this") }
    }

    @Synchronized
    @AnyThread
    override fun pause() {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
        if (isActive) isActive = false
        else logWarn { traceMessage("Already pause:$this") }
    }

    @Synchronized
    @AnyThread
    override fun cancel() {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$id") }
            return
        }
        isCanceled = true
        registrationList.onEach { it.cancel() }.clear()
    }

    @Synchronized
    @AnyThread
    override fun register(replace: CSRegistration?,
                          registration: CSRegistration?): CSRegistration? {
        if (isCanceled) logWarn { traceMessage("Already canceled:$id") }
        replace?.let { cancel(it) }
        return registration?.let { add(it) }
    }

    @Synchronized
    @AnyThread
    override fun register(registration: CSRegistration): CSRegistration = add(registration)

    @Synchronized
    @AnyThread
    private fun add(registration: CSRegistration): CSRegistration {
        if (isCanceled) logWarn { traceMessage("Already canceled:$id") }
        if (registration.isCanceled) return registration
        if (isCanceled) return registration.also { it.cancel() }
        registrationList.add(registration)
        return registration
    }

    @Synchronized
    @AnyThread
    override fun cancel(registration: CSRegistration) {
        if (registration.isCanceled) {
            logWarn { traceMessage("Registration already canceled:$registration") }
            registrationList.remove(registration)
            return
        }
        registration.cancel()
        if (!registrationList.remove(registration)) logWarn {
            traceMessage("Registration not found")
        }
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$id") }
            return
        }
    }

    val size: Int get() = registrationList.size
}