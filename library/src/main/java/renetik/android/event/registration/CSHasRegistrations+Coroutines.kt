@file:OptIn(ExperimentalCoroutinesApi::class)

package renetik.android.event.registration

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import kotlin.coroutines.CoroutineContext

val mainScope: CoroutineScope = MainScope()

//@OptIn(DelicateCoroutinesApi::class)
//val SingleThread: ExecutorCoroutineDispatcher = newSingleThreadContext("SingleThread")

// TODO: move elsewhere

@OptIn(ExperimentalStdlibApi::class)
suspend fun currentDispatcher(): CoroutineDispatcher? =
    currentCoroutineContext()[CoroutineDispatcher]

suspend fun <T> CoroutineContext.context(
    block: suspend CoroutineScope.() -> T
): T = withContext(this, block)

suspend fun Job.cancelIfNotActive(scope: CoroutineScope, onCancel: () -> Unit) = apply {
    while (isActive) {
        delay(500)
        if (!scope.isActive) {
            onCancel()
            cancelAndJoin()
        }
    }
    join()
}

inline fun CoroutineDispatcher.launch(
    crossinline func: suspend (CSRegistration) -> Unit,
): CSRegistration {
    var job: Job? = null
    val registration = CSRegistration(isActive = true, onCancel = {
        job?.let { if (!it.isCompleted) it.cancel() }
    })
    job = mainScope.launch(this) {
        if (isActive && registration.isActive) func(registration)
    }
    return registration
}

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