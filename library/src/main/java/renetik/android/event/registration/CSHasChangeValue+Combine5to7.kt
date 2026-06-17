package renetik.android.event.registration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import renetik.android.core.kotlin.className
import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.tuples.CSQuintuple
import renetik.android.core.lang.tuples.CSSeventuple
import renetik.android.core.lang.tuples.CSSixtuple
import renetik.android.core.lang.tuples.to
import renetik.android.event.common.CSDebouncer.Companion.debouncer

fun <Argument1, Argument2, Argument3, Argument4, Argument5> onChange(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSHasChangeValue<Argument3>,
    item4: CSHasChangeValue<Argument4>,
    item5: CSHasChangeValue<Argument5>,
    onAction: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
): CSRegistration = listOf(item1, item2, item3, item4, item5).onChange {
    onAction(item1.value, item2.value, item3.value, item4.value, item5.value)
}

fun <Argument1, Argument2, Argument3, Argument4, Argument5>
        CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                CSHasChangeValue<Argument5>>.onChange(
    onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
): CSRegistration = onChange(first, second, third, fourth, fifth, onChange)

fun <Argument1, Argument2, Argument3, Argument4, Argument5> action(
    item1: CSHasChangeValue<Argument1>,
    item2: CSHasChangeValue<Argument2>,
    item3: CSHasChangeValue<Argument3>,
    item4: CSHasChangeValue<Argument4>,
    item5: CSHasChangeValue<Argument5>,
    onAction: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
): CSRegistration = listOf(item1, item2, item3, item4, item5).action {
    onAction(item1.value, item2.value, item3.value, item4.value, item5.value)
}

fun <Argument1, Argument2, Argument3, Argument4, Argument5> CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>, CSHasChangeValue<Argument5>>.action(
    onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
): CSRegistration = action(first, second, third, fourth, fifth, onChange)

fun <Argument1, Argument2, Argument3, Argument4, Argument5> CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>, CSHasChangeValue<Argument5>>.onChangeLaterOnce(
    onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
): CSRegistration = listOf(first, second, third, fourth, fifth).onChangeLaterOnce {
    onChange(first.value, second.value, third.value, fourth.value, fifth.value)
}

fun <Argument1, Argument2, Argument3, Argument4, Argument5> CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>, CSHasChangeValue<Argument5>>.actionLaterOnce(
    onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
): CSRegistration = listOf(first, second, third, fourth, fifth).actionLaterOnce {
    onChange(first.value, second.value, third.value, fourth.value, fifth.value)
}


fun <Argument1, Argument2, Argument3, Argument4, Argument5, Return>
        CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                CSHasChangeValue<Argument5>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> =
    object : CSHasChangeValueBase<Return>(parent, onChange) {
        override var value: Return =
            from(first.value, second.value, third.value, fourth.value, fifth.value)

        init {
            this + (first to second to third to fourth to fifth).onChange { item1, item2, item3, item4, item5 ->
                value(from(item1, item2, item3, item4, item5))
            }
        }
    }

fun <Argument1, Argument2, Argument3, Argument4, Argument5>
        CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                CSHasChangeValue<Argument5>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>? = null
): CSHasChangeValue<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>> =
    hasChangeValue(parent, from = { item1, item2, item3, item4, item5 ->
        CSQuintuple(item1, item2, item3, item4, item5)
    }, onChange)


fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
        CSSixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>>.onChange(
    onChange: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Unit,
): CSRegistration = listOf(first, second, third, fourth, fifth, sixth).onChange {
    onChange(first.value, second.value, third.value, fourth.value, fifth.value, sixth.value)
}

fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
        CSSixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>
                >.action(
    onChange: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Unit,
): CSRegistration = listOf(first, second, third, fourth, fifth, sixth).action {
    onChange(first.value, second.value, third.value, fourth.value, fifth.value, sixth.value)
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

fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Return>
        CSSixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> =
    object : CSHasChangeValueBase<Return>(parent, onChange) {
        override var value: Return = from(first.value, second.value,
            third.value, fourth.value, fifth.value, sixth.value)

        init {
            this + (first to second to third to fourth to fifth to sixth)
                .onChange { item1, item2, item3, item4, item5, item6 ->
                    value(from(item1, item2, item3, item4, item5, item6))
                }
        }
    }

fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
        CSSixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>? = null
): CSHasChangeValue<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>> =
    hasChangeValue(parent, from = { item1, item2, item3, item4, item5, item6 ->
        CSSixtuple(item1, item2, item3, item4, item5, item6)
    }, onChange)

fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Argument7>
        CSSeventuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>,
                CSHasChangeValue<Argument7>>.action(
    onChange: (
        Argument1, Argument2, Argument3, Argument4,
        Argument5, Argument6, Argument7
    ) -> Unit,
): CSRegistration = listOf(first, second, third, fourth,
    fifth, sixth, seventh).action {
    onChange(first.value, second.value, third.value,
        fourth.value, fifth.value, sixth.value, seventh.value)
}
