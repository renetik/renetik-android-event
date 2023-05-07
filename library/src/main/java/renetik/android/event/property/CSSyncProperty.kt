package renetik.android.event.property

import renetik.android.core.lang.variable.CSSynchronizedVariable
import renetik.android.event.common.CSHasDestruct

interface CSSyncProperty<T> : CSSynchronizedVariable<T>, CSProperty<T>

fun <T> CSHasDestruct.syncProperty(
    value: T, onChange: ((value: T) -> Unit)? = null): CSSyncProperty<T> =
    CSSyncPropertyImpl(this, value, onChange)

fun <T> CSHasDestruct.syncProperty(
    property: CSProperty<T>,
    onChange: ((value: T) -> Unit)? = null): CSSyncProperty<T> =
    CSSyncPropertyImpl(this, property.value, onChange).apply { connect(property) }