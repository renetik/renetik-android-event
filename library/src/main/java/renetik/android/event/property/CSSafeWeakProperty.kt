package renetik.android.event.property

import renetik.android.event.common.CSHasDestruct
import renetik.android.event.util.CSLater.onMain
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference

class CSSafeWeakProperty<T>(
    parent: CSHasDestruct,
    value: T? = null,
    onChange: ((value: T?) -> Unit)? = null
) : CSPropertyBase<T?>(parent, onChange), CSSafeProperty<T?> {

    private val _value = AtomicReference<WeakReference<T?>?>(null)


    override fun value(newValue: T?, fire: Boolean) {
        //This was proposed by Gemini and ChaGpt after long conversation..
        while (true) {
            val current = _value.get()
            if (current?.get() == newValue) return
            val weakReference = WeakReference(newValue)
            if (_value.compareAndSet(current, weakReference)) {
                onMain { onValueChanged(newValue) }
                return
            }
        }
    }

    override var value: T?
        get() = _value.get()?.get()
        set(value) = this.value(value)

    init {
        value?.let { this.value = it }
    }

    companion object {
        fun <T> CSHasDestruct.safeWeakProperty(
            value: T? = null,
            onChange: ((value: T?) -> Unit)? = null
        ): CSProperty<T?> = CSSafeWeakProperty(this, value, onChange)
    }
}