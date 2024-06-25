package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.java.lang.nanoTime
import renetik.android.core.kotlin.collections.removeValue
import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.variable.CSVariable.Companion.variable
import renetik.android.core.logging.CSLog.logWarnTrace
import java.util.concurrent.atomic.AtomicInteger

class CSRegistrationsMap(private val parent: Any) : CSRegistrations, CSHasRegistrations {

    override val registrations: CSRegistrationsMap = this

    @get:Synchronized
    override var isActive by variable(true, ::onActiveChange)
        private set

    private var isCancelling: Boolean = false

    @get:Synchronized
    override var isCanceled: Boolean = false
        private set

    private val registrationMap: MutableMap<String, CSRegistration> by lazy(::mutableMapOf)
    private val counter = AtomicInteger(0)
    private fun createUniqueId() = "${counter.incrementAndGet()}: $nanoTime"

    private fun onActiveChange(isActive: Boolean) {
        if (isCanceled) {
            logWarnTrace { "Already canceled:$parent" }
            return
        }
        registrationMap.forEach { it.value.setActive(isActive) }
    }

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
    @AnyThread
    override fun cancel() {
        if (isCancelling) {
            logWarnTrace { "Already cancelling:$this" }
            return
        }
        isCancelling = true

        if (isCanceled) {
            logWarnTrace { "Already canceled:$this" }
            return
        }
        registrationMap.onEach { it.value.cancel() }.clear()
        isCanceled = true
    }

    @Synchronized
    @AnyThread
    override fun register(key: String, registration: CSRegistration?): CSRegistration? {
        if (isCanceled) logWarnTrace { "Already canceled:$this" }
        remove(key)
        return registration?.let { add(key, it) }
    }

    @Synchronized
    @AnyThread
    override fun register(
        replace: CSRegistration?,
        registration: CSRegistration?,
    ): CSRegistration? {
        if (isCanceled) logWarnTrace { "Already canceled:$this" }
        replace?.cancel()
        return registration?.let { add(createUniqueId(), it) }
    }

    fun isActive(key: String): Boolean = registrationMap[key]?.isActive.isTrue

    @Synchronized
    @AnyThread
    override fun register(registration: CSRegistration): CSRegistration =
        add(createUniqueId(), registration)

    @Synchronized
    @AnyThread
    private fun add(key: String, registration: CSRegistration): CSRegistration {
        if (isCanceled) logWarnTrace { "Already canceled:$this" }
        if (registration.isCanceled) return registration
        if (isCanceled) return registration.also { it.cancel() }
        registrationMap[key] = registration
        return object : CSRegistration {
            override val isActive: Boolean get() = registration.isActive
            override val isCanceled: Boolean get() = registration.isCanceled
            override fun resume() = registration.resume()
            override fun pause() = registration.pause()
            override fun cancel() = cancel(registration)
        }
    }

    @Synchronized
    @AnyThread
    fun cancel(key: String) {
        if (isCanceled)
            logWarnTrace { "Already canceled:$this" }
        remove(key)
    }

    private fun remove(key: String) = registrationMap.remove(key)?.cancel()

    @Synchronized
    @AnyThread
    private fun cancel(registration: CSRegistration) {
        if (isCancelling && !isCanceled) {
            registration.cancel()
            return
        }
        val wasPresent = registrationMap.removeValue(registration)
        if (registration.isCanceled && !wasPresent) return
        if (!wasPresent) logWarnTrace { "Registration not found:$registration" }
        if (registration.isCanceled) return
        registration.cancel()
        if (isCanceled) logWarnTrace { "Already canceled:$this" }
    }

    val size: Int get() = registrationMap.size

    fun clear() = registrationMap.cancelRegistrations()

    override fun toString(): String =
        "${super.toString()} parent:$parent " + "size:$size isActive:$isActive isCanceled:$isCanceled"
}