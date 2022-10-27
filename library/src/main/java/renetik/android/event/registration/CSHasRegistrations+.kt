package renetik.android.event.registration

fun <T : CSRegistration> CSHasRegistrations.register(
    registration: T): T = registration.also { registrations.register(it) }

@JvmName("registerReplaceRegistrationNullable")
fun CSHasRegistrations.register(
    replace: CSRegistration?, registration: CSRegistration?): CSRegistration? =
    registrations.register(replace, registration)

fun CSHasRegistrations.register(
    replace: CSRegistration?, registration: CSRegistration): CSRegistration =
    registration.also { registrations.register(replace, it) }

fun CSHasRegistrations.cancel(
    registration: CSRegistration): CSRegistration =
    registration.also { registrations.cancel(it) }

@JvmName("CSEventOwnerRegisterNullable")
fun CSHasRegistrations.register(
    registration: CSRegistration?): CSRegistration? =
    registration?.let { registrations.register(it) }

@JvmName("CSEventOwnerCancelNullable")
fun CSHasRegistrations.cancel(registration: CSRegistration?) =
    registration?.let { registrations.cancel(it) }

fun CSHasRegistrations.cancel(vararg registrations: CSRegistration?) =
    registrations.forEach { cancel(it) }

fun CSHasRegistrations.cancel(registrations: List<CSRegistration>?) {
    registrations?.forEach { cancel(it) }
}