package renetik.android.event.registration

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.Result.Companion.success

suspend inline fun <reified T> CSHasChangeValue<*>.onType(): T =
    value as? T ?: suspendCancellableCoroutine {
        value as? T ?: run {
            var registration: CSRegistration? = null
            registration = onChange { value ->
                (value as? T)?.also { type ->
                    registration?.cancel()
                    success(type)
                }
            }
            it.invokeOnCancellation { registration.cancel() }
        }
    }