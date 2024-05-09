package renetik.android.event.property

import renetik.android.core.lang.value.CSSafeValue
import renetik.android.event.registration.CSHasChangeValue

interface CSSafeHasChangeValue<T> : CSSafeValue<T>, CSHasChangeValue<T>