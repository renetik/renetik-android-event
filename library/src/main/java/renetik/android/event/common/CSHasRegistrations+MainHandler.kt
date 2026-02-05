package renetik.android.event.common

import renetik.android.core.kotlin.primitives.min
import renetik.android.core.lang.CSHandler.mainHandler
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.later
import renetik.android.event.registration.plus
import kotlin.time.Duration

inline fun CSHasRegistrations.later(
    after: Int, crossinline function: () -> Unit,
): CSRegistration = this + mainHandler.later(after.min(10)) { function() }

inline fun CSHasRegistrations.later(
    after: Duration, crossinline function: () -> Unit,
): CSRegistration = later(after.inWholeMilliseconds.toInt(), function)