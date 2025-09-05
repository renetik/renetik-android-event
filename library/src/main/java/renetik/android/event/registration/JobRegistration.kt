package renetik.android.event.registration

import kotlinx.coroutines.Job

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