package renetik.android.event.property

import androidx.annotation.AnyThread
import renetik.android.core.lang.variable.CSSafeVariable
import renetik.android.event.common.CSHasDestruct

interface CSSafeProperty<T> : CSSafeHasChangeValue<T>, CSSafeVariable<T>, CSProperty<T>

fun <T> CSHasDestruct.safe(
    property: CSProperty<T>, @AnyThread onChangeUnsafe: ((value: T) -> Unit)? = null
): CSSafeProperty<T> = CSSafePropertyImpl(
    this, property.value, onChangeUnsafe
).apply { connect(property) }