@file:OptIn(ExperimentalCoroutinesApi::class)

package renetik.android.event.registration

import kotlinx.coroutines.CancellationException
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
import renetik.android.core.logging.CSLog.logError
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
    val context = name?.let(::CoroutineName)?.let { this + it } ?: this
    mainScope.launch(context) {
        try {
            if (isActive && !registration.isCanceled) {
                registration.job = coroutineContext[Job]!!
                func(registration)
            }
        } catch (ex: CancellationException) {
            registration.cancel()
            throw ex
        } catch (ex: Exception) {
            logError(ex)
        }
    }
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