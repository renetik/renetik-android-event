package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.lang.CSHandler.main
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

@AnyThread
fun CSHasRegistrations.registerLaterEach(
    after: Int, period: Int = after, function: () -> Unit,
): CSRegistration {
    val registration = this + main.laterEach(after, period, function)
    return CSRegistration { cancel(registration) }
}
