package renetik.android.event.dispatch

import renetik.android.event.lifecycle.*

import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

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