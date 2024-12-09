@file:OptIn(ExperimentalCoroutinesApi::class)

package renetik.android.event.registration

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

inline fun CSHasRegistrations.launch(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline func: suspend (CSRegistration) -> Unit,
): CSRegistration {
    val registration = CompletableDeferred<CSRegistration>()
    registration.complete(this + dispatcher.launch {
        registration.await().also {
            if (!it.isCanceled) {
                func(it)
                if (!it.isCanceled) it.cancel()
            }
        }
    })
    return registration.getCompleted()
}

inline fun CSHasRegistrations.launch(
    key: String,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline func: suspend (CSRegistration) -> Unit,
): CSRegistration {
    val registration = CompletableDeferred<CSRegistration>()
    registration.complete(this + (key to dispatcher.launch {
        registration.await().also {
            if (!it.isCanceled) {
                func(it)
                it.cancel()
            }
        }
    }))
    return registration.getCompleted()
}

inline fun CSHasRegistrations.launchIfNot(
    key: String,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline func: suspend (CSRegistration) -> Unit,
): CSRegistration? {
    if (registrations.isActive(key)) return null
    return launch(key, dispatcher, func)
}