package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.synchronized
import renetik.android.core.lang.tuples.CSQuintuple
import renetik.android.core.lang.tuples.to
import renetik.android.event.property.CSSafeHasChangeValue
import renetik.android.event.property.CSSafeHasChangeValueBase
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

@JvmName("CSHasChangeValueAndCSSafeHasChangeValueBoolean")
infix fun <T> CSHasChangeValue<T>.and(
    other: CSSafeHasChangeValue<Boolean>
): CSSafeHasChangeValue<T> {
    val first = this
    return object : CSSafeHasChangeValueBase<T>(initialValue = first.value) {
        init {
            @Suppress("UNCHECKED_CAST")
            this + (
                if (first is CSSafeHasChangeValue<*>)
                    (first as CSSafeHasChangeValue<T>).onUnsafeChange {
                        if (other.value) value(it)
                        else setValueSilently(it)
                    }
                else first.onChange {
                    if (other.value) value(it)
                    else setValueSilently(it)
                })
            this + other.onUnsafeChange {
                if (it) {
                    val currentValue = value
                    value(currentValue, force = true)
                }
            }
        }
    }
}

@JvmName("CSSafeHasChangeValueAndCSSafeHasChangeValueBoolean")
infix fun <T> CSSafeHasChangeValue<T>.and(
    other: CSSafeHasChangeValue<Boolean>
): CSSafeHasChangeValue<T> {
    val first = this
    return object : CSSafeHasChangeValueBase<T>(initialValue = first.value) {
        init {
            this + first.onUnsafeChange {
                if (other.value) value(it)
                else setValueSilently(it)
            }
            this + other.onUnsafeChange {
                if (it) {
                    val currentValue = value
                    value(currentValue, force = true)
                }
            }
        }
    }
}

@JvmName("CSSafeHasChangeValueAndCSHasChangeValueBoolean")
infix fun <T> CSSafeHasChangeValue<T>.and(
    other: CSHasChangeValue<Boolean>
): CSSafeHasChangeValue<T> {
    val first = this
    return object : CSSafeHasChangeValueBase<T>(initialValue = first.value) {
        init {
            this + first.onUnsafeChange {
                if (other.value) value(it)
                else setValueSilently(it)
            }
            this + other.onChange {
                if (it) {
                    val currentValue = value
                    value(currentValue, force = true)
                }
            }
        }
    }
}

@JvmName("CSSafeHasChangeValueBooleanNot")
operator fun CSSafeHasChangeValue<Boolean>.not(): CSSafeHasChangeValue<Boolean> {
    val source = this
    return object : CSSafeHasChangeValueBase<Boolean>(initialValue = !source.value) {
        init {
            this + source.onUnsafeChange { value(!it) }
        }
    }
}

@JvmName("safeHasChangeValue")
fun <Argument, Return> CSSafeHasChangeValue<Argument>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (Argument) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return> = let { source ->
    object : CSSafeHasChangeValueBase<Return>(parent, from(source.value), onChange) {
        init {
            this + source.onUnsafeChange { value(from(it)) }
        }
    }
}

@JvmName("safeHasChangeValueIdentity")
fun <T> CSSafeHasChangeValue<T>.hasChangeValue(
    parent: CSHasRegistrations? = null, onChange: ArgFun<T>? = null,
): CSSafeHasChangeValue<T> = hasChangeValue(parent, from = { it }, onChange)

fun <Argument, Return> CSHasChangeValue<Argument>.safeHasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (Argument) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> = let { property ->
    object : CSHasChangeValueBase<Return>(parent, onChange) {
        @Volatile
        override var value: Return = from(property.value)

        init {
            this + property.onChange { value(from(it)) }
        }
    }
}

fun <Argument1, Argument2, Argument3, Argument4, Argument5> onChange(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSHasChangeValue<Argument3>,
    item4: CSHasChangeValue<Argument4>,
    item5: CSSafeHasChangeValue<Argument5>,
    onUnsafeChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
): CSRegistration {
    // Preserves the prior id: this was a CSHasChangeValue.Companion member, so
    // `className` resolved to the companion's simpleName ("Companion").
    val registrations = CSRegistrationsMap("Companion")
    val lock = Any()
    var value1 = item1.value
    var value2 = item2.value
    var value3 = item3.value
    var value4 = item4.value
    var value5 = item5.value

    fun fireChange(
        values: CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>
    ) {
        if (registrations.isActive) onUnsafeChange(
            values.first, values.second, values.third, values.fourth, values.fifth)
    }

    registrations.register(item1.onChange { newValue ->
        val values = synchronized(lock) {
            value1 = newValue
            CSQuintuple(value1, value2, value3, value4, value5)
        }
        fireChange(values)
    })
    registrations.register(item2.onChange { newValue ->
        val values = synchronized(lock) {
            value2 = newValue
            CSQuintuple(value1, value2, value3, value4, value5)
        }
        fireChange(values)
    })
    registrations.register(item3.onChange { newValue ->
        val values = synchronized(lock) {
            value3 = newValue
            CSQuintuple(value1, value2, value3, value4, value5)
        }
        fireChange(values)
    })
    registrations.register(item4.onChange { newValue ->
        val values = synchronized(lock) {
            value4 = newValue
            CSQuintuple(value1, value2, value3, value4, value5)
        }
        fireChange(values)
    })
    registrations.register(item5.onUnsafeChange { newValue ->
        val values = synchronized(lock) {
            value5 = newValue
            CSQuintuple(value1, value2, value3, value4, value5)
        }
        fireChange(values)
    })
    return registrations
}

@JvmName("onChangeWithSafeFifth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.onChange(
    onUnsafeChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
): CSRegistration
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    onChange(first, second, third, fourth, fifth, onUnsafeChange)

@JvmName("hasChangeValueWithSafeFifth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>? = null
): CSSafeHasChangeValue<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    hasChangeValue(parent, from = { item1, item2, item3, item4, item5 ->
        CSQuintuple(item1, item2, item3, item4, item5)
    }, onChange)

@JvmName("hasChangeValueFromWithSafeFifth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Return,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, from(first.value, second.value, third.value, fourth.value, fifth.value),
        onChange
    ) {
        init {
            this + onChange(first, second, third, fourth, fifth) { item1, item2, item3, item4, item5 ->
                value(from(item1, item2, item3, item4, item5))
            }
        }
    }

