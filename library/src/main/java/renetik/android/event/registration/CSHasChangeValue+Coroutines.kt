package renetik.android.event.registration

import kotlinx.coroutines.suspendCancellableCoroutine

suspend inline fun <reified T> CSHasChangeValue<*>.onType(): T = suspendCancellableCoroutine {
    val registration = onType<T> { type -> Result.success(type) }
    it.invokeOnCancellation { registration.cancel() }
}