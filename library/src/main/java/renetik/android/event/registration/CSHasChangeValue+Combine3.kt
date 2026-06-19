package renetik.android.event.registration

import kotlinx.coroutines.Dispatchers.Main
import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.SusFun
import renetik.android.core.lang.tuples.to
import kotlin.coroutines.CoroutineContext

fun <Argument1, Argument2>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChange<*>>.onChange(
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

fun <Argument1, Argument2, Argument3>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>>.onChange(
    onChange: (Argument1, Argument2, Argument3) -> Unit,
): CSRegistration = listOf(first, second, third).onChange {
    onChange(first.value, second.value, third.value)
}

fun <Argument1, Argument2, Argument3>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>>.onChange(
    onChange: () -> Unit,
): CSRegistration = listOf(first, second, third).onChange {
    onChange()
}

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

fun <Argument1, Argument2, Argument3>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>>.action(
    onAction: (Argument1, Argument2, Argument3) -> Unit,
): CSRegistration = listOf(first, second, third).action {
    onAction(first.value, second.value, third.value)
}

fun <Argument1, Argument2, Argument3>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>>.actionLaterOnce(
    onAction: (Argument1, Argument2, Argument3) -> Unit,
): CSRegistration = listOf(first, second, third).actionLaterOnce {
    onAction(first.value,
        second.value,
        third.value)
}

fun <Argument1, Argument2, Argument3>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>>.actionLaterOnce(
    onAction: () -> Unit,
): CSRegistration = actionLaterOnce { _, _, _ -> onAction() }