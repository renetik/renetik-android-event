package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.tuples.CSQuadruple
import renetik.android.core.lang.tuples.to

fun <Argument1, Argument2, Argument3, Argument4, Return>
        CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (Argument1, Argument2, Argument3, Argument4) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> =
    object : CSHasChangeValueBase<Return>(parent, onChange) {
        override var value: Return =
            from(first.value, second.value, third.value, fourth.value)

        init {
            this + (first to second to third to fourth)
                .onChange { item1, item2, item3, item4 ->
                    value(from(item1, item2, item3, item4))
                }
        }
    }

fun <Argument1, Argument2, Argument3, Argument4, Return>
        CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (CSQuadruple<Argument1, Argument2, Argument3, Argument4>) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> =
    object : CSHasChangeValueBase<Return>(parent, onChange) {
        override var value: Return =
            from(CSQuadruple(first.value, second.value, third.value, fourth.value))

        init {
            this + (first to second to third to fourth)
                .onChange { item1, item2, item3, item4 ->
                    value(from(CSQuadruple(item1, item2, item3, item4)))
                }
        }
    }

fun <Argument1, Argument2, Argument3, Argument4>
        CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>? = null
): CSHasChangeValue<CSQuadruple<Argument1, Argument2, Argument3, Argument4>> =
    hasChangeValue(parent, from = { item1, item2, item3, item4 ->
        CSQuadruple(item1, item2, item3, item4)
    }, onChange)

fun <Argument1, Argument2, Argument3, Argument4> onChange(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSHasChangeValue<Argument3>,
    item4: CSHasChangeValue<Argument4>,
    onAction: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = listOf(item1, item2, item3, item4).onChange {
    onAction(item1.value, item2.value, item3.value, item4.value)
}

fun <Argument1, Argument2, Argument3, Argument4> CSQuadruple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>>.onChange(
    onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = onChange(first, second, third, fourth, onChange)

fun <Argument1, Argument2, Argument3, Argument4> action(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSHasChangeValue<Argument3>,
    item4: CSHasChangeValue<Argument4>,
    onAction: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = listOf(item1, item2, item3, item4).action {
    onAction(item1.value, item2.value, item3.value, item4.value)
}

fun <Argument1, Argument2, Argument3, Argument4> CSQuadruple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>>.action(
    onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = action(first, second, third, fourth, onChange)

fun <Argument1, Argument2, Argument3, Argument4> CSQuadruple<CSHasChangeValue<Argument1>,
        CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
        CSHasChangeValue<Argument4>>.actionLaterOnce(
    isActionNow: Boolean = false,
    onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = listOf(first, second, third, fourth).actionLaterOnce(isActionNow) {
    onChange(first.value, second.value, third.value, fourth.value)
}


fun <Argument1, Argument2, Argument3, Argument4> CSQuadruple<CSHasChangeValue<Argument1>,
        CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
        CSHasChangeValue<Argument4>>.onChangeLaterOnce(
    onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = listOf(first, second, third, fourth).onChangeLaterOnce {
    onChange(first.value, second.value, third.value, fourth.value)
}
