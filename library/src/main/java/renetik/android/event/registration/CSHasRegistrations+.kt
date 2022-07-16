package renetik.android.event.registration

fun CSHasRegistrations.register(
    registration: CSRegistration): CSRegistration =
    registrations.add(registration)

fun CSHasRegistrations.register(
    key: Any, registration: CSRegistration): CSRegistration =
    registration.also { registrations.add(key, it) }

fun CSHasRegistrations.cancel(
    registration: CSRegistration): CSRegistration =
    registration.also { registrations.cancel(it) }

@JvmName("CSEventOwnerRegisterNullable")
fun CSHasRegistrations.register(
    registration: CSRegistration?): CSRegistration? =
    registration?.let { registrations.add(it) }

@JvmName("CSEventOwnerCancelNullable")
fun CSHasRegistrations.cancel(registration: CSRegistration?) =
    registration?.let { registrations.cancel(it) }

fun CSHasRegistrations.cancel(vararg registrations: CSRegistration?) =
    registrations.forEach { cancel(it) }

fun CSHasRegistrations.cancel(registrations: List<CSRegistration>?) {
    registrations?.forEach { cancel(it) }
}