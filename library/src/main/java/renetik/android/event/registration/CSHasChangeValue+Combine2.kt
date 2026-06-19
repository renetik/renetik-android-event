package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.tuples.to

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