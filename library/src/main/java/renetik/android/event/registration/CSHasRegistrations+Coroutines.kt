package renetik.android.event.registration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

val mainScope = MainScope()

//@OptIn(DelicateCoroutinesApi::class)
//val SingleThread: ExecutorCoroutineDispatcher = newSingleThreadContext("SingleThread")

// TODO: move elsewhere

@OptIn(ExperimentalStdlibApi::class)
suspend fun currentDispatcher(): CoroutineDispatcher? =
    currentCoroutineContext()[CoroutineDispatcher]

suspend fun <T> CoroutineDispatcher.context(
    block: suspend CoroutineScope.() -> T
): T = withContext(this, block)

suspend fun Job.cancelIfNotActive(scope: CoroutineScope, onCancel: () -> Unit) {
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
    val registration = CSRegistration(isActive = true) { job?.cancel() }
    job = mainScope.launch(this) {
        //Somehow registration was canceled when job was no initialised
        if (!registration.isCanceled) {
            func(registration)
        }
    }
    return registration
}

inline fun CSHasRegistrations.launch(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline func: suspend (CSRegistration) -> Unit,
): CSRegistration = this + dispatcher.launch {
    if (!it.isCanceled) {
        func(it)
        cancel(it)
    }
}

inline fun CSHasRegistrations.launch(
    key: String,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline func: suspend (CSRegistration) -> Unit,
): CSRegistration = this + (key to dispatcher.launch {
    if (!it.isCanceled) {
        func(it)
        cancel(it)
    }
})

inline fun CSHasRegistrations.launchIfNot(
    key: String,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline func: suspend (CSRegistration) -> Unit,
): CSRegistration? {
    if (registrations.isActive(key)) return null
    return launch(key, dispatcher, func)
}

inline fun CSHasRegistrations.reLaunch(
    key: String,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline func: suspend (CSRegistration) -> Unit,
): CSRegistration {
    registrations.cancel(key)
    return launch(key, dispatcher, func)
}