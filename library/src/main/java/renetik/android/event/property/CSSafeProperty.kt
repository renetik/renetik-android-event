package renetik.android.event.property

import androidx.annotation.AnyThread
import renetik.android.core.lang.variable.CSSafeVariable
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.registration.plus

interface CSSafeProperty<T> : CSSafeHasChangeValue<T>, CSSafeVariable<T>, CSProperty<T>

//TODO?: Maybe move to companion same as in CSSafeHasChangeValue ?
fun <T> CSHasDestruct.safe(
    property: CSProperty<T>, @AnyThread onChangeUnsafe: ((value: T) -> Unit)? = null
): CSSafeProperty<T> = CSSafePropertyImpl(
    this, property.value, onChangeUnsafe
).apply { this + connect(property) }