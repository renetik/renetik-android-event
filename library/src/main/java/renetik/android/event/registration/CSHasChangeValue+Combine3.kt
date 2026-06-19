package renetik.android.event.registration

import kotlinx.coroutines.Dispatchers.Main
import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.SusFun
import renetik.android.core.lang.tuples.to
import kotlin.coroutines.CoroutineContext

fun <Argument1, Argument2> Triple<CSHasChangeValue<Argument1>,
        CSHasChangeValue<Argument2>, CSHasChange<*>>.onChange(
    onChange: (Argument1, Argument2) -> Unit,
): CSRegistration = listOf(first, second, third).onChange {
    onChange(first.value, second.value)
}

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