@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.synchronized
import renetik.android.core.lang.tuples.CSQuadruple
import renetik.android.core.lang.tuples.CSQuintuple
import renetik.android.core.lang.tuples.CSSixtuple
import renetik.android.core.lang.value.CSSafeValue
import renetik.android.event.property.CSSafeHasChangeValue
import renetik.android.event.property.CSSafeHasChangeValueBase
import kotlin.reflect.KProperty

fun <T> CSHasChangeValue<T>.safeValue(
    parent: CSHasRegistrations): CSSafeValue<T> = let { property ->
    object : CSSafeValue<T> {
        @Volatile
        override var value: T = property.value
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

        init {
            parent + property.onChange { value = it }
        }
    }
}

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

fun <Argument1, Argument2> onUnsafeChange(
    item1: CSHasChangeValue<Argument1>,
    item2: CSSafeHasChangeValue<Argument2>,
    onUnsafeChange: (Argument1, Argument2) -> Unit,
): CSRegistration {
    val registrations = CSRegistrationsMap("Companion")
    val lock = Any()
    var value1 = item1.value
    var value2 = item2.value

    fun fireChange(values: Pair<Argument1, Argument2>) {
        if (registrations.isActive) onUnsafeChange(values.first, values.second)
    }

    registrations.register(item1.onChange { newValue ->
        val values = synchronized(lock) {
            value1 = newValue
            value1 to value2
        }
        fireChange(values)
    })
    registrations.register(item2.onUnsafeChange { newValue ->
        val values = synchronized(lock) {
            value2 = newValue
            value1 to value2
        }
        fireChange(values)
    })
    return registrations
}

fun <Argument1, Argument2> onUnsafeChange(
    item1: CSSafeHasChangeValue<Argument1>,
    item2: CSSafeHasChangeValue<Argument2>,
    onUnsafeChange: (Argument1, Argument2) -> Unit,
): CSRegistration {
    val registrations = CSRegistrationsMap("Companion")
    val lock = Any()
    var value1 = item1.value
    var value2 = item2.value

    fun fireChange(values: Pair<Argument1, Argument2>) {
        if (registrations.isActive) onUnsafeChange(values.first, values.second)
    }

    registrations.register(item1.onUnsafeChange { newValue ->
        val values = synchronized(lock) {
            value1 = newValue
            value1 to value2
        }
        fireChange(values)
    })
    registrations.register(item2.onUnsafeChange { newValue ->
        val values = synchronized(lock) {
            value2 = newValue
            value1 to value2
        }
        fireChange(values)
    })
    return registrations
}

@JvmName("onUnsafeChangeWithSafeSecond")
fun <Argument1, Argument2, Item1, Item2> Pair<Item1, Item2>.onUnsafeChange(
    onUnsafeChange: (Argument1, Argument2) -> Unit,
): CSRegistration
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2> =
    onUnsafeChange(first, second, onUnsafeChange)

@JvmName("hasChangeValueWithSafeSecond")
fun <Argument1, Argument2, Item1, Item2> Pair<Item1, Item2>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<Pair<Argument1, Argument2>>? = null
): CSSafeHasChangeValue<Pair<Argument1, Argument2>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2> =
    hasChangeValue(parent, unsafeFrom = { item1, item2 -> item1 to item2 }, onChange)

@JvmName("hasChangeValueFromWithSafeBoth")
fun <Argument1, Argument2, Return, Item1, Item2> Pair<Item1, Item2>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    unsafeFrom: (Argument1, Argument2) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return>
        where Item1 : CSSafeHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFrom(first.value, second.value), onChange
    ) {
        init {
            this + onUnsafeChange(first, second) { item1, item2 ->
                value(unsafeFrom(item1, item2))
            }
        }
    }

@JvmName("hasChangeValueFromWithSafeSecond")
fun <Argument1, Argument2, Return, Item1, Item2> Pair<Item1, Item2>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    unsafeFrom: (Argument1, Argument2) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFrom(first.value, second.value), onChange
    ) {
        init {
            this + onUnsafeChange(first, second) { item1, item2 ->
                value(unsafeFrom(item1, item2))
            }
        }
    }

fun <Argument1, Argument2, Argument3> onUnsafeChange(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSSafeHasChangeValue<Argument3>,
    onUnsafeChange: (Argument1, Argument2, Argument3) -> Unit,
): CSRegistration {
    val registrations = CSRegistrationsMap("Companion")
    val lock = Any()
    var value1 = item1.value
    var value2 = item2.value
    var value3 = item3.value

    fun fireChange(values: Triple<Argument1, Argument2, Argument3>) {
        if (registrations.isActive) onUnsafeChange(values.first, values.second, values.third)
    }

    registrations.register(item1.onChange { newValue ->
        val values = synchronized(lock) {
            value1 = newValue
            Triple(value1, value2, value3)
        }
        fireChange(values)
    })
    registrations.register(item2.onChange { newValue ->
        val values = synchronized(lock) {
            value2 = newValue
            Triple(value1, value2, value3)
        }
        fireChange(values)
    })
    registrations.register(item3.onUnsafeChange { newValue ->
        val values = synchronized(lock) {
            value3 = newValue
            Triple(value1, value2, value3)
        }
        fireChange(values)
    })
    return registrations
}

@JvmName("onUnsafeChangeWithSafeThird")
fun <Argument1, Argument2, Argument3, Item1, Item2, Item3>
        Triple<Item1, Item2, Item3>.onUnsafeChange(
    onUnsafeChange: (Argument1, Argument2, Argument3) -> Unit,
): CSRegistration
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3> =
    onUnsafeChange(first, second, third, onUnsafeChange)

@JvmName("hasChangeValueWithSafeThird")
fun <Argument1, Argument2, Argument3, Item1, Item2, Item3>
        Triple<Item1, Item2, Item3>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<Triple<Argument1, Argument2, Argument3>>? = null
): CSSafeHasChangeValue<Triple<Argument1, Argument2, Argument3>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3> =
    hasChangeValue(parent, unsafeFrom = { item1, item2, item3 ->
        Triple(item1, item2, item3)
    }, onChange)

@JvmName("hasChangeValueFromWithSafeThird")
fun <Argument1, Argument2, Argument3, Return, Item1, Item2, Item3>
        Triple<Item1, Item2, Item3>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    unsafeFrom: (Argument1, Argument2, Argument3) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFrom(first.value, second.value, third.value), onChange
    ) {
        init {
            this + onUnsafeChange(first, second, third) { item1, item2, item3 ->
                value(unsafeFrom(item1, item2, item3))
            }
        }
    }

fun <Argument1, Argument2, Argument3, Argument4> onUnsafeChange(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSHasChangeValue<Argument3>,
    item4: CSSafeHasChangeValue<Argument4>,
    onUnsafeChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration {
    val registrations = CSRegistrationsMap("Companion")
    val lock = Any()
    var value1 = item1.value
    var value2 = item2.value
    var value3 = item3.value
    var value4 = item4.value

    fun fireChange(values: CSQuadruple<Argument1, Argument2, Argument3, Argument4>) {
        if (registrations.isActive) onUnsafeChange(
            values.first, values.second, values.third, values.fourth)
    }

    registrations.register(item1.onChange { newValue ->
        val values = synchronized(lock) {
            value1 = newValue
            CSQuadruple(value1, value2, value3, value4)
        }
        fireChange(values)
    })
    registrations.register(item2.onChange { newValue ->
        val values = synchronized(lock) {
            value2 = newValue
            CSQuadruple(value1, value2, value3, value4)
        }
        fireChange(values)
    })
    registrations.register(item3.onChange { newValue ->
        val values = synchronized(lock) {
            value3 = newValue
            CSQuadruple(value1, value2, value3, value4)
        }
        fireChange(values)
    })
    registrations.register(item4.onUnsafeChange { newValue ->
        val values = synchronized(lock) {
            value4 = newValue
            CSQuadruple(value1, value2, value3, value4)
        }
        fireChange(values)
    })
    return registrations
}

@JvmName("onUnsafeChangeWithSafeFourth")
fun <Argument1, Argument2, Argument3, Argument4,
        Item1, Item2, Item3, Item4>
        CSQuadruple<Item1, Item2, Item3, Item4>.onUnsafeChange(
    onUnsafeChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4> =
    onUnsafeChange(first, second, third, fourth, onUnsafeChange)

@JvmName("hasChangeValueWithSafeFourth")
fun <Argument1, Argument2, Argument3, Argument4,
        Item1, Item2, Item3, Item4>
        CSQuadruple<Item1, Item2, Item3, Item4>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>? = null
): CSSafeHasChangeValue<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4> =
    hasChangeValue(parent, unsafeFrom = { item1, item2, item3, item4 ->
        CSQuadruple(item1, item2, item3, item4)
    }, onChange)

@JvmName("hasChangeValueFromWithSafeFourth")
fun <Argument1, Argument2, Argument3, Argument4, Return,
        Item1, Item2, Item3, Item4>
        CSQuadruple<Item1, Item2, Item3, Item4>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    unsafeFrom: (Argument1, Argument2, Argument3, Argument4) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFrom(first.value, second.value, third.value, fourth.value), onChange
    ) {
        init {
            this + onUnsafeChange(first, second, third, fourth) { item1, item2, item3, item4 ->
                value(unsafeFrom(item1, item2, item3, item4))
            }
        }
    }

fun <Argument1, Argument2, Argument3, Argument4, Argument5> onUnsafeChange(
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

fun <Argument1, Argument2, Argument3, Argument4, Argument5> onUnsafeChange(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSSafeHasChangeValue<Argument3>,
    item4: CSHasChangeValue<Argument4>,
    item5: CSSafeHasChangeValue<Argument5>,
    onUnsafeChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
): CSRegistration {
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
    registrations.register(item3.onUnsafeChange { newValue ->
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

@JvmName("onUnsafeChangeWithSafeFifth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.onUnsafeChange(
    onUnsafeChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
): CSRegistration
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    onUnsafeChange(first, second, third, fourth, fifth, onUnsafeChange)

@JvmName("onUnsafeChangeWithSafeThirdAndFifth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.onUnsafeChange(
    onUnsafeChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
): CSRegistration
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    onUnsafeChange(first, second, third, fourth, fifth, onUnsafeChange)

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
    hasChangeValue(parent, unsafeFrom = { item1, item2, item3, item4, item5 ->
        CSQuintuple(item1, item2, item3, item4, item5)
    }, onChange)

@JvmName("hasChangeValueWithSafeThirdAndFifth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>? = null
): CSSafeHasChangeValue<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    hasChangeValue(parent, unsafeFrom = { item1, item2, item3, item4, item5 ->
        CSQuintuple(item1, item2, item3, item4, item5)
    }, onChange)

@JvmName("hasChangeValueFromWithSafeFifth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Return,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    unsafeFrom: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFrom(first.value, second.value, third.value, fourth.value, fifth.value),
        onChange
    ) {
        init {
            this + onUnsafeChange(first, second, third, fourth,
                fifth) { item1, item2, item3, item4, item5 ->
                value(unsafeFrom(item1, item2, item3, item4, item5))
            }
        }
    }

@JvmName("hasChangeValueFromWithSafeThirdAndFifth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Return,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    unsafeFrom: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFrom(first.value, second.value, third.value, fourth.value, fifth.value),
        onChange
    ) {
        init {
            this + onUnsafeChange(first, second, third, fourth,
                fifth) { item1, item2, item3, item4, item5 ->
                value(unsafeFrom(item1, item2, item3, item4, item5))
            }
        }
    }

fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6> onUnsafeChange(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSHasChangeValue<Argument3>,
    item4: CSHasChangeValue<Argument4>,
    item5: CSHasChangeValue<Argument5>,
    item6: CSSafeHasChangeValue<Argument6>,
    onUnsafeChange: (
        Argument1, Argument2, Argument3, Argument4, Argument5, Argument6
    ) -> Unit,
): CSRegistration {
    val registrations = CSRegistrationsMap("Companion")
    val lock = Any()
    var value1 = item1.value
    var value2 = item2.value
    var value3 = item3.value
    var value4 = item4.value
    var value5 = item5.value
    var value6 = item6.value

    fun fireChange(
        values: CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
    ) {
        if (registrations.isActive) onUnsafeChange(
            values.first, values.second, values.third, values.fourth,
            values.fifth, values.sixth)
    }

    registrations.register(item1.onChange { newValue ->
        val values = synchronized(lock) {
            value1 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item2.onChange { newValue ->
        val values = synchronized(lock) {
            value2 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item3.onChange { newValue ->
        val values = synchronized(lock) {
            value3 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item4.onChange { newValue ->
        val values = synchronized(lock) {
            value4 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item5.onChange { newValue ->
        val values = synchronized(lock) {
            value5 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item6.onUnsafeChange { newValue ->
        val values = synchronized(lock) {
            value6 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    return registrations
}

fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6> onUnsafeChange(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSHasChangeValue<Argument3>,
    item4: CSSafeHasChangeValue<Argument4>,
    item5: CSHasChangeValue<Argument5>,
    item6: CSSafeHasChangeValue<Argument6>,
    onUnsafeChange: (
        Argument1, Argument2, Argument3, Argument4, Argument5, Argument6
    ) -> Unit,
): CSRegistration {
    val registrations = CSRegistrationsMap("Companion")
    val lock = Any()
    var value1 = item1.value
    var value2 = item2.value
    var value3 = item3.value
    var value4 = item4.value
    var value5 = item5.value
    var value6 = item6.value

    fun fireChange(
        values: CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
    ) {
        if (registrations.isActive) onUnsafeChange(
            values.first, values.second, values.third, values.fourth,
            values.fifth, values.sixth)
    }

    registrations.register(item1.onChange { newValue ->
        val values = synchronized(lock) {
            value1 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item2.onChange { newValue ->
        val values = synchronized(lock) {
            value2 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item3.onChange { newValue ->
        val values = synchronized(lock) {
            value3 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item4.onUnsafeChange { newValue ->
        val values = synchronized(lock) {
            value4 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item5.onChange { newValue ->
        val values = synchronized(lock) {
            value5 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item6.onUnsafeChange { newValue ->
        val values = synchronized(lock) {
            value6 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    return registrations
}

fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6> onUnsafeChange(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSSafeHasChangeValue<Argument3>,
    item4: CSSafeHasChangeValue<Argument4>,
    item5: CSHasChangeValue<Argument5>,
    item6: CSSafeHasChangeValue<Argument6>,
    onUnsafeChange: (
        Argument1, Argument2, Argument3, Argument4, Argument5, Argument6
    ) -> Unit,
): CSRegistration {
    val registrations = CSRegistrationsMap("Companion")
    val lock = Any()
    var value1 = item1.value
    var value2 = item2.value
    var value3 = item3.value
    var value4 = item4.value
    var value5 = item5.value
    var value6 = item6.value

    fun fireChange(
        values: CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
    ) {
        if (registrations.isActive) onUnsafeChange(
            values.first, values.second, values.third, values.fourth,
            values.fifth, values.sixth)
    }

    registrations.register(item1.onChange { newValue ->
        val values = synchronized(lock) {
            value1 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item2.onChange { newValue ->
        val values = synchronized(lock) {
            value2 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item3.onUnsafeChange { newValue ->
        val values = synchronized(lock) {
            value3 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item4.onUnsafeChange { newValue ->
        val values = synchronized(lock) {
            value4 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item5.onChange { newValue ->
        val values = synchronized(lock) {
            value5 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    registrations.register(item6.onUnsafeChange { newValue ->
        val values = synchronized(lock) {
            value6 = newValue
            CSSixtuple(value1, value2, value3, value4, value5, value6)
        }
        fireChange(values)
    })
    return registrations
}

@JvmName("onUnsafeChangeWithSafeSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.onUnsafeChange(
    onUnsafeChange: (
        Argument1, Argument2, Argument3, Argument4, Argument5, Argument6
    ) -> Unit,
): CSRegistration
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    onUnsafeChange(first, second, third, fourth, fifth, sixth, onUnsafeChange)

@JvmName("onUnsafeChangeWithSafeFourthAndSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.onUnsafeChange(
    onUnsafeChange: (
        Argument1, Argument2, Argument3, Argument4, Argument5, Argument6
    ) -> Unit,
): CSRegistration
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    onUnsafeChange(first, second, third, fourth, fifth, sixth, onUnsafeChange)

@JvmName("onUnsafeChangeWithSafeThirdFourthAndSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.onUnsafeChange(
    onUnsafeChange: (
        Argument1, Argument2, Argument3, Argument4, Argument5, Argument6
    ) -> Unit,
): CSRegistration
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    onUnsafeChange(first, second, third, fourth, fifth, sixth, onUnsafeChange)

@JvmName("hasChangeValueWithSafeSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>? = null
): CSSafeHasChangeValue<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    hasChangeValue(parent, unsafeFrom = { item1, item2, item3, item4, item5, item6 ->
        CSSixtuple(item1, item2, item3, item4, item5, item6)
    }, onChange)

@JvmName("hasChangeValueWithSafeFourthAndSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>? = null
): CSSafeHasChangeValue<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    hasChangeValue(parent, unsafeFrom = { item1, item2, item3, item4, item5, item6 ->
        CSSixtuple(item1, item2, item3, item4, item5, item6)
    }, onChange)

@JvmName("hasChangeValueWithSafeThirdFourthAndSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>? = null
): CSSafeHasChangeValue<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    hasChangeValue(parent, unsafeFrom = { item1, item2, item3, item4, item5, item6 ->
        CSSixtuple(item1, item2, item3, item4, item5, item6)
    }, onChange)

@JvmName("hasChangeValueFromWithSafeSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Return,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    unsafeFrom: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFrom(first.value, second.value, third.value,
            fourth.value, fifth.value, sixth.value), onChange) {
        init {
            this + onUnsafeChange(first, second, third, fourth, fifth,
                sixth) { item1, item2, item3, item4, item5, item6 ->
                value(unsafeFrom(item1, item2, item3, item4, item5, item6))
            }
        }
    }

@JvmName("hasChangeValueFromWithSafeFourthAndSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Return,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    unsafeFrom: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFrom(first.value, second.value, third.value,
            fourth.value, fifth.value, sixth.value), onChange) {
        init {
            this + onUnsafeChange(first, second, third, fourth, fifth,
                sixth) { item1, item2, item3, item4, item5, item6 ->
                value(unsafeFrom(item1, item2, item3, item4, item5, item6))
            }
        }
    }

@JvmName("hasChangeValueFromWithSafeThirdFourthAndSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Return,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    unsafeFrom: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFrom(first.value, second.value, third.value,
            fourth.value, fifth.value, sixth.value), onChange) {
        init {
            this + onUnsafeChange(first, second, third, fourth, fifth,
                sixth) { item1, item2, item3, item4, item5, item6 ->
                value(unsafeFrom(item1, item2, item3, item4, item5, item6))
            }
        }
    }
