package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.tuples.CSQuadruple
import renetik.android.core.lang.tuples.to

fun <Argument1, Argument2, Argument3, Argument4>
        CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.onChange(
    onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = listOf(first, second, third, fourth).onChange {
    onChange(first.value, second.value, third.value, fourth.value)
}

fun <Argument1, Argument2, Argument3, Argument4>
        CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.onChangeLaterOnce(
    onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = listOf(first, second, third, fourth).onChangeLaterOnce {
    onChange(first.value, second.value, third.value, fourth.value)
}

fun <Argument1, Argument2, Argument3, Argument4>
        CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.action(
    onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = listOf(first, second, third, fourth).action {
    onChange(first.value, second.value, third.value, fourth.value)
}

fun <Argument1, Argument2, Argument3, Argument4>
        CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.actionLaterOnce(
    isActionNow: Boolean = false,
    onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
): CSRegistration = listOf(first, second, third, fourth).actionLaterOnce(isActionNow) {
    onChange(first.value, second.value, third.value, fourth.value)
}

fun <Argument1, Argument2, Argument3, Argument4, Return>
        CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
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
                CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
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
                CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    onChange: ArgFun<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>? = null
): CSHasChangeValue<CSQuadruple<Argument1, Argument2, Argument3, Argument4>> =
    hasChangeValue(parent, from = { item1, item2, item3, item4 ->
        CSQuadruple(item1, item2, item3, item4)
    }, onChange)

fun <T, V, K, L, Return> CSQuadruple<CSHasChangeValue<T>,
        CSHasChangeValue<V>, CSHasChangeValue<K>, CSHasChangeValue<L>>.delegate(
    parent: CSHasRegistrations? = null,
    from: (T, V, K, L) -> Return,
): CSHasChangeValue<Return> = object : CSHasChangeValue<Return> {
    override val value: Return
        get() = from(first.value, second.value, third.value, fourth.value)

    override fun onChange(function: (Return) -> Unit): CSRegistration {
        val value = CSValueFunction(this, value, function)
        return CSRegistration(first.onChange {
            if (parent?.registrations.isActive)
                value(from(it, second.value, third.value, fourth.value))
        }, second.onChange {
            if (parent?.registrations.isActive)
                value(from(first.value, it, third.value, fourth.value))
        }, third.onChange {
            if (parent?.registrations.isActive)
                value(from(first.value, second.value, it, fourth.value))
        }, fourth.onChange {
            if (parent?.registrations.isActive)
                value(from(first.value, second.value, third.value, it))
        }).registerTo(parent)
    }
}
