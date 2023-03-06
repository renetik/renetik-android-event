package renetik.android.event.registration

import renetik.android.core.lang.value.CSValue
import renetik.android.event.property.action

interface CSHasChangeValue<T> : CSValue<T>, CSHasChange<T> {
    companion object {
        fun <T, V> onChange(
            parent: CSHasChangeValue<T>,
            child: (T) -> CSHasChangeValue<V>,
            onChange: (V) -> Any
        ): CSRegistration {
            var childRegistration: CSRegistration? = null
            val parentRegistration = parent.onChange {
                childRegistration?.cancel()
                childRegistration = child(parent.value).action { onChange(it) }
            }
            return CSRegistration.CSRegistration(isActive = true, onCancel = {
                parentRegistration.cancel()
                childRegistration?.cancel()
            })
        }
    }
}