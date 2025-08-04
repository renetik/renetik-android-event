package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.lang.CSHandler.mainHandler
import kotlin.time.Duration

@AnyThread
inline fun CSHasRegistrations.laterEach(
    after: Int, period: Int = after,
    start: Boolean = true, crossinline function: () -> Unit,
): CSRegistration = this + mainHandler.laterEach(after, period, start, function)

@AnyThread
inline fun CSHasRegistrations.laterEach(
    after: Duration, period: Duration = after,
    start: Boolean = true, crossinline function: () -> Unit,
): CSRegistration = this + mainHandler.laterEach(after, period, start, function)