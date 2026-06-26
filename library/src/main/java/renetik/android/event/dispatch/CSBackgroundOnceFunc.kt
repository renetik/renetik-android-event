package renetik.android.event.dispatch

import renetik.android.event.lifecycle.*

import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

import androidx.annotation.WorkerThread
import kotlinx.coroutines.delay
import renetik.android.core.base.CSApplication.Companion.app
import renetik.android.core.lang.CSFunc
import renetik.android.event.registration.CSRegistration
import renetik.android.event.dispatch.launch

class CSBackgroundOnceFunc(
    parent: CSHasDestruct? = null,
    @WorkerThread private val function: () -> Unit,
    val after: Int = 0,
) : CSModel(parent), CSFunc {

    companion object {
        fun CSHasDestruct.backgroundOnce(
            after: Int = 0, @WorkerThread function: () -> Unit,
        ) = CSBackgroundOnceFunc(this, function, after)
    }

    var registration: CSRegistration? = null
    override operator fun invoke() {
        registration?.cancel()
        registration = launch(app.Default) { delay(after.toLong()); function() }
    }
}

//class CSBackgroundOnceFunc(
//    parent: CSHasDestruct? = null,
//    @WorkerThread private val function: () -> Unit,
//    val after: Int = 0,
//) : CSModel(parent), CSFunc {
//
//    companion object {
//        fun CSHasDestruct.backgroundOnce(
//            after: Int = 0, @WorkerThread function: () -> Unit,
//        ) = CSBackgroundOnceFunc(this, function, after)
//    }
//
//    var registration: CSRegistration? = null
//    override operator fun invoke() {
//        registration?.cancel()
//        registration = registerBackground(after, function)
//    }
//}