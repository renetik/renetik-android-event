@file:OptIn(ExperimentalCoroutinesApi::class)

package renetik.android.event.registration

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import renetik.android.core.lang.result.mainScope
import renetik.android.core.lang.variable.CSWeakVariable.Companion.weak
import kotlin.time.Duration

private class JobRegistrationImpl(
    isActive: Boolean = false,
    private val onCancel: (Job?) -> Unit,
) : CSRegistrationImpl(isActive), JobRegistration {
    override var job: Job? by weak(null)
    override fun onCancel() {
        super.onCancel()
        onCancel.invoke(job)
    }
}

fun CoroutineDispatcher.launch(
    name: String? = null, func: suspend (JobRegistration) -> Unit,
): JobRegistration {
    val registration = JobRegistrationImpl(isActive = true,
        onCancel = { job -> job?.let { if (!it.isCompleted) it.cancel() } })
    val job = CompletableDeferred<Job>()
    val context = name?.let(::CoroutineName)?.let { this + it } ?: this
    job.complete(mainScope.launch(context) {
        job.await()
        registration.job = job.getCompleted()
        if (isActive && !registration.isCanceled) func(registration)
    })
    return registration
}

fun CoroutineDispatcher.launch(
    name: String? = null, after: Duration,
    func: suspend (JobRegistration) -> Unit,
): JobRegistration = launch(name) { delay(after); func(it) }

fun CoroutineScope.start(
    func: suspend (JobRegistration) -> Unit,
): JobRegistration {
    val registration = JobRegistrationImpl(isActive = true,
        onCancel = { job -> job?.let { if (!it.isCompleted) it.cancel() } })
    val job = CompletableDeferred<Job>()
    job.complete(launch {
        job.await()
        registration.job = job.getCompleted()
        if (isActive && registration.isActive) func(registration)
    })
    return registration
}