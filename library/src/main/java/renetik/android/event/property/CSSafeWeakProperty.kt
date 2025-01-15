package renetik.android.event.property

import renetik.android.event.common.CSHasDestruct
import renetik.android.event.util.CSLater.onMain
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

class CSSafeWeakProperty<T>(
    parent: CSHasDestruct,
    value: T? = null,
    onChangeUnsafe: ((value: T?) -> Unit)? = null
) : CSPropertyBase<T?>(parent, onChangeUnsafe), CSSafeProperty<T?> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.value = value
    }

    private val _value = AtomicReference<WeakReference<T?>?>(null)

    override fun value(newValue: T?, fire: Boolean) {
        //This was proposed by Gemini and ChaGpt after long conversation..
        while (true) {
            val current = _value.get()
            if (current?.get() == newValue) return
            val weakReference = WeakReference(newValue)
            if (_value.compareAndSet(current, weakReference)) {
                onValueChanged(newValue)
                return
            }
        }
    }

    init {
        value?.let { this.value = it }
    }

    @Volatile
    override var isChanged = false

    override fun fireChange() = value.let {
        onChange?.invoke(it)
        onMain { eventChange.fire(it) }
    }

    override var value: T?
        get() = _value.get()?.get()
        set(value) = this.value(value)

    companion object {
        fun <T> CSHasDestruct.safeWeakProperty(
            value: T? = null,
            onChangeUnsafe: ((value: T?) -> Unit)? = null
        ): CSProperty<T?> = CSSafeWeakProperty(this, value, onChangeUnsafe)
    }
}