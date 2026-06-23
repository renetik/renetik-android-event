@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.event.common.CSHasRegistrationsHasDestruct
import renetik.android.event.property.CSSafeHasChangeValue
import renetik.android.event.property.CSSafeHasChangeValueBase

@JvmName("safeStateDelegate")
fun <Argument, Return> CSSafeHasChangeValue<Argument>.stateDelegate(
    parent: CSHasRegistrations? = null,
    unsafeFromValue: (Argument) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return> = let { source ->
    object : CSSafeHasChangeValueBase<Return>(parent, unsafeFromValue(source.value), onChange) {
        init {
            this + source.onUnsafeChange { value(unsafeFromValue(it)) }
        }
    }
}

@JvmName("safeHasChangeValueIdentity")
fun <T> CSSafeHasChangeValue<T>.safeStateDelegate(
    parent: CSHasRegistrations? = null, onChange: ArgFun<T>? = null,
): CSSafeHasChangeValue<T> = stateDelegate(parent, unsafeFromValue = { it }, onChange)

fun <Argument, Return> CSSafeHasChangeValue<Argument>.safeStateDelegate(
    parent: CSHasRegistrationsHasDestruct,
    unsafeFromValue: (Argument) -> Return
): CSSafeHasChangeValue<Return> = let { source ->
    object : CSSafeHasChangeValueBase<Return>(parent, unsafeFromValue(source.value)) {
        init {
            parent + source.onUnsafeChange { value(unsafeFromValue(it)) }
        }
    }
}