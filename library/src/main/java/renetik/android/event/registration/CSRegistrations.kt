package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.java.lang.nanoTime
import renetik.android.core.kotlin.collections.removeValue
import renetik.android.core.lang.variable.CSVariable.Companion.variable
import renetik.android.core.logging.CSLog.logWarn
import renetik.android.core.logging.CSLogMessage.Companion.traceMessage

class CSRegistrations(val parent: Any) : CSRegistration, CSHasRegistrations {
    override val registrations: CSRegistrations = this
    private val registrationMap: MutableMap<String, CSRegistration> = mutableMapOf()
    override var isActive by variable(true, ::onActiveChange)
    override var isCanceled = false
    private var idCount = 0
    private fun createUniqId() = "$idCount: $nanoTime".also { idCount++ }

    private fun onActiveChange(isActive: Boolean) {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$parent") }
            return
        }
        registrationMap.forEach { it.value.setActive(isActive) }
    }

    @Synchronized
    @AnyThread
    override fun resume() {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
        if (isPaused) isActive = true else logWarn { traceMessage("Already resume:$this") }
    }

    @Synchronized
    @AnyThread
    override fun pause() {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
        if (isActive) isActive = false else logWarn { traceMessage("Already pause:$this") }
    }

    @Synchronized
    @AnyThread
    override fun cancel() {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$parent") }
            return
        }
        isCanceled = true
        registrationMap.onEach { it.value.cancel() }.clear()
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
        registrationMap[key] = registration
        return registration
    }

    @Synchronized
    @AnyThread
    fun cancel(key: String) {
        if (isCanceled) logWarn { traceMessage("Already canceled:$parent") }
        remove(key)
    }

    private fun remove(key: String) = registrationMap.remove(key)?.cancel()

    @Synchronized
    @AnyThread
    fun cancel(registration: CSRegistration) {
        if (registration.isCanceled) {
            logWarn { traceMessage("Registration already canceled:$registration") }
            registrationMap.removeValue(registration)
            return
        }
        registration.cancel()
        if (!registrationMap.removeValue(registration)) logWarn {
            traceMessage("Registration not found")
        }
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$parent") }
            return
        }
    }

    val size: Int get() = registrationMap.size
}