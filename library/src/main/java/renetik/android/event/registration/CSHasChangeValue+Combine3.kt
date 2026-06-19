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

fun <T, V, K, Return>
        Triple<CSHasChangeValue<T>, CSHasChangeValue<V>,
                CSHasChangeValue<K>>.delegate(
    parent: CSHasRegistrations? = null,
    from: (T, V, K) -> Return,
): CSHasChangeValue<Return> = object : CSHasChangeValue<Return> {
    override val value: Return
        get() = from(first.value, second.value, third.value)

    override fun onChange(function: (Return) -> Unit): CSRegistration {
        val value = CSValueFunction(this, value, function)
        return CSRegistration(first.onChange {
            if (parent?.registrations.isActive) value(from(it, second.value, third.value))
        }, second.onChange {
            if (parent?.registrations.isActive) value(from(first.value, it, third.value))
        }, third.onChange {
            if (parent?.registrations.isActive) value(from(first.value, second.value, it))
        }).registerTo(parent)
    }
}