package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.kotlin.collections.removeIf
import java.lang.System.nanoTime

class CSRegistrations {
    private val registrations: MutableMap<Any, CSRegistration> = mutableMapOf()
    private var active = true

    @Synchronized
    @AnyThread
    fun cancel() {
        active = false
        registrations.onEach { it.value.cancel() }.clear()
    }

    @Synchronized
    @AnyThread
    fun addAll(vararg registrations: CSRegistration): CSRegistrations {
        registrations.onEach { add(it) }
        return this
    }

    @Synchronized
    @AnyThread
    fun add(registration: CSRegistration): CSRegistration {
        if (!registration.isActive) return registration
        if (!active) return registration.apply(::cancel)
        registration.isActive = active
        registrations[nanoTime()] = registration
        return registration
    }

    @Synchronized
    @AnyThread
    fun add(key: Any, registration: CSRegistration): CSRegistration {
        if (!registration.isActive) return registration
        if (!active) return registration.apply(::cancel)
        registrations[key]?.cancel()
        registrations[key] = registration
        registration.isActive = active
        return registration
    }

    @Synchronized
    @AnyThread
    fun cancel(registration: CSRegistration) {
        registration.cancel()
        remove(registration)
    }

    @Synchronized
    @AnyThread
    fun remove(registration: CSRegistration) =
        registrations.removeIf { _, value -> value == registration }

    @Synchronized
    @AnyThread
    fun setActive(active: Boolean) {
        this.active = active
        for (registration in registrations) registration.value.isActive = active
    }
}