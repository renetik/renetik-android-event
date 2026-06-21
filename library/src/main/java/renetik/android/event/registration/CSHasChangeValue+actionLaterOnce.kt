package renetik.android.event.registration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import renetik.android.core.kotlin.className
import renetik.android.core.lang.tuples.CSQuadruple
import renetik.android.core.lang.tuples.CSQuintuple
import renetik.android.core.lang.tuples.CSSixtuple
import renetik.android.event.common.CSDebouncer.Companion.debouncer

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

fun <Argument1, Argument2, Argument3, Argument4>
        CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.actionLaterOnce(
    isActionNow: Boolean = false,
    onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = listOf(first, second, third, fourth).actionLaterOnce(isActionNow) {
    onChange(first.value, second.value, third.value, fourth.value)
}

fun <Argument1, Argument2, Argument3, Argument4, Argument5>
        CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                CSHasChangeValue<Argument5>>.actionLaterOnce(
    onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
): CSRegistration = listOf(first, second, third, fourth, fifth).actionLaterOnce {
    onChange(first.value, second.value, third.value, fourth.value, fifth.value)
}

fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
        CSSixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>
                >.actionLaterOnce(
    dispatcher: CoroutineDispatcher = Main,
    onChange: suspend (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Unit
): CSRegistration {
    val registrations = CSRegistrationsMap(className)
    var value1: Argument1? = null
    var value2: Argument2? = null
    var value3: Argument3? = null
    var value4: Argument4? = null
    var value5: Argument5? = null
    var value6: Argument6? = null
    fun clearValues() {
        value1 = null; value2 = null; value3 = null
        value4 = null; value5 = null; value6 = null
    }

    val laterOnceFunction = registrations.debouncer(dispatcher) {
        if (registrations.isActive) {
            onChange((value1 ?: first.value),
                (value2 ?: second.value),
                (value3 ?: third.value),
                (value4 ?: fourth.value),
                (value5 ?: fifth.value),
                (value6 ?: sixth.value))
            clearValues()
        }
    }
    registrations + first.onChange { value1 = it; laterOnceFunction() }
    registrations + second.onChange { value2 = it; laterOnceFunction.invoke() }
    registrations + third.onChange { value3 = it; laterOnceFunction.invoke() }
    registrations + fourth.onChange { value4 = it; laterOnceFunction.invoke() }
    registrations + fifth.onChange { value5 = it; laterOnceFunction.invoke() }
    registrations + sixth.onChange { value6 = it; laterOnceFunction.invoke() }
    laterOnceFunction()
    registrations.onCancel(::clearValues)
    return registrations
}