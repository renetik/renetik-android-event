package renetik.android.event.registration

import androidx.annotation.UiThread
import renetik.android.event.CSEvent
import renetik.android.event.listen

inline fun <T> CSHasRegistrations.listenOnce(
    event: CSEvent<T>, @UiThread crossinline listener: (argument: T) -> Unit) =
    register(event.listen { registration, argument ->
        cancel(registration)
        listener(argument)
    })