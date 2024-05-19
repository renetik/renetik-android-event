package renetik.android.event.registration

import androidx.annotation.UiThread
import renetik.android.event.CSEvent

inline fun <T> CSHasRegistrations.registerListenOnce(
    event: CSEvent<T>, @UiThread crossinline listener: (argument: T) -> Unit
): CSRegistration {
    var registration: CSRegistration? = null
    return this + event.listen { argument ->
        listener(argument)
        registration?.cancel()
    }.also { registration = it }
}