package renetik.android.event.registration

import renetik.android.core.lang.tuples.CSQuadruple

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

fun <Argument1, Argument2, Argument3>
        Triple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>>.action(
    onAction: (Argument1, Argument2, Argument3) -> Unit,
): CSRegistration = listOf(first, second, third).action {
    onAction(first.value, second.value, third.value)
}

fun <Argument1, Argument2, Argument3, Argument4>
        CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.action(
    onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = listOf(first, second, third, fourth).action {
    onChange(first.value, second.value, third.value, fourth.value)
}