package renetik.android.event.common

import androidx.annotation.WorkerThread
import renetik.android.core.lang.CSFunc
import renetik.android.core.lang.void
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.registerBackground

class CSBackgroundOnceFunc(
    parent: CSHasDestruct? = null,
    @WorkerThread private val function: () -> Unit,
    val after: Int = 0,
) : CSModel(parent), CSFunc {

    companion object {
        fun CSHasDestruct.backgroundOnce(
            after: Int = 0, @WorkerThread function: () -> void,
        ) = CSBackgroundOnceFunc(this, function, after)
    }

    var registration: CSRegistration? = null
    override operator fun invoke() {
        registration?.cancel()
        registration = registerBackground(after, function)
    }
}