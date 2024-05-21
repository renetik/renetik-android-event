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
import kotlin.coroutines.CoroutineContext

val mainScope = MainScope()

//@OptIn(DelicateCoroutinesApi::class)
//val SingleThread: ExecutorCoroutineDispatcher = newSingleThreadContext("SingleThread")

// TODO: move elsewhere

@OptIn(ExperimentalStdlibApi::class)
suspend fun currentDispatcher(): CoroutineDispatcher? =
    currentCoroutineContext()[CoroutineDispatcher]

suspend fun <T> CoroutineContext.context(
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

class JobRegistration(
    val job: Job
) : CSRegistrationImpl(isActive = true) {
    override fun onCancel() {
        super.onCancel()
        job.cancel()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
inline fun CoroutineDispatcher.launch(
    crossinline func: suspend (JobRegistration) -> Unit,
): JobRegistration {
    val registration = CompletableDeferred<JobRegistration>()
    registration.complete(JobRegistration(mainScope.launch(this) {
        if (isActive) func(registration.await())
    }))
    return registration.getCompleted()
}

inline fun CSHasRegistrations.launch(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline func: suspend (JobRegistration) -> Unit,
): CSRegistration = this + dispatcher.launch {
    if (!it.isCanceled) func(it)
}

inline fun CSHasRegistrations.launch(
    key: String,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline func: suspend (JobRegistration) -> Unit,
): CSRegistration = this + (key to dispatcher.launch {
    if (!it.isCanceled) func(it)
})

inline fun CSHasRegistrations.launchIfNot(
    key: String,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline func: suspend (JobRegistration) -> Unit,
): CSRegistration? {
    if (registrations.isActive(key)) return null
    return launch(key, dispatcher, func)
}

