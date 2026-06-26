package renetik.android.event.change

import renetik.android.event.dispatch.*
import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

import renetik.android.core.lang.value.CSSafeValue

fun <T> CSHasRegistrations.safeValue(property: CSHasChangeValue<T>)
        : CSSafeValue<T> = property.safeValue(this)