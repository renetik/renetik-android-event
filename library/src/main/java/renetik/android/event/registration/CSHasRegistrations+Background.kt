package renetik.android.event.registration

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import renetik.android.core.lang.Func
import renetik.android.core.lang.variable.CSVariable.Companion.variable
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import renetik.android.event.registration.task.CSBackground.background

@AnyThread
inline fun CSHasRegistrations.registerBackground(
    @WorkerThread crossinline function: Func
) = registerBackground(after = 0, function)

@AnyThread
inline fun CSHasRegistrations.registerBackground(
    after: Int, @WorkerThread crossinline function: () -> Unit,
): CSRegistration {
    var isRegisteredToParent by variable(false)
    val registration = background(after) {
        function()
        if (isRegisteredToParent) cancel(it) else it.cancel()
    }
    // For some magic reason background could execute already
    if (!registration.isCanceled) {
        this + registration
        isRegisteredToParent = true
    }
    return CSRegistration { if (!registration.isCanceled) cancel(registration) }
}