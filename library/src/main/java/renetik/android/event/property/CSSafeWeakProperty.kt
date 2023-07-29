package renetik.android.event.property

import renetik.android.event.common.CSHasDestruct
import renetik.android.event.util.CSLater.onMain
import java.lang.ref.WeakReference

class CSSafeWeakProperty<T>(
    parent: CSHasDestruct,
    value: T? = null, onChange: ((value: T?) -> Unit)? = null
) : CSPropertyBase<T?>(parent, onChange), CSSafeProperty<T?> {

    private var _value: WeakReference<T>? = null

    override fun value(newValue: T?, fire: Boolean): Unit = synchronized(this) {
        if (_value?.get() == newValue) return
        _value = WeakReference(newValue)
        onMain { onValueChanged(newValue) }
    }

    @get:Synchronized
    override var value: T?
        get() = _value?.get()
        set(value) = value(value)

    init {
        value?.let { this.value = it }
    }

    companion object {
        fun <T> CSHasDestruct.safeWeakProperty(
            value: T? = null, onChange: ((value: T?) -> Unit)? = null
        ): CSProperty<T?> = CSSafeWeakProperty(this, value, onChange)
    }
}