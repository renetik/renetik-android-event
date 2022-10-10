package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.java.lang.nanoTime
import renetik.android.core.kotlin.collections.removeValue
import renetik.android.core.logging.CSLog.logWarn
import renetik.android.core.logging.CSLogMessage.Companion.traceMessage
import java.lang.System.nanoTime

class CSRegistrations(val parent: Any) {
    private val registrations: MutableMap<String, CSRegistration> = mutableMapOf()
    var isCanceled = false

    private var idCount = 0
    private fun createUniqId() = "$idCount: $nanoTime".also { idCount++ }

    @Synchronized
    @AnyThread
    fun cancel() {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$parent") }
            return
        }
        isCanceled = true
        registrations.onEach { it.value.cancel() }.clear()
    }

    @Synchronized
    @AnyThread
    fun register(key: String, registration: CSRegistration?): CSRegistration? {
        if (isCanceled) logWarn { traceMessage("Already canceled:$parent") }
        remove(key)
        return registration?.let { add(key, it) }
    }

    @Synchronized
    @AnyThread
    fun register(replace: CSRegistration?, registration: CSRegistration?): CSRegistration? {
        if (isCanceled) logWarn { traceMessage("Already canceled:$parent") }
        replace?.let { cancel(it) }
        return registration?.let { add(createUniqId(), it) }
    }

    @Synchronized
    @AnyThread
    fun register(registration: CSRegistration): CSRegistration =
        add(createUniqId(), registration)

    @Synchronized
    @AnyThread
    private fun add(key: String, registration: CSRegistration): CSRegistration {
        if (isCanceled) logWarn { traceMessage("Already canceled:$parent") }
        if (registration.isCanceled) return registration
        if (isCanceled) return registration.also { it.cancel() }
        registrations[key] = registration
        return registration
    }

    @Synchronized
    @AnyThread
    fun cancel(key: String) {
        if (isCanceled) logWarn { traceMessage("Already canceled:$parent") }
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
            logWarn { traceMessage("Already canceled:$parent") }
            return
        }
    }

    @Synchronized
    @AnyThread
    fun setActive(active: Boolean) {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$parent") }
            return
        }
        registrations.forEach { it.value.setActive(active) }
    }

    val size: Int get() = registrations.size
}