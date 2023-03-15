package renetik.android.event.registration

import renetik.android.core.lang.value.CSValue
import renetik.android.event.property.action

interface CSHasChangeValue<T> : CSValue<T>, CSHasChange<T> {
    companion object {
        inline fun <ParentValue, ChildValue> onChange(
            parent: CSHasChangeValue<ParentValue>,
            crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>,
            crossinline onChange: (ChildValue) -> Unit
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

        inline fun <ParentValue, ChildValue> onChangeNullableChild(
            parent: CSHasChangeValue<ParentValue>,
            crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>?,
            crossinline onChange: (ChildValue) -> Unit
        ): CSRegistration {
            var childRegistration: CSRegistration? = null
            val parentRegistration = parent.onChange { parentValue ->
                childRegistration?.cancel()
                childRegistration = child(parentValue)?.action { childValue ->
                    onChange(childValue)
                }
            }
            return CSRegistration.CSRegistration(isActive = true, onCancel = {
                parentRegistration.cancel()
                childRegistration?.cancel()
            })
        }
    }
}