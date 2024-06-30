package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.lang.CSHandler.mainHandler

@AnyThread
inline fun CSHasRegistrations.laterEach(
    after: Int, period: Int = after, crossinline function: () -> Unit,
): CSRegistration = this + mainHandler.laterEach(after, period, function)