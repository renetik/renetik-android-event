package renetik.android.event.registration

import androidx.annotation.AnyThread

@AnyThread
fun <T : CSRegistration> CSHasRegistrations.register(registration: T): T =
    registration.also { registrations.register(it) }

operator fun <T : CSRegistration> CSHasRegistrations.plus(registration: T): T =
    register(registration)

@JvmName("registerNullable")
fun CSHasRegistrations.register(registration: CSRegistration?): CSRegistration? =
    registration?.also(::register)

@JvmName("plusNullable")
operator fun <T : CSRegistration> CSHasRegistrations.plus(registration: T?): T? =
    registration?.also(::register)

@JvmName("registerReplaceRegistrationNullable")
fun CSHasRegistrations.register(
    replace: CSRegistration?, registration: CSRegistration?
): CSRegistration? = registrations.register(replace, registration)

fun CSHasRegistrations.register(
    replace: CSRegistration?, registration: CSRegistration
): CSRegistration = registration.also { registrations.register(replace, it) }

@JvmName("registerKeyRegistrationNullable")
fun CSHasRegistrations.register(
    key: String, registration: CSRegistration?
): CSRegistration? = registrations.register(key, registration)

fun CSHasRegistrations.register(
    key: String, registration: CSRegistration
): CSRegistration = registration.also { registrations.register(key, it) }

operator fun CSHasRegistrations.plus(
    registration: Pair<String, CSRegistration>
): CSRegistration = register(registration.first, registration.second)

@JvmName("plusPairNullable")
operator fun CSHasRegistrations.plus(
    registration: Pair<String, CSRegistration?>
): CSRegistration? = register(registration.first, registration.second)

//Cancel
@JvmName("CSEventOwnerCancelNullable")
fun CSHasRegistrations.cancel(registration: CSRegistration?) {
    registration?.let { registrations.cancel(it) }
}

operator fun <T : CSRegistration> CSHasRegistrations.minus(registration: T?) =
    cancel(registration)

fun CSHasRegistrations.cancel(registrations: List<CSRegistration>?) {
    registrations?.forEach { cancel(it) }
}
