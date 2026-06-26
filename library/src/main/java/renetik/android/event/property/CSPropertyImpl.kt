package renetik.android.event.property

import renetik.android.event.change.*
import renetik.android.event.dispatch.*
import renetik.android.event.lifecycle.*
import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

class CSPropertyImpl<T>(
    value: T, onApply: ((value: T) -> Unit)? = null
) : CSPropertyBase<T>(onApply) {

    private var _value: T = value

    override fun value(newValue: T, fire: Boolean) {
        if (_value == newValue) return
        _value = newValue
        onValueChanged(newValue, fire)
    }

    override var value: T
        get() = _value
        set(value) = value(value)
}


