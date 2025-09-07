@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

interface CSHasChange<Argument> {
    companion object {
        inline fun onChange(vararg hasChanges: CSHasChange<*>, crossinline function: () -> Unit)
                : CSRegistration = CSRegistration(hasChanges.map { it.onChange(function) })

        inline fun CSHasChange<*>.action(crossinline function: () -> Unit): CSRegistration {
            function()
            return onChange(function)
        }

        inline fun <T> empty() = object : CSHasChange<T> {
            override fun onChange(function: (T) -> Unit) = CSRegistration.Empty
        }
    }

    fun onChange(function: (Argument) -> Unit): CSRegistration
}