package renetik.android.event.property

import renetik.android.event.common.CSHasDestruct
import renetik.android.event.util.CSLater.onMain
import java.util.concurrent.atomic.AtomicReference

class CSSafePropertyImpl<T>(
    parent: CSHasDestruct,
    value: T, onChangeUnsafe: ((value: T) -> Unit)? = null
) : CSPropertyBase<T>(parent, onChangeUnsafe), CSSafeProperty<T> {

    private val field = AtomicReference(value)

    override fun value(newValue: T, fire: Boolean) {
        if (field.getAndSet(newValue) != newValue)
            onValueChanged(newValue, fire)
    }

    @Volatile
    override var isChanged = false

    override fun fireChange() = value.let {
        onChange?.invoke(it)
        onMain { eventChange.fire(it) }
    }

    override var value: T
        get() = this.field.get()
        set(value) = value(value)

    companion object {
        fun <T> CSHasDestruct.safeProperty(
            value: T, onChangeUnsafe: ((value: T) -> Unit)? = null
        ) = CSSafePropertyImpl(this, value, onChangeUnsafe)
    }
}