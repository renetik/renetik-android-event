package renetik.android.event.registration

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import renetik.android.core.lang.Func
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import renetik.android.event.registration.task.CSBackground.background

//TODO: Can CSHasDestruct.background bew used instead without memory leak reports ?
@AnyThread
inline fun CSHasRegistrations.registerBackground(
    @WorkerThread crossinline function: Func
) = registerBackground(after = 0, function)

//TODO: Can CSHasDestruct.background bew used instead without memory leak reports ?
@AnyThread
inline fun CSHasRegistrations.registerBackground(
    after: Int, @WorkerThread crossinline function: () -> Unit,
): CSRegistration {
    val registration = register(background(after) { //after.min(10)
        function()
        cancel(it)
    })
    return CSRegistration { if (!registration.isCanceled) cancel(registration) }
}