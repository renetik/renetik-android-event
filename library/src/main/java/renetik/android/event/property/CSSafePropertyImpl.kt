package renetik.android.event.property

import renetik.android.event.common.CSHasDestruct
import renetik.android.event.util.CSLater.onMain
import java.util.concurrent.atomic.AtomicReference

class CSSafePropertyImpl<T>(
    parent: CSHasDestruct,
    value: T, onChange: ((value: T) -> Unit)? = null
) : CSPropertyBase<T>(parent, onChange), CSSafeProperty<T> {

    private val field = AtomicReference(value)

    override fun value(newValue: T, fire: Boolean) {
        if (field.getAndSet(newValue) != newValue)
            onMain { onValueChanged(newValue, fire) }
    }

    override var value: T
        get() = this.field.get()
        set(value) = value(value)

    companion object {
        fun <T> CSHasDestruct.safeProperty(
            value: T, onChange: ((value: T) -> Unit)? = null
        ) = CSSafePropertyImpl(this, value, onChange)
    }
}