package renetik.android.event.registration

fun <T> CSHasChangeValue<T>.delegate(
    parent: CSHasRegistrations? = null,
): CSHasChangeValue<T> = delegateFrom(parent, from = { it })

fun <T, Return> CSHasChangeValue<T>.delegateFrom(
    parent: CSHasRegistrations? = null,
    from: (T) -> Return,
): CSHasChangeValue<Return> = let { property ->
    object : CSHasChangeValue<Return> {
        override val value: Return get() = from(property.value)
        override fun onChange(function: (Return) -> Unit): CSRegistration {
            val value = CSValueFunction(this, value, function)
            return property.onChange {
                if (parent?.registrations.isActive) value(from(it))
            }.registerTo(parent)
        }
    }
}

fun <Argument, Return> List<CSHasChangeValue<Argument>>.delegateFromList(
    parent: CSHasRegistrations? = null,
    from: (List<Argument>) -> Return,
): CSHasChangeValue<Return> = let { properties ->
    object : CSHasChangeValue<Return> {
        override val value: Return get() = from(properties.map { it.value })
        override fun onChange(function: (Return) -> Unit): CSRegistration {
            val value = CSValueFunction(this, value, function)
            return properties.onChange { list ->
                if (parent?.registrations.isActive) value(from(list))
            }.registerTo(parent)
        }
    }
}

fun <T> CSHasChangeValue<T>.delegateIsChange(
    parent: CSHasRegistrations? = null,
): CSHasChangeValue<Boolean> = let { property ->
    object : CSHasChangeValue<Boolean> {
        override var value: Boolean = false
        override fun onChange(function: (Boolean) -> Unit): CSRegistration =
            property.onChange {
                value = true
                if (parent?.registrations.isActive) function(true)
                value = false
            }.registerTo(parent)
    }
}
