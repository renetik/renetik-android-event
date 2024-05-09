package renetik.android.event.registration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import renetik.android.event.common.CSContext
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

inline fun CSHasRegistrations.launch(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline func: suspend (CSRegistration) -> Unit,
): CSRegistration {
    val self = this
    var job: Job? = null
    val registration = this + CSRegistration { job?.cancel() }
    job = mainScope.launch(dispatcher) {
        //Somehow registration was canceled when job was no initialised
        if (!registration.isCanceled) {
            func(registration)
            self.cancel(registration)
        }
    }
    return registration
}

inline fun CSHasRegistrations.launch(
    key: String,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline func: suspend (CSRegistration) -> Unit,
): CSRegistration {
    val self = this
    var job: Job? = null
    val registration = this + (key to CSRegistration { job?.cancel() })
    job = mainScope.launch(dispatcher) {
        //Somehow registration was canceled when job was no initialised
        if (!registration.isCanceled) {
            func(registration)
            self.cancel(registration)
        }
    }
    return registration
}

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
): CSRegistration? {
    registrations.cancel(key)
    return launch(key, dispatcher, func)
}