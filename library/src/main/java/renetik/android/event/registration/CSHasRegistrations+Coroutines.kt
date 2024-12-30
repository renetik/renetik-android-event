package renetik.android.event.registration

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job

private class JobRegistrationImpl2(
    private val registration: CSRegistration
) : JobRegistration {
    override var job: Job? = null
    override val isActive: Boolean get() = registration.isActive
    override val isCanceled: Boolean get() = registration.isCanceled
    override val eventCancel = registration.eventCancel
    override fun resume() = registration.resume()
    override fun pause() = registration.pause()
    override fun cancel() = registration.cancel()
}

@OptIn(ExperimentalCoroutinesApi::class)
fun CSHasRegistrations.launch(
    dispatcher: CoroutineDispatcher = Main,
    func: suspend (JobRegistration) -> Unit,
): JobRegistration {
    val newRegistration = CompletableDeferred<JobRegistrationImpl2>()
    val jobRegistration = dispatcher.launch { registration ->
        newRegistration.await().also {
            it.job = registration.job!!
            if (!it.isCanceled) {
                func(it)
                it.cancel()
            }
        }
    }
    newRegistration.complete(JobRegistrationImpl2(this + jobRegistration))
    return newRegistration.getCompleted()
}

fun CSHasRegistrations.launchWhileActive(
    dispatcher: CoroutineDispatcher = Main,
    func: suspend (JobRegistration) -> Unit,
) = launch(dispatcher) { while (it.isActive) func(it) }

@OptIn(ExperimentalCoroutinesApi::class)
fun CSHasRegistrations.launch(
    key: String, dispatcher: CoroutineDispatcher = Main,
    func: suspend (JobRegistration) -> Unit,
): JobRegistration {
    val newRegistration = CompletableDeferred<JobRegistrationImpl2>()
    val jobRegistration = dispatcher.launch { registration ->
        newRegistration.await().also {
            it.job = registration.job!!
            if (!it.isCanceled) {
                func(it)
                it.cancel()
            }
        }
    }
    newRegistration.complete(JobRegistrationImpl2(this + (key to jobRegistration)))
    return newRegistration.getCompleted()
}

fun CSHasRegistrations.launchIfNot(
    key: String,
    dispatcher: CoroutineDispatcher = Main,
    func: suspend (CSRegistration) -> Unit,
): CSRegistration? {
    if (registrations.isActive(key)) return null
    return launch(key, dispatcher, func)
}