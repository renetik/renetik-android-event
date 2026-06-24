package renetik.android.event.registration

import renetik.android.event.common.CSHasDestruct
import renetik.android.event.property.CSSafeHasChangeValue

fun <T> CSHasDestruct.safe(property: CSHasChangeValue<T>)
        : CSSafeHasChangeValue<T> = property.safeStateDelegate(this)