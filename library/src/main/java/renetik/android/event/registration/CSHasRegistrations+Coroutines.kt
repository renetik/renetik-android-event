package renetik.android.event.registration

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import renetik.android.core.kotlin.className
import renetik.android.core.lang.result.mainScope
import renetik.android.core.lang.variable.CSWeakVariable.Companion.weak

private class JobRegistrationWrapper(
    private val registration: CSRegistration
) : JobRegistration {
    override var job: Job? by weak(null)
    override val isActive: Boolean get() = registration.isActive
    override val isCanceled: Boolean get() = registration.isCanceled
    override val eventCancel = registration.eventCancel
    override fun resume() = registration.resume()
    override fun pause() = registration.pause()
    override fun cancel() = registration.cancel()
}

@OptIn(ExperimentalCoroutinesApi::class)
fun CSHasRegistrations.launch(
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher = Main,
    func: suspend (JobRegistration) -> Unit,
): JobRegistration {
    val newRegistration = CompletableDeferred<JobRegistrationWrapper>()
    val jobRegistration = dispatcher.launch(scope, className) { registration ->
        newRegistration.await().also {
            it.job = registration.job!!
            if (!it.isCanceled) {
                func(it)
                it.cancel()
            }
        }
    }
    newRegistration.complete(JobRegistrationWrapper(this + jobRegistration))
    return newRegistration.getCompleted()
}

fun CSHasRegistrations.launch(
    dispatcher: CoroutineDispatcher = Main,
    func: suspend (JobRegistration) -> Unit,
): JobRegistration = launch(mainScope, dispatcher, func)

@OptIn(ExperimentalCoroutinesApi::class)
fun CSHasRegistrations.launch(
    key: String, dispatcher: CoroutineDispatcher = Main,
    func: suspend (JobRegistration) -> Unit,
): JobRegistration {
    val newRegistration = CompletableDeferred<JobRegistrationWrapper>()
    val previous = registrations.map[key] as? JobRegistration
    registrations.cancel(key)
    val jobRegistration = dispatcher.launch(className) { registration ->
        previous?.waitToFinish()
        newRegistration.await().also {
            it.job = registration.job!!
            if (!it.isCanceled) {
                func(it)
                it.cancel()
            }
        }
    }
    newRegistration.complete(JobRegistrationWrapper(this + (key to jobRegistration)))
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