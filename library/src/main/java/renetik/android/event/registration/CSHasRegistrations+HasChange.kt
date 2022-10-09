package renetik.android.event.registration

import androidx.annotation.UiThread
import renetik.android.event.CSEvent
import renetik.android.event.listen

inline fun <T> CSHasRegistrations.onChangeOnce(
    event: CSHasChange<T>, @UiThread crossinline listener: (argument: T) -> Unit) =
    register(event.onChange { registration, argument ->
        cancel(registration)
        listener(argument)
    })