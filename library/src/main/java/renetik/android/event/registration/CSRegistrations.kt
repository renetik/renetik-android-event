package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.kotlin.collections.removeValue
import renetik.android.core.logging.CSLog.logWarn
import renetik.android.core.logging.CSLogMessage.Companion.traceMessage
import java.lang.System.nanoTime

class CSRegistrations {
    private val registrations: MutableMap<String, CSRegistration> = mutableMapOf()
    var isCanceled = false

    private var idCount = 0
    private fun createUniqId() = "$idCount: ${nanoTime()}".also { idCount++ }

    @Synchronized
    @AnyThread
    fun cancel() {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
        isCanceled = true
        registrations.onEach { it.value.cancel() }.clear()
    }

    @Synchronized
    @AnyThread
    fun register(registration: CSRegistration): CSRegistration {
        if (isCanceled) logWarn { traceMessage("Already canceled:$this") }
        if (registration.isCanceled) return registration
        if (isCanceled) return registration.also { it.cancel() }
        registrations[createUniqId()] = registration
        return registration
    }

    @Synchronized
    @AnyThread
    fun register(replace: CSRegistration?, registration: CSRegistration): CSRegistration {
        replace?.let { cancel(it) }
        return register(registration)
    }

    @Synchronized
    @AnyThread
    fun register(key: String, registration: CSRegistration?): CSRegistration? {
        if (isCanceled) logWarn { traceMessage("Already canceled:$this") }
        remove(key)
        if (registration == null) return null
        if (registration.isCanceled) return registration
        if (isCanceled) return registration.also { it.cancel() }
        registrations[key] = registration
        return registration
    }

    @Synchronized
    @AnyThread
    fun cancel(key: String) {
        if (isCanceled) logWarn { traceMessage("Already canceled:$this") }
        remove(key)
    }

    private fun remove(key: String) = registrations.remove(key)?.cancel()

    @Synchronized
    @AnyThread
    fun cancel(registration: CSRegistration) {
        if (registration.isCanceled) {
            logWarn { traceMessage("Registration already canceled:$registration") }
            registrations.removeValue(registration)
            return
        }
        registration.cancel()
        if (!registrations.removeValue(registration)) logWarn {
            traceMessage("Registration not found")
        }
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
    }

    @Synchronized
    @AnyThread
    fun setActive(active: Boolean) {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
        registrations.forEach { it.value.setActive(active) }
    }

    val size: Int get() = registrations.size
}