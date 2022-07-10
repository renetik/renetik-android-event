package renetik.android.event.registrations

import renetik.android.event.registration.CSRegistration

fun CSHasRegistrations.register(registration: CSRegistration) =
    registrations.add(registration)

fun CSHasRegistrations.register(key: Any, registration: CSRegistration): CSRegistration {
    registrations.add(key, registration)
    return registration
}

fun CSHasRegistrations.cancel(registration: CSRegistration) =
    registration.also { registrations.cancel(it) }

fun CSHasRegistrations.remove(registration: CSRegistration) =
    registration.also { registrations.remove(it) }

@JvmName("CSEventOwnerRegisterNullable")
fun CSHasRegistrations.register(registration: CSRegistration?) =
    registration?.let { registrations.add(it) }

@JvmName("CSEventOwnerCancelNullable")
fun CSHasRegistrations.cancel(registration: CSRegistration?) =
    registration?.let { registrations.cancel(it) }

fun CSHasRegistrations.cancel(vararg registrations: CSRegistration?) =
    registrations.forEach { cancel(it) }

fun CSHasRegistrations.cancel(registrations: List<CSRegistration>?) {
    registrations?.forEach { cancel(it) }
}

@JvmName("CSEventOwnerRemoveNullable")
fun CSHasRegistrations.remove(registration: CSRegistration?) =
    registration?.also { registrations.remove(it) }