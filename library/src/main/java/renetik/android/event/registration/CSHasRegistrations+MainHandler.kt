package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.lang.CSHandler.mainHandler
import kotlin.time.Duration

@AnyThread
inline fun CSHasRegistrations.laterEach(
    after: Int, period: Int = after, crossinline function: () -> Unit,
): CSRegistration = this + mainHandler.laterEach(after, period, function)

@AnyThread
inline fun CSHasRegistrations.laterEach(
    after: Duration, period: Duration = after, crossinline function: () -> Unit,
): CSRegistration = this + mainHandler.laterEach(after, period, function)