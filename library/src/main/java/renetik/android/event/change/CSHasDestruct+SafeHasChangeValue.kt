package renetik.android.event.change

import renetik.android.event.dispatch.*
import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

import renetik.android.event.lifecycle.CSHasDestruct
import renetik.android.event.property.CSSafeHasChangeValue

fun <T> CSHasDestruct.safe(property: CSHasChangeValue<T>)
        : CSSafeHasChangeValue<T> = property.safeStateDelegate(this)