package renetik.android.event.property

import renetik.android.core.lang.value.CSSafeValue
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.CSRegistration

interface CSSafeHasChangeValue<T> : CSSafeValue<T>, CSHasChangeValue<T> {
    fun onUnsafeChange(function: (T) -> Unit): CSRegistration
}
