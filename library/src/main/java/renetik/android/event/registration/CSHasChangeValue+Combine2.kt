package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.tuples.to

fun <Argument1, Argument2, Return>
        Pair<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (Argument1, Argument2) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> =
    object : CSHasChangeValueBase<Return>(parent, onChange) {
        override var value: Return = from(first.value, second.value)

        init {
            this + (first to second).onChange { item1, item2 ->
                value(from(item1, item2))
            }
        }
    }

fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>,
        CSHasChangeValue<Argument2>>.onChange(
    onChange: (Argument1, Argument2) -> Unit,
): CSRegistration = listOf(first, second).onChange {
    onChange(first.value, second.value)
}

fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>,
        CSHasChangeValue<Argument2>>.onChange(
    onChange: () -> Unit,
): CSRegistration = listOf(first, second).onChange {
    onChange()
}

fun <Argument1, Argument2> Pair<CSHasChangeValue<out Argument1>,
        CSHasChangeValue<out Argument2>>.action(
    onAction: (Argument1, Argument2) -> Unit,
): CSRegistration = listOf(first, second).action {
    onAction(first.value, second.value)
}

fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>,
        CSHasChangeValue<Argument2>>.action(
    onAction: () -> Unit,
): CSRegistration = listOf(first, second).action {
    onAction()
}


fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>,
        CSHasChangeValue<Argument2>>.onChangeLaterOnce(
    onChange: () -> Unit,
): CSRegistration = listOf(first, second)
    .onChangeLaterOnce { onChange() }

fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>,
        CSHasChangeValue<Argument2>>.onChangeLaterOnce(
    onChange: (Argument1, Argument2) -> Unit,
): CSRegistration = listOf(first, second)
    .onChangeLaterOnce { onChange(first.value, second.value) }

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