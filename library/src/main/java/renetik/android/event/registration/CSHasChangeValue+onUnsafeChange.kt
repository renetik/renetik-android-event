@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import renetik.android.core.kotlin.className
import renetik.android.core.lang.synchronized
import renetik.android.core.lang.tuples.CSQuadruple
import renetik.android.core.lang.tuples.CSQuintuple
import renetik.android.core.lang.tuples.CSSixtuple
import renetik.android.event.property.CSSafeHasChangeValue

fun <Argument1, Argument2, Item1, Item2> Pair<Item1, Item2>.onUnsafeChange(
    onUnsafeChange: (Argument1, Argument2) -> Unit,
): CSRegistration
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2> {
    val registrations = CSRegistrationsMap(className)
    val lock = Any()
    var value1 = first.value
    var value2 = second.value
    fun fireChange(values: Pair<Argument1, Argument2>) {
        if (registrations.isActive) onUnsafeChange(values.first, values.second)
    }
    registrations.register(first.onChange { newValue ->
        val values = synchronized<Any, Pair<Argument1, Argument2>>(lock) {
            value1 = newValue
            value1 to value2
        }
        fireChange(values)
    })
    registrations.register(second.onUnsafeChange { newValue ->
        val values = synchronized<Any, Pair<Argument1, Argument2>>(lock) {
            value2 = newValue
            value1 to value2
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
              Item3 : CSSafeHasChangeValue<Argument3> {
    val registrations = CSRegistrationsMap(className)
    val lock = Any()
    var value1 = first.value
    var value2 = second.value
    var value3 = third.value
    fun fireChange(values: Triple<Argument1, Argument2, Argument3>) {
        if (registrations.isActive) onUnsafeChange(values.first, values.second, values.third)
    }
    registrations.register(first.onChange { newValue ->
        val values = synchronized<Any, Triple<Argument1, Argument2, Argument3>>(lock) {
            value1 = newValue
            Triple(value1, value2, value3)
        }
        fireChange(values)
    })
    registrations.register(second.onChange { newValue ->
        val values = synchronized<Any, Triple<Argument1, Argument2, Argument3>>(lock) {
            value2 = newValue
            Triple(value1, value2, value3)
        }
        fireChange(values)
    })
    registrations.register(third.onUnsafeChange { newValue ->
        val values = synchronized<Any, Triple<Argument1, Argument2, Argument3>>(lock) {
            value3 = newValue
            Triple(value1, value2, value3)
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
              Item4 : CSSafeHasChangeValue<Argument4> {
    val registrations = CSRegistrationsMap(className)
    val lock = Any()
    var value1 = first.value
    var value2 = second.value
    var value3 = third.value
    var value4 = fourth.value
    fun fireChange(values: CSQuadruple<Argument1, Argument2, Argument3, Argument4>) {
        if (registrations.isActive) onUnsafeChange(
            values.first, values.second, values.third, values.fourth)
    }
    registrations.register(first.onChange { newValue ->
        val values =
            synchronized<Any, CSQuadruple<Argument1, Argument2, Argument3, Argument4>>(lock) {
                value1 = newValue
                CSQuadruple(value1, value2, value3, value4)
            }
        fireChange(values)
    })
    registrations.register(second.onChange { newValue ->
        val values =
            synchronized<Any, CSQuadruple<Argument1, Argument2, Argument3, Argument4>>(lock) {
                value2 = newValue
                CSQuadruple(value1, value2, value3, value4)
            }
        fireChange(values)
    })
    registrations.register(third.onChange { newValue ->
        val values =
            synchronized<Any, CSQuadruple<Argument1, Argument2, Argument3, Argument4>>(lock) {
                value3 = newValue
                CSQuadruple(value1, value2, value3, value4)
            }
        fireChange(values)
    })
    registrations.register(fourth.onUnsafeChange { newValue ->
        val values =
            synchronized<Any, CSQuadruple<Argument1, Argument2, Argument3, Argument4>>(lock) {
                value4 = newValue
                CSQuadruple(value1, value2, value3, value4)
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
              Item5 : CSSafeHasChangeValue<Argument5> {
    val registrations = CSRegistrationsMap(className)
    val lock = Any()
    var value1 = first.value
    var value2 = second.value
    var value3 = third.value
    var value4 = fourth.value
    var value5 = fifth.value
    fun fireChange(values: CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>) {
        if (registrations.isActive) onUnsafeChange(
            values.first, values.second, values.third, values.fourth, values.fifth)
    }
    registrations.register(first.onChange { newValue ->
        val values =
            synchronized<Any, CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>(
                lock) {
                value1 = newValue
                CSQuintuple(value1, value2, value3, value4, value5)
            }
        fireChange(values)
    })
    registrations.register(second.onChange { newValue ->
        val values =
            synchronized<Any, CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>(
                lock) {
                value2 = newValue
                CSQuintuple(value1, value2, value3, value4, value5)
            }
        fireChange(values)
    })
    registrations.register(third.onChange { newValue ->
        val values =
            synchronized<Any, CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>(
                lock) {
                value3 = newValue
                CSQuintuple(value1, value2, value3, value4, value5)
            }
        fireChange(values)
    })
    registrations.register(fourth.onChange { newValue ->
        val values =
            synchronized<Any, CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>(
                lock) {
                value4 = newValue
                CSQuintuple(value1, value2, value3, value4, value5)
            }
        fireChange(values)
    })
    registrations.register(fifth.onUnsafeChange { newValue ->
        val values =
            synchronized<Any, CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>(
                lock) {
                value5 = newValue
                CSQuintuple(value1, value2, value3, value4, value5)
            }
        fireChange(values)
    })
    return registrations
}

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
              Item5 : CSSafeHasChangeValue<Argument5> {
    val registrations = CSRegistrationsMap(className)
    val lock = Any()
    var value1 = first.value
    var value2 = second.value
    var value3 = third.value
    var value4 = fourth.value
    var value5 = fifth.value
    fun fireChange(values: CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>) {
        if (registrations.isActive) onUnsafeChange(
            values.first, values.second, values.third, values.fourth, values.fifth)
    }
    registrations.register(first.onChange { newValue ->
        val values =
            synchronized<Any, CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>(
                lock) {
                value1 = newValue
                CSQuintuple(value1, value2, value3, value4, value5)
            }
        fireChange(values)
    })
    registrations.register(second.onChange { newValue ->
        val values =
            synchronized<Any, CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>(
                lock) {
                value2 = newValue
                CSQuintuple(value1, value2, value3, value4, value5)
            }
        fireChange(values)
    })
    registrations.register(third.onUnsafeChange { newValue ->
        val values =
            synchronized<Any, CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>(
                lock) {
                value3 = newValue
                CSQuintuple(value1, value2, value3, value4, value5)
            }
        fireChange(values)
    })
    registrations.register(fourth.onChange { newValue ->
        val values =
            synchronized<Any, CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>(
                lock) {
                value4 = newValue
                CSQuintuple(value1, value2, value3, value4, value5)
            }
        fireChange(values)
    })
    registrations.register(fifth.onUnsafeChange { newValue ->
        val values =
            synchronized<Any, CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>(
                lock) {
                value5 = newValue
                CSQuintuple(value1, value2, value3, value4, value5)
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
              Item6 : CSSafeHasChangeValue<Argument6> {
    val registrations = CSRegistrationsMap(className)
    val lock = Any()
    var value1 = first.value
    var value2 = second.value
    var value3 = third.value
    var value4 = fourth.value
    var value5 = fifth.value
    var value6 = sixth.value
    fun fireChange(
        values: CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
    ) {
        if (registrations.isActive) onUnsafeChange(
            values.first, values.second, values.third, values.fourth,
            values.fifth, values.sixth)
    }
    registrations.register(first.onChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value1 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(second.onChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value2 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(third.onChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value3 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(fourth.onChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value4 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(fifth.onChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value5 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(sixth.onUnsafeChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value6 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    return registrations
}

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
              Item6 : CSSafeHasChangeValue<Argument6> {
    val registrations = CSRegistrationsMap(className)
    val lock = Any()
    var value1 = first.value
    var value2 = second.value
    var value3 = third.value
    var value4 = fourth.value
    var value5 = fifth.value
    var value6 = sixth.value
    fun fireChange(
        values: CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
    ) {
        if (registrations.isActive) onUnsafeChange(
            values.first, values.second, values.third, values.fourth,
            values.fifth, values.sixth)
    }
    registrations.register(first.onChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value1 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(second.onChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value2 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(third.onChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value3 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(fourth.onUnsafeChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value4 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(fifth.onChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value5 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(sixth.onUnsafeChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value6 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    return registrations
}

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
              Item6 : CSSafeHasChangeValue<Argument6> {
    val registrations = CSRegistrationsMap(className)
    val lock = Any()
    var value1 = first.value
    var value2 = second.value
    var value3 = third.value
    var value4 = fourth.value
    var value5 = fifth.value
    var value6 = sixth.value
    fun fireChange(
        values: CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
    ) {
        if (registrations.isActive) onUnsafeChange(
            values.first, values.second, values.third, values.fourth,
            values.fifth, values.sixth)
    }
    registrations.register(first.onChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value1 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(second.onChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value2 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(third.onUnsafeChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value3 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(fourth.onUnsafeChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value4 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(fifth.onChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value5 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    registrations.register(sixth.onUnsafeChange { newValue ->
        val values =
            synchronized<Any, CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>(
                lock) {
                value6 = newValue
                CSSixtuple(value1, value2, value3, value4, value5, value6)
            }
        fireChange(values)
    })
    return registrations
}