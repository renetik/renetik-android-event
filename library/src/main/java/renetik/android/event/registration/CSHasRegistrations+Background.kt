package renetik.android.event.registration

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import renetik.android.core.lang.Func
import renetik.android.event.CSBackground.background

@AnyThread
inline fun CSHasRegistrations.registerBackground(
    @WorkerThread crossinline function: Func
) = registerBackground(after = 0, function)

@AnyThread
inline fun CSHasRegistrations.registerBackground(
    after: Int, @WorkerThread crossinline function: () -> Unit,
): CSRegistration = this + background(after) { function() }