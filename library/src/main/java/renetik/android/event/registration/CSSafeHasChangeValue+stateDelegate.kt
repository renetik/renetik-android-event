@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.event.common.CSHasRegistrationsHasDestruct
import renetik.android.event.property.CSSafeHasChangeValue
import renetik.android.event.property.CSSafeHasChangeValueBase

@JvmName("safeStateDelegate")
fun <Argument, Return> CSSafeHasChangeValue<Argument>.stateDelegate(
    parent: CSHasRegistrations? = null,
    unsafeFrom: (Argument) -> Return,
    onChange: ArgFun<Return>? = null
): CSSafeHasChangeValue<Return> = let { source ->
    object : CSSafeHasChangeValueBase<Return>(parent, unsafeFrom(source.value), onChange) {
        init {
            this + source.onUnsafeChange { value(unsafeFrom(it)) }
        }
    }
}

@JvmName("safeHasChangeValueIdentity")
fun <T> CSSafeHasChangeValue<T>.safeStateDelegate(
    parent: CSHasRegistrations? = null, onChange: ArgFun<T>? = null,
): CSSafeHasChangeValue<T> = stateDelegate(parent, unsafeFrom = { it }, onChange)

fun <Argument, Return> CSSafeHasChangeValue<Argument>.safeStateDelegate(
    parent: CSHasRegistrationsHasDestruct,
    from: (Argument) -> Return
): CSSafeHasChangeValue<Return> = let { source ->
    object : CSSafeHasChangeValueBase<Return>(parent, from(source.value)) {
        init {
            parent + source.onUnsafeChange {
                value(from(it))
            }
        }
    }
}