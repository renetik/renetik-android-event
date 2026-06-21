@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.tuples.CSQuadruple
import renetik.android.core.lang.tuples.to
import kotlin.to

fun <Argument, Return> CSHasChangeValue<Argument>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (Argument) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> = let { source ->
    object : CSHasChangeValueBase<Return>(parent, onChange) {
        override var value: Return = from(source.value)

        init {
            this + source.onChange { value(from(it)) }
        }
    }
}

fun <Argument, Return> CSHasChangeValue<Argument>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    fromWithPrevious: (Argument, Return?) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> =
    let { property ->
        object : CSHasChangeValueBase<Return>(parent, onChange) {
            var previous: Return? = null

            override var value: Return = fromWithPrevious(property.value, previous)

            init {
                this + property.onChange {
                    value(fromWithPrevious(it, previous))
                    previous = value
                }
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
    object : CSHasChangeValueBase<Return>(parent, onChange) {
        override var value: Return = from(first.value, second.value)

        init {
            this + (first to second).onChange { item1, item2 ->
                value(from(item1, item2))
            }
        }
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