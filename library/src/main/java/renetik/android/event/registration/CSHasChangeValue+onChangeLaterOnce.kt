package renetik.android.event.registration

import kotlinx.coroutines.Dispatchers.Main
import renetik.android.core.lang.SusFun
import renetik.android.core.lang.tuples.CSQuadruple
import kotlin.coroutines.CoroutineContext

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

fun <Argument1, Argument2, Argument3, Argument4>
        CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.onChangeLaterOnce(
    onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = listOf(first, second, third, fourth).onChangeLaterOnce {
    onChange(first.value, second.value, third.value, fourth.value)
}