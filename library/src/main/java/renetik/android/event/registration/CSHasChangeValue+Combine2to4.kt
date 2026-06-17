package renetik.android.event.registration

import kotlinx.coroutines.Dispatchers.Main
import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.SusFun
import renetik.android.core.lang.tuples.CSQuadruple
import renetik.android.core.lang.tuples.to
import kotlin.coroutines.CoroutineContext

fun <Argument1, Argument2, Return> hasChangeValue(
    parent: CSHasRegistrations? = null,
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    from: (Argument1, Argument2) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> =
    object : CSHasChangeValueBase<Return>(parent, onChange) {
        override var value: Return = from(item1.value, item2.value)

        init {
            this + (item1 to item2).onChange { item1, item2 ->
                value(from(item1, item2))
            }
        }
    }

fun <Argument1, Argument2, Return>
        Pair<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (Argument1, Argument2) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> =
    hasChangeValue(parent, first, second, from, onChange)

fun <Argument1, Argument2> onChange(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    onChange: (Argument1, Argument2) -> Unit,
): CSRegistration = listOf(item1, item2).onChange {
    onChange(item1.value, item2.value)
}

fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.onChange(
    onChange: (Argument1, Argument2) -> Unit,
): CSRegistration = onChange(first, second, onChange)

fun <Argument1, Argument2> Triple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChange<*>>.onChange(
    onChange: (Argument1, Argument2) -> Unit,
): CSRegistration = listOf(first, second, third).onChange {
    onChange(first.value, second.value)
}

fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.onChange(
    onChange: () -> Unit,
): CSRegistration = onChange(first, second) { _, _ -> onChange() }

fun <Argument1, Argument2> action(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    onAction: (Argument1, Argument2) -> Unit,
): CSRegistration = listOf(item1, item2).action {
    onAction(item1.value, item2.value)
}

fun <Argument1, Argument2> Pair<CSHasChangeValue<out Argument1>, CSHasChangeValue<out Argument2>>.action(
    onAction: (Argument1, Argument2) -> Unit,
): CSRegistration = action(first, second) { first, second -> onAction(first, second) }


fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.action(
    onAction: () -> Unit,
): CSRegistration = action(first, second) { _, _ -> onAction() }


fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.onChangeLaterOnce(
    onChange: () -> Unit,
): CSRegistration = listOf(first, second).onChangeLaterOnce { onChange() }

fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.onChangeLaterOnce(
    onChange: (Argument1, Argument2) -> Unit,
): CSRegistration =
    listOf(first, second).onChangeLaterOnce { onChange(first.value, second.value) }

fun <Argument1, Argument2, Argument3, Return>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (Argument1, Argument2, Argument3) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> =
    object : CSHasChangeValueBase<Return>(parent, onChange) {
        override var value: Return = from(first.value, second.value, third.value)

        init {
            this + (first to second to third).onChange { item1, item2, item3 ->
                value(from(item1, item2, item3))
            }
        }
    }

fun <Argument1, Argument2, Return>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChange<*>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (Argument1, Argument2) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> =
    object : CSHasChangeValueBase<Return>(parent, onChange) {
        override var value: Return = from(first.value, second.value)

        init {
            this + (first to second to third).onChange { item1, item2 ->
                value(from(item1, item2))
            }
        }
    }

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

fun <Argument1, Argument2, Argument3> onChange(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSHasChangeValue<Argument3>,
    onChange: (Argument1, Argument2, Argument3) -> Unit,
): CSRegistration = listOf(item1, item2, item3).onChange {
    onChange(item1.value, item2.value, item3.value)
}

fun <Argument1, Argument2, Argument3>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>>.onChange(
    onChange: (Argument1, Argument2, Argument3) -> Unit,
): CSRegistration = onChange(first, second, third, onChange)

fun <Argument1, Argument2, Argument3>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>>.onChange(
    onChange: () -> Unit,
): CSRegistration = onChange(first, second, third) { _, _, _ -> onChange() }

fun <Argument1, Argument2, Argument3>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>>.onChangeLaterOnce(
    onChange: (Argument1, Argument2, Argument3) -> Unit,
): CSRegistration = listOf(first, second, third).onChangeLaterOnce {
    onChange(first.value, second.value, third.value)
}

fun <Argument1, Argument2, Argument3>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>>.onChangeLaterOnce(
    dispatcher: CoroutineContext = Main, onChange: SusFun,
): CSRegistration = listOf(first, second, third)
    .onChangeLaterOnce(dispatcher) { onChange() }

fun <Argument1, Argument2, Argument3> action(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSHasChangeValue<Argument3>,
    onAction: (Argument1, Argument2, Argument3) -> Unit,
): CSRegistration = listOf(item1, item2, item3).action {
    onAction(item1.value, item2.value, item3.value)
}

fun <Argument1, Argument2, Argument3> Triple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>>.action(
    onAction: (Argument1, Argument2, Argument3) -> Unit,
): CSRegistration = action(first, second, third, onAction)

fun <Argument1, Argument2, Argument3> Triple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>>.actionLaterOnce(
    onAction: (Argument1, Argument2, Argument3) -> Unit,
): CSRegistration = listOf(first, second, third).actionLaterOnce {
    onAction(first.value,
        second.value,
        third.value)
}

fun <Argument1, Argument2, Argument3> Triple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>>.actionLaterOnce(
    onAction: () -> Unit,
): CSRegistration = actionLaterOnce { _, _, _ -> onAction() }

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
