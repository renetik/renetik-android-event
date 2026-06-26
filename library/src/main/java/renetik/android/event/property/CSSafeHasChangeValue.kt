package renetik.android.event.property

import renetik.android.event.change.*
import renetik.android.event.dispatch.*
import renetik.android.event.lifecycle.*
import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

import renetik.android.core.lang.value.CSSafeValue
import renetik.android.event.change.CSHasChangeValue
import renetik.android.event.registration.CSRegistration

interface CSSafeHasChangeValue<T> : CSSafeValue<T>, CSHasChangeValue<T> {
    fun onUnsafeChange(function: (T) -> Unit): CSRegistration
}
