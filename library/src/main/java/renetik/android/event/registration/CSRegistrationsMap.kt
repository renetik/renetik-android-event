package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.java.lang.nanoTime
import renetik.android.core.kotlin.collections.removeValue
import renetik.android.core.lang.variable.CSVariable.Companion.variable
import renetik.android.core.logging.CSLog.logWarn
import renetik.android.core.logging.CSLogMessage.Companion.traceMessage

class CSRegistrationsMap(parent: Any) : CSRegistrations, CSHasRegistrations {
    private val id by lazy { "$parent" }
    override val registrations: CSRegistrationsMap = this
    private val registrationMap: MutableMap<String, CSRegistration> = mutableMapOf()
    override var isActive by variable(true, ::onActiveChange)
    override var isCanceled = false
    private var idCount = 0
    private fun createUniqId() = "$idCount: $nanoTime".also { idCount++ }

    private fun onActiveChange(isActive: Boolean) {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$id") }
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
            logWarn { traceMessage("Already canceled:$id") }
            return
        }
        isCanceled = true
        registrationMap.onEach { it.value.cancel() }.clear()
    }

    @Synchronized
    @AnyThread
    fun register(key: String, registration: CSRegistration?): CSRegistration? {
        if (isCanceled) logWarn { traceMessage("Already canceled:$id") }
        remove(key)
        return registration?.let { add(key, it) }
    }

    @Synchronized
    @AnyThread
    override fun register(replace: CSRegistration?,
                          registration: CSRegistration?): CSRegistration? {
        if (isCanceled) logWarn { traceMessage("Already canceled:$id") }
        replace?.let { cancel(it) }
        return registration?.let { add(createUniqId(), it) }
    }

    @Synchronized
    @AnyThread
    override fun register(registration: CSRegistration): CSRegistration =
        add(createUniqId(), registration)

    @Synchronized
    @AnyThread
    private fun add(key: String, registration: CSRegistration): CSRegistration {
        if (isCanceled) logWarn { traceMessage("Already canceled:$id") }
        if (registration.isCanceled) return registration
        if (isCanceled) return registration.also { it.cancel() }
        registrationMap[key] = registration
        return registration
    }

    @Synchronized
    @AnyThread
    fun cancel(key: String) {
        if (isCanceled) logWarn { traceMessage("Already canceled:$id") }
        remove(key)
    }

    private fun remove(key: String) = registrationMap.remove(key)?.cancel()

    @Synchronized
    @AnyThread
    override fun cancel(registration: CSRegistration) {
        val wasPresent = registrationMap.removeValue(registration)
        if (registration.isCanceled && !wasPresent) return
        if (!wasPresent) logWarn { traceMessage("Registration not found") }
        if (registration.isCanceled) {
            logWarn { traceMessage("Registration already canceled but was present:$registration") }
            return
        }
        registration.cancel()
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$id") }
            return
        }
    }

    val size: Int get() = registrationMap.size
}