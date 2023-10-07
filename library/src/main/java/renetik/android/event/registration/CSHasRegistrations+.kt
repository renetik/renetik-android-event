package renetik.android.event.registration

import androidx.annotation.AnyThread

operator fun <T : CSRegistration> CSHasRegistrations.plus(registration: T): T =
    registration.also { registrations.register(it) }

@JvmName("plusNullable")
operator fun <T : CSRegistration> CSHasRegistrations.plus(registration: T?): T? =
    registration?.also { registrations.register(it) }

operator fun <T : CSRegistration> CSHasRegistrations.minus(registration: T?) {
    cancel(registration)
}

@AnyThread
fun <T : CSRegistration> CSHasRegistrations.register(registration: T): T =
    registration.also { registrations.register(it) }

@JvmName("registerNullable")
fun CSHasRegistrations.register(registration: CSRegistration?): CSRegistration? =
    registration?.let { registrations.register(it) }

@JvmName("registerReplaceRegistrationNullable")
fun CSHasRegistrations.register(
    replace: CSRegistration?, registration: CSRegistration?
): CSRegistration? =
    registrations.register(replace, registration)

fun CSHasRegistrations.register(
    replace: CSRegistration?, registration: CSRegistration
): CSRegistration =
    registration.also { registrations.register(replace, it) }

@JvmName("CSEventOwnerCancelNullable")
fun CSHasRegistrations.cancel(registration: CSRegistration?) {
    registration?.let { registrations.cancel(it) }
}

fun CSHasRegistrations.cancel(registrations: List<CSRegistration>?) {
    registrations?.forEach { cancel(it) }
}