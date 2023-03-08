package renetik.android.event.registration

inline fun <T> CSHasRegistrations.onChangeOnce(
    event: CSHasChange<T>,
    crossinline listener: (argument: T) -> Unit
) = register(event.onChange { registration, argument ->
    cancel(registration)
    listener(argument)
})

inline fun CSHasRegistrations.onChangeOnce(
    event: CSHasChange<*>,
    crossinline listener: () -> Unit
) = onChangeOnce(event) { _ -> listener() }

inline fun CSHasRegistrations.listenUntilTrueOnce(
    hasChange: CSHasChange<Boolean>,
    crossinline listener: (argument: Boolean) -> Unit
) = register(hasChange.onChange { registration, value ->
    if (value) {
        cancel(registration)
        listener(true)
    }
})