@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalAtomicApi::class)

package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.tuples.CSQuadruple
import renetik.android.core.lang.tuples.CSQuintuple
import renetik.android.core.lang.tuples.CSSixtuple
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.property.CSSafeHasChangeValue
import renetik.android.event.property.CSSafeHasChangeValueBase
import kotlin.concurrent.atomics.ExperimentalAtomicApi

// TODO?: eventUnsafeChange is never needed here but CSSafeHasChangeValue requires it
fun <T> CSHasChangeValue<T>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<T>? = null
): CSSafeHasChangeValue<T> =
    object : CSSafeHasChangeValueBase<T>(parent, value, onChange) {
        init {
            this + this.onChange { newValue -> value(newValue) }
        }
    }

@JvmName("safeStateDelegateWith2Safe1")
fun <Argument1, Argument2, Item1, Item2> Pair<Item1, Item2>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<Pair<Argument1, Argument2>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Pair<Argument1, Argument2>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2> =
    safeStateDelegate(parent, unsafeFromValues = { item1, item2 -> item1 to item2 }, onChange, delivery)

@JvmName("safeStateDelegateFromWith2Safe1")
fun <Argument1, Argument2, Return, Item1, Item2> Pair<Item1, Item2>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value), onChange
    ) {
        init {
            this + onUnsafeChange(delivery) { item1, item2 ->
                value(unsafeFromValues(item1, item2))
            }
        }
    }

@JvmName("safeStateDelegateWith3Safe1")
fun <Argument1, Argument2, Argument3, Item1, Item2, Item3>
        Triple<Item1, Item2, Item3>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<Triple<Argument1, Argument2, Argument3>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Triple<Argument1, Argument2, Argument3>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3> =
    safeStateDelegate(parent, unsafeFromValues = ::Triple, onChange, delivery)

@JvmName("safeStateDelegateFromWith3Safe1")
fun <Argument1, Argument2, Argument3, Return, Item1, Item2, Item3>
        Triple<Item1, Item2, Item3>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2, Argument3) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value, third.value), onChange
    ) {
        init {
            this + onUnsafeChange(delivery) { item1, item2, item3 ->
                value(unsafeFromValues(item1, item2, item3))
            }
        }
    }

@JvmName("safeStateDelegateWith4Safe1")
fun <Argument1, Argument2, Argument3, Argument4,
        Item1, Item2, Item3, Item4>
        CSQuadruple<Item1, Item2, Item3, Item4>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4> =
    safeStateDelegate(parent, unsafeFromValues = ::CSQuadruple, onChange, delivery)

@JvmName("safeStateDelegateFromWith4Safe1")
fun <Argument1, Argument2, Argument3, Argument4, Return,
        Item1, Item2, Item3, Item4>
        CSQuadruple<Item1, Item2, Item3, Item4>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2, Argument3, Argument4) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value, third.value, fourth.value), onChange
    ) {
        init {
            this + onUnsafeChange(delivery) { item1, item2, item3, item4 ->
                value(unsafeFromValues(item1, item2, item3, item4))
            }
        }
    }

@JvmName("safeStateDelegateWith5Safe1")
fun <Argument1, Argument2, Argument3, Argument4, Argument5,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    safeStateDelegate(parent, unsafeFromValues = ::CSQuintuple, onChange, delivery)

@JvmName("safeStateDelegateFromWith5Safe1")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Return,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value, third.value, fourth.value, fifth.value),
        onChange
    ) {
        init {
            this + onUnsafeChange(delivery) { item1, item2, item3, item4, item5 ->
                value(unsafeFromValues(item1, item2, item3, item4, item5))
            }
        }
    }

@JvmName("safeStateDelegateWith6Safe1")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    safeStateDelegate(parent, unsafeFromValues = ::CSSixtuple, onChange, delivery)

@JvmName("safeStateDelegateFromWith6Safe1")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Return,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value, third.value,
            fourth.value, fifth.value, sixth.value), onChange) {
        init {
            this + onUnsafeChange(delivery) { item1, item2, item3, item4, item5, item6 ->
                value(unsafeFromValues(item1, item2, item3, item4, item5, item6))
            }
        }
    }

@JvmName("safeStateDelegateWith2Safe2")
fun <Argument1, Argument2, Item1, Item2> Pair<Item1, Item2>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<Pair<Argument1, Argument2>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Pair<Argument1, Argument2>>
        where Item1 : CSSafeHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2> =
    safeStateDelegate(parent, unsafeFromValues = { item1, item2 -> item1 to item2 }, onChange, delivery)

@JvmName("safeStateDelegateFromWith2Safe2")
fun <Argument1, Argument2, Return, Item1, Item2> Pair<Item1, Item2>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSSafeHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value), onChange
    ) {
        init {
            this + onUnsafeChange(delivery) { item1, item2 ->
                value(unsafeFromValues(item1, item2))
            }
        }
    }

@JvmName("safeStateDelegateWith3Safe2")
fun <Argument1, Argument2, Argument3, Item1, Item2, Item3>
        Triple<Item1, Item2, Item3>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<Triple<Argument1, Argument2, Argument3>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Triple<Argument1, Argument2, Argument3>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3> =
    safeStateDelegate(parent, unsafeFromValues = ::Triple, onChange, delivery)

@JvmName("safeStateDelegateFromWith3Safe2")
fun <Argument1, Argument2, Argument3, Return, Item1, Item2, Item3>
        Triple<Item1, Item2, Item3>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2, Argument3) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value, third.value), onChange
    ) {
        init {
            this + onUnsafeChange(delivery) { item1, item2, item3 ->
                value(unsafeFromValues(item1, item2, item3))
            }
        }
    }

@JvmName("safeStateDelegateWith4Safe2")
fun <Argument1, Argument2, Argument3, Argument4,
        Item1, Item2, Item3, Item4>
        CSQuadruple<Item1, Item2, Item3, Item4>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4> =
    safeStateDelegate(parent, unsafeFromValues = ::CSQuadruple, onChange, delivery)

@JvmName("safeStateDelegateFromWith4Safe2")
fun <Argument1, Argument2, Argument3, Argument4, Return,
        Item1, Item2, Item3, Item4>
        CSQuadruple<Item1, Item2, Item3, Item4>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2, Argument3, Argument4) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value, third.value, fourth.value), onChange
    ) {
        init {
            this + onUnsafeChange(delivery) { item1, item2, item3, item4 ->
                value(unsafeFromValues(item1, item2, item3, item4))
            }
        }
    }

@JvmName("safeStateDelegateWith5Safe2")
fun <Argument1, Argument2, Argument3, Argument4, Argument5,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    safeStateDelegate(parent, unsafeFromValues = ::CSQuintuple, onChange, delivery)

@JvmName("safeStateDelegateFromWith5Safe2")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Return,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value, third.value, fourth.value, fifth.value),
        onChange
    ) {
        init {
            this + onUnsafeChange(delivery) { item1, item2, item3, item4, item5 ->
                value(unsafeFromValues(item1, item2, item3, item4, item5))
            }
        }
    }

@JvmName("safeStateDelegateWith6Safe2")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    safeStateDelegate(parent, unsafeFromValues = ::CSSixtuple, onChange, delivery)

@JvmName("safeStateDelegateFromWith6Safe2")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Return,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value, third.value,
            fourth.value, fifth.value, sixth.value), onChange) {
        init {
            this + onUnsafeChange(delivery) { item1, item2, item3, item4, item5, item6 ->
                value(unsafeFromValues(item1, item2, item3, item4, item5, item6))
            }
        }
    }

@JvmName("safeStateDelegateWith3Safe3")
fun <Argument1, Argument2, Argument3, Item1, Item2, Item3>
        Triple<Item1, Item2, Item3>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<Triple<Argument1, Argument2, Argument3>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Triple<Argument1, Argument2, Argument3>>
        where Item1 : CSSafeHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3> =
    safeStateDelegate(parent, unsafeFromValues = ::Triple, onChange, delivery)

@JvmName("safeStateDelegateFromWith3Safe3")
fun <Argument1, Argument2, Argument3, Return, Item1, Item2, Item3>
        Triple<Item1, Item2, Item3>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2, Argument3) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSSafeHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value, third.value), onChange
    ) {
        init {
            this + onUnsafeChange(delivery) { item1, item2, item3 ->
                value(unsafeFromValues(item1, item2, item3))
            }
        }
    }

@JvmName("safeStateDelegateWith4Safe3")
fun <Argument1, Argument2, Argument3, Argument4,
        Item1, Item2, Item3, Item4>
        CSQuadruple<Item1, Item2, Item3, Item4>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4> =
    safeStateDelegate(parent, unsafeFromValues = ::CSQuadruple, onChange, delivery)

@JvmName("safeStateDelegateFromWith4Safe3")
fun <Argument1, Argument2, Argument3, Argument4, Return,
        Item1, Item2, Item3, Item4>
        CSQuadruple<Item1, Item2, Item3, Item4>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2, Argument3, Argument4) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSSafeHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value, third.value, fourth.value), onChange
    ) {
        init {
            this + onUnsafeChange(delivery) { item1, item2, item3, item4 ->
                value(unsafeFromValues(item1, item2, item3, item4))
            }
        }
    }

@JvmName("safeStateDelegateWith5Safe3")
fun <Argument1, Argument2, Argument3, Argument4, Argument5,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    safeStateDelegate(parent, unsafeFromValues = ::CSQuintuple, onChange, delivery)

@JvmName("safeStateDelegateFromWith5Safe3")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Return,
        Item1, Item2, Item3, Item4, Item5>
        CSQuintuple<Item1, Item2, Item3, Item4, Item5>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSSafeHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value, third.value, fourth.value, fifth.value),
        onChange
    ) {
        init {
            this + onUnsafeChange(delivery) { item1, item2, item3, item4, item5 ->
                value(unsafeFromValues(item1, item2, item3, item4, item5))
            }
        }
    }

@JvmName("safeStateDelegateWith6Safe3")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.safeStateDelegate(
    parent: CSHasDestruct,
    onChange: ArgFun<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    safeStateDelegate(parent, unsafeFromValues = ::CSSixtuple, onChange, delivery)

@JvmName("safeStateDelegateFromWith6Safe3")
fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Return,
        Item1, Item2, Item3, Item4, Item5, Item6>
        CSSixtuple<Item1, Item2, Item3, Item4, Item5, Item6>.safeStateDelegate(
    parent: CSHasDestruct,
    unsafeFromValues: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Return,
    onChange: ArgFun<Return>? = null,
    delivery: CSSafeChangeDelivery = CSSafeChangeDelivery.Conflated,
): CSSafeHasChangeValue<Return>
        where Item1 : CSHasChangeValue<Argument1>,
              Item2 : CSHasChangeValue<Argument2>,
              Item3 : CSHasChangeValue<Argument3>,
              Item4 : CSSafeHasChangeValue<Argument4>,
              Item5 : CSSafeHasChangeValue<Argument5>,
              Item6 : CSSafeHasChangeValue<Argument6> =
    object : CSSafeHasChangeValueBase<Return>(
        parent, unsafeFromValues(first.value, second.value, third.value,
            fourth.value, fifth.value, sixth.value), onChange) {
        init {
            this + onUnsafeChange(delivery) { item1, item2, item3, item4, item5, item6 ->
                value(unsafeFromValues(item1, item2, item3, item4, item5, item6))
            }
        }
    }
