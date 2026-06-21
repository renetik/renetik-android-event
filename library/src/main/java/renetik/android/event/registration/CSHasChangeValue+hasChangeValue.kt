@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.tuples.CSSixtuple
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.destruct

fun <Argument, Return : CSHasDestruct>
        CSHasChangeValue<Argument>.hasChangeValueDestruct(
    parent: CSHasRegistrations? = null,
    from: (Argument) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> = hasChangeValue(
    parent, fromWithPrevious = { type, previous ->
        previous?.destruct(); from(type)
    }, onChange)

fun <Argument> CSHasChangeValue<Argument>.hasChangeValue(
    parent: CSHasRegistrations? = null, onChange: ArgFun<Argument>? = null,
): CSHasChangeValue<Argument> = hasChangeValue(parent, from = { it }, onChange)

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