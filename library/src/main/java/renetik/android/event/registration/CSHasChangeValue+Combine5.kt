package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.tuples.CSQuintuple
import renetik.android.core.lang.tuples.to

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