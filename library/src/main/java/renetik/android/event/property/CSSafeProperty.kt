package renetik.android.event.property

import renetik.android.core.lang.variable.CSSynchronizedVariable
import renetik.android.event.common.CSHasDestruct

interface CSSafeProperty<T> : CSSynchronizedVariable<T>, CSProperty<T>

@Deprecated("use safe(prop..")
fun <T> CSHasDestruct.safeProperty(
    value: T, onChange: ((value: T) -> Unit)? = null
): CSSafeProperty<T> = CSSafePropertyImpl(this, value, onChange)

fun <T> CSHasDestruct.safe(
    property: CSProperty<T>, onChange: ((value: T) -> Unit)? = null
): CSSafeProperty<T> = CSSafePropertyImpl(
    this, property.value, onChange
).apply { connect(property) }