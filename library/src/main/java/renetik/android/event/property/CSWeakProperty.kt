package renetik.android.event.property

import renetik.android.event.change.*
import renetik.android.event.dispatch.*
import renetik.android.event.lifecycle.*
import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

import java.lang.ref.WeakReference

class CSWeakProperty<T>(
    value: T? = null, onChange: ((value: T?) -> Unit)? = null
) : CSPropertyBase<T?>(onChange) {

    private var _value: WeakReference<T>? = null

    override fun value(newValue: T?, fire: Boolean) {
        if (_value?.get() == newValue) return
        _value = WeakReference(newValue)
        onValueChanged(newValue, fire)
    }

    override var value: T?
        get() = _value?.get()
        set(value) = value(value)

    init {
        value?.let { this.value = it }
    }

    companion object {
        fun <T> weakProperty(
            value: T? = null, onChange: ((value: T?) -> Unit)? = null
        ): CSProperty<T?> = CSWeakProperty(value, onChange)
    }
}