@file:OptIn(ExperimentalCoroutinesApi::class)

package renetik.android.event.registration

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import renetik.android.core.lang.result.mainScope
import renetik.android.core.logging.CSLog.logInfo

interface JobRegistration : CSRegistration {
    val job: Job?
    suspend fun cancelAndWait() {
        cancel()
        waitToFinish()
    }

    suspend fun waitToFinish() {
        job?.join()
    }
}

private class JobRegistrationImpl(
    isActive: Boolean = false,
    private val onCancel: (Job?) -> Unit,
) : CSRegistrationImpl(isActive), JobRegistration {
    override var job: Job? = null
    override fun onCancel() {
        super.onCancel()
        onCancel.invoke(job)
    }
}

fun CoroutineDispatcher.launch(
    func: suspend (JobRegistration) -> Unit,
): JobRegistration {
    val registration = JobRegistrationImpl(isActive = true,
        onCancel = { job -> job?.let { if (!it.isCompleted) it.cancel() } })
    val job = CompletableDeferred<Job>()
    job.complete(mainScope.launch(this) {
        job.await()
        registration.job = job.getCompleted()
        if (isActive && registration.isActive) func(registration)
    })
    return registration
}