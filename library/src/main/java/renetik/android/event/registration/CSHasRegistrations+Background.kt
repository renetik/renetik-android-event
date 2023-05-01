package renetik.android.event.registration

import androidx.annotation.WorkerThread
import renetik.android.core.lang.Func
import renetik.android.event.registration.task.CSBackground.background

inline fun CSHasRegistrations.registerBackground(
    @WorkerThread crossinline function: Func,
): CSRegistration = register(background {
    function()
    cancel(it)
})