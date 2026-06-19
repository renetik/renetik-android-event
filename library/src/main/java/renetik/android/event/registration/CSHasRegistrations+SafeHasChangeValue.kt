package renetik.android.event.registration

import renetik.android.core.lang.value.CSSafeValue

fun <T> CSHasRegistrations.safeValue(property: CSHasChangeValue<T>)
        : CSSafeValue<T> = property.safeValue(this)