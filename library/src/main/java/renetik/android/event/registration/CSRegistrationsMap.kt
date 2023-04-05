package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.java.lang.nanoTime
import renetik.android.core.kotlin.collections.removeValue
import renetik.android.core.lang.variable.CSVariable.Companion.variable
import renetik.android.core.logging.CSLog.logWarn
import renetik.android.core.logging.CSLogMessage.Companion.traceMessage

class CSRegistrationsMap(
    private val parent: Any
) : CSRegistrations, CSHasRegistrations {

    override val registrations: CSRegistrationsMap = this
    override var isActive: Boolean by variable(true, ::onActiveChange)
    override var isCanceled: Boolean = false

    private val registrationMap: MutableMap<String, CSRegistration> = mutableMapOf()
    private var idCount: Int = 0
    private fun createUniqueId() = "$idCount: $nanoTime".also { idCount++ }

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
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
        isCanceled = true
        registrationMap.onEach { it.value.cancel() }.clear()
    }

    @Synchronized
    @AnyThread
    fun register(key: String, registration: CSRegistration?): CSRegistration? {
        if (isCanceled) logWarn { traceMessage("Already canceled:$this") }
        remove(key)
        return registration?.let { add(key, it) }
    }

    @Synchronized
    @AnyThread
    override fun register(
        replace: CSRegistration?,
        registration: CSRegistration?
    ): CSRegistration? {
        if (isCanceled) logWarn { traceMessage("Already canceled:$this") }
        replace?.let { cancel(it) }
        return registration?.let { add(createUniqueId(), it) }
    }

    @Synchronized
    @AnyThread
    override fun register(registration: CSRegistration): CSRegistration =
        add(createUniqueId(), registration)

    @Synchronized
    @AnyThread
    private fun add(key: String, registration: CSRegistration): CSRegistration {
        if (isCanceled) logWarn { traceMessage("Already canceled:$this") }
        if (registration.isCanceled) return registration
        if (isCanceled) return registration.also { it.cancel() }
        registrationMap[key] = registration
        return registration
    }

    @Synchronized
    @AnyThread
    fun cancel(key: String) {
        if (isCanceled) logWarn { traceMessage("Already canceled:$this") }
        remove(key)
    }

    private fun remove(key: String) = registrationMap.remove(key)?.cancel()

    @Synchronized
    @AnyThread
    override fun cancel(registration: CSRegistration) {
        val wasPresent = registrationMap.removeValue(registration)
        if (registration.isCanceled && !wasPresent) return
        if (!wasPresent)
            logWarn { traceMessage("Registration not found:$registration") }
        if (registration.isCanceled) {
            logWarn { traceMessage("Registration already canceled but was present:$registration") }
            return
        }
        registration.cancel()
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
    }

    val size: Int get() = registrationMap.size

    override fun toString(): String =
        "${super.toString()} parent:$parent size:$size isActive:$isActive isCanceled:$isCanceled"
}