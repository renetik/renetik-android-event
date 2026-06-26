package renetik.android.event.property

import renetik.android.event.change.*
import renetik.android.event.dispatch.*
import renetik.android.event.lifecycle.*
import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

import renetik.android.core.lang.variable.CSListValuesVariable

interface CSListValuesProperty<T> : CSListValuesVariable<T>, CSProperty<T>