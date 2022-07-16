package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.kotlin.collections.removeIf
import renetik.android.core.logging.CSLog.logWarn
import java.lang.System.nanoTime

class CSRegistrations {
    val registrations: MutableMap<Any, CSRegistration> = mutableMapOf()
    var isCanceled = false

    private var idCount = 0
    private fun createUniqId() = "$idCount: ${nanoTime()}".also { idCount++ }

    @Synchronized
    @AnyThread
    fun cancel() {
        if (isCanceled) {
            logWarn("Already canceled:$this")
            return
        }
        isCanceled = true
        registrations.onEach { it.value.cancel() }.clear()
    }

    @Synchronized
    @AnyThread
    fun add(registration: CSRegistration): CSRegistration {
        if (isCanceled) logWarn("Already canceled:$this")
        if (registration.isCanceled) return registration
        if (isCanceled) return registration.also { it.cancel() }
        registrations[createUniqId()] = registration
        return registration
    }

    @Synchronized
    @AnyThread
    fun add(key: Any, registration: CSRegistration): CSRegistration {
        if (isCanceled) logWarn("Already canceled:$this")
        if (registration.isCanceled) return registration
        if (isCanceled) return registration.also { it.cancel() }
        registrations[key]?.cancel()
        registrations[key] = registration
        return registration
    }

    @Synchronized
    @AnyThread
    fun cancel(registration: CSRegistration) {
        if (isCanceled) {
            logWarn("Already canceled:$this")
            return
        }
        registration.cancel()
        if (!registrations.removeIf { _, value -> value == registration })
            logWarn("Registration not found")
    }

    @Synchronized
    @AnyThread
    fun setActive(active: Boolean) {
        if (isCanceled) {
            logWarn("Already canceled:$this")
            return
        }
        registrations.forEach { it.value.setActive(active) }
    }
}