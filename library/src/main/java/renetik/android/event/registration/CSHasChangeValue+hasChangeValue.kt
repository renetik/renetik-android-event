@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.tuples.CSQuadruple
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

fun <T> CSHasChangeValue<T>.hasChangeValue(
    parent: CSHasRegistrations? = null, onChange: ArgFun<T>? = null,
): CSHasChangeValue<T> = hasChangeValue(parent, from = { it }, onChange)