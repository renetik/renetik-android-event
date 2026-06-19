package renetik.android.event.registration

import renetik.android.core.lang.synchronized

class CSValueFunction<Return>(
    val parent: Any,
    private var value: Return,
    private val function: (Return) -> Unit,
) {
    operator fun invoke(newValue: Return) = synchronized(parent) {
        if (value != newValue) {
            value = newValue
            function(newValue)
        }
    }
}