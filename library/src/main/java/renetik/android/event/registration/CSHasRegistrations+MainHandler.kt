package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.lang.CSHandler.mainHandler

@AnyThread
fun CSHasRegistrations.registerLaterEach(
    after: Int, period: Int = after, function: () -> Unit,
): CSRegistration = this + mainHandler.laterEach(after, period, function)