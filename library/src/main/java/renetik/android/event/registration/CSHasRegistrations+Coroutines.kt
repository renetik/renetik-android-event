package renetik.android.event.registration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import kotlin.properties.Delegates.notNull

val mainScope = MainScope()

@OptIn(DelicateCoroutinesApi::class)
val SingleThread: ExecutorCoroutineDispatcher = newSingleThreadContext("SingleThread")

suspend fun <T> CoroutineDispatcher.context(
    block: suspend CoroutineScope.() -> T
): T = withContext(this, block)

fun CSHasRegistrations.launch(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    func: suspend () -> Unit,
): CSRegistration {
    val self = this
    var job: Job by notNull()
    val registration = this + CSRegistration { job.cancel() }
    job = mainScope.launch(dispatcher) {
        func()
        self.cancel(registration)
    }
    return registration
}