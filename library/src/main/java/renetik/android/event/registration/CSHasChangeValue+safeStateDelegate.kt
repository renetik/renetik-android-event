@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalAtomicApi::class)

package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.tuples.CSQuadruple
import renetik.android.core.lang.tuples.CSQuintuple
import renetik.android.core.lang.tuples.CSSixtuple
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.CSModel
import renetik.android.event.common.onMain
import renetik.android.event.property.CSSafeHasChangeValue
import renetik.android.event.property.CSSafeHasChangeValueBase
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.reflect.KProperty

// TODO?: eventUnsafeChange is never needed here it was added just because
//  CSSafeHasChangeValue requires it now
fun <T> CSHasChangeValue<T>.safe(
    parent: CSHasDestruct,
    onChange: ArgFun<T>? = null
): CSSafeHasChangeValue<T> = let { property ->
    object : CSModel(parent), CSSafeHasChangeValue<T> {
        private val _value = AtomicReference(property.value)
        val eventChange = event<T>()
        val eventUnsafeChange = event<T>()

        override var value: T
            get() = _value.load()
            set(value) = _value.store(value)

        override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
        override fun onChange(function: (T) -> Unit) = eventChange.listen(function)
        override fun onUnsafeChange(function: (T) -> Unit) = eventUnsafeChange.listen(function)

        init {
            this + property.onChange { newValue ->
                if (newValue != _value.exchange(newValue)) {
                    eventUnsafeChange.fire(newValue)
                    onMain {
                        onChange?.invoke(newValue)
                        eventChange.fire(newValue)
                    }
                }
            }
        }
    }
}

@JvmName("stateDelegateWithSafeSecond")
fun <Argument1, Argument2, Item1, Item2> Pair<Item1, Item2>.safeStateDelegate(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<Pair<Argument1, Argument2>>? = null
): CSSafeHasChangeValue<Pair<Argument1, Argument2>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2> =
    safeStateDelegate(parent, unsafeFrom = { item1, item2 -> item1 to item2 }, onChange)

@JvmName("stateDelegateFromWithSafeBoth")
fun <Argument1, Argument2, Return, Item1, Item2> Pair<Item1, Item2>.safeStateDelegate(
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
            this + onUnsafeChange { item1, item2 ->
                value(unsafeFrom(item1, item2))
            }
        }
    }

@JvmName("stateDelegateFromWithSafeSecond")
fun <Argument1, Argument2, Return, Item1, Item2> Pair<Item1, Item2>.safeStateDelegate(
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
            this + onUnsafeChange { item1, item2 ->
                value(unsafeFrom(item1, item2))
            }
        }
    }

@JvmName("stateDelegateWithSafeThird")
fun <Argument1, Argument2, Argument3, Item1, Item2, Item3>
        Triple<Item1, Item2, Item3>.safeStateDelegate(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<Triple<Argument1, Argument2, Argument3>>? = null
): CSSafeHasChangeValue<Triple<Argument1, Argument2, Argument3>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3> =
    safeStateDelegate(parent, unsafeFrom = ::Triple, onChange)

@JvmName("stateDelegateFromWithSafeThird")
fun <Argument1, Argument2, Argument3, Return, Item1, Item2, Item3>
        Triple<Item1, Item2, Item3>.safeStateDelegate(
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
            this + onUnsafeChange { item1, item2, item3 ->
                value(unsafeFrom(item1, item2, item3))
            }
        }
    }

@JvmName("stateDelegateWithSafeFourth")
fun <Argument1, Argument2, Argument3, Argument4,
        Item1, Item2, Item3, Item4>
        CSQuadruple<Item1, Item2, Item3, Item4>.safeStateDelegate(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>? = null
): CSSafeHasChangeValue<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4> =
    safeStateDelegate(parent, unsafeFrom = ::CSQuadruple, onChange)

@JvmName("stateDelegateFromWithSafeFourth")
fun <Argument1, Argument2, Argument3, Argument4, Return,
        Item1, Item2, Item3, Item4>
        CSQuadruple<Item1, Item2, Item3, Item4>.safeStateDelegate(
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
            this + onUnsafeChange { item1, item2, item3, item4 ->
                value(unsafeFrom(item1, item2, item3, item4))
            }
        }
    }

@JvmName("stateDelegateWithSafeFifth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.safeStateDelegate(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>? = null
): CSSafeHasChangeValue<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    safeStateDelegate(parent, unsafeFrom = ::CSQuintuple, onChange)

@JvmName("stateDelegateWithSafeThirdAndFifth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.safeStateDelegate(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>? = null
): CSSafeHasChangeValue<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    safeStateDelegate(parent, unsafeFrom = ::CSQuintuple, onChange)

@JvmName("stateDelegateFromWithSafeFifth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Return,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.safeStateDelegate(
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
            this + onUnsafeChange { item1, item2, item3, item4, item5 ->
                value(unsafeFrom(item1, item2, item3, item4, item5))
            }
        }
    }

@JvmName("stateDelegateFromWithSafeThirdAndFifth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Return,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.safeStateDelegate(
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
            this + onUnsafeChange { item1, item2, item3, item4, item5 ->
                value(unsafeFrom(item1, item2, item3, item4, item5))
            }
        }
    }

@JvmName("stateDelegateWithSafeSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.safeStateDelegate(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>? = null
): CSSafeHasChangeValue<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    safeStateDelegate(parent, unsafeFrom = { item1, item2, item3, item4, item5, item6 ->
        CSSixtuple(item1, item2, item3, item4, item5, item6)
    }, onChange)

@JvmName("stateDelegateWithSafeFourthAndSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.safeStateDelegate(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>? = null
): CSSafeHasChangeValue<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    safeStateDelegate(parent, unsafeFrom = ::CSSixtuple, onChange)

@JvmName("stateDelegateWithSafeThirdFourthAndSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.safeStateDelegate(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>? = null
): CSSafeHasChangeValue<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    safeStateDelegate(parent, unsafeFrom = { item1, item2, item3, item4, item5, item6 ->
        CSSixtuple(item1, item2, item3, item4, item5, item6)
    }, onChange)

@JvmName("stateDelegateFromWithSafeSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Return,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.safeStateDelegate(
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
            this + onUnsafeChange { item1, item2, item3, item4, item5, item6 ->
                value(unsafeFrom(item1, item2, item3, item4, item5, item6))
            }
        }
    }

@JvmName("stateDelegateFromWithSafeFourthAndSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Return,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.safeStateDelegate(
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
            this + onUnsafeChange { item1, item2, item3, item4, item5, item6 ->
                value(unsafeFrom(item1, item2, item3, item4, item5, item6))
            }
        }
    }

@JvmName("stateDelegateFromWithSafeThirdFourthAndSixth")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Return,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.safeStateDelegate(
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
            this + onUnsafeChange { item1, item2, item3, item4, item5, item6 ->
                value(unsafeFrom(item1, item2, item3, item4, item5, item6))
            }
        }
    }
