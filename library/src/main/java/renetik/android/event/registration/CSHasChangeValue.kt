package renetik.android.event.registration

import renetik.android.core.lang.value.CSValue

interface CSHasChangeValue<T> : CSValue<T>, CSHasChange<T> {
    companion object {
        fun <T> CSHasChangeValue<T>.action(function: (T) -> Unit): CSRegistration {
            function(value)
            return onChange(function)
        }
    }
}