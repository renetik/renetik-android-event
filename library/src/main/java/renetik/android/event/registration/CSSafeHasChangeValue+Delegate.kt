package renetik.android.event.registration

import renetik.android.event.property.CSSafeHasChangeValue

fun <T> CSSafeHasChangeValue<T>.delegate(
    parent: CSHasRegistrations? = null,
): CSSafeHasChangeValue<T> = delegateValue(parent, from = { it })

fun <T, Return> CSSafeHasChangeValue<T>.delegateValue(
    parent: CSHasRegistrations? = null,
    from: (T) -> Return,
): CSSafeHasChangeValue<Return> = let { source ->
    object : CSSafeHasChangeValue<Return> {
        override val value: Return get() = from(source.value)

        override fun onChange(function: (Return) -> Unit): CSRegistration {
            val value = CSValueFunction(this, value, function)
            return source.onChange {
                if (parent?.registrations.isActive) value.update { from(source.value) }
            }.registerTo(parent)
        }

        override fun onUnsafeChange(function: (Return) -> Unit): CSRegistration {
            val value = CSValueFunction(this, value, function)
            return source.onUnsafeChange {
                if (parent?.registrations.isActive) value.update { from(source.value) }
            }.registerTo(parent)
        }
    }
}

@JvmName("safePairDelegate")
fun <T, V, Return> Pair<CSSafeHasChangeValue<T>, CSSafeHasChangeValue<V>>.delegate(
    parent: CSHasRegistrations? = null,
    from: (T, V) -> Return,
): CSSafeHasChangeValue<Return> = object : CSSafeHasChangeValue<Return> {
    override val value: Return get() = from(first.value, second.value)

    override fun onChange(function: (Return) -> Unit): CSRegistration {
        val value = CSValueFunction(this, value, function)
        return CSRegistration(
            first.onChange {
                if (parent?.registrations.isActive) value.update {
                    from(first.value, second.value)
                }
            },
            second.onChange {
                if (parent?.registrations.isActive) value.update {
                    from(first.value, second.value)
                }
            },
        ).registerTo(parent)
    }

    override fun onUnsafeChange(function: (Return) -> Unit): CSRegistration {
        val value = CSValueFunction(this, value, function)
        return CSRegistration(
            first.onUnsafeChange {
                if (parent?.registrations.isActive) value.update {
                    from(first.value, second.value)
                }
            },
            second.onUnsafeChange {
                if (parent?.registrations.isActive) value.update {
                    from(first.value, second.value)
                }
            },
        ).registerTo(parent)
    }
}
