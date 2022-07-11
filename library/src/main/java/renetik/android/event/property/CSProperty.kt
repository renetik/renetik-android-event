package renetik.android.event.property

import renetik.android.core.lang.variable.CSVariable
import renetik.android.event.registration.CSRegistration

interface CSProperty<T> : CSVariable<T> {
    fun value(newValue: T, fire: Boolean = true)
    fun onChange(function: (T) -> Unit): CSRegistration
    fun fireChange()

    companion object {
        fun <T> property(
            value: T, onApply: ((value: T) -> Unit)? = null): CSProperty<T> =
            CSPropertyImpl(value, onApply)

        fun <T> property(
            onApply: ((value: T?) -> Unit)? = null): CSProperty<T?> =
            CSPropertyImpl(null, onApply)

        fun <T> synchronizedProperty(
            value: T, onApply: ((value: T) -> Unit)? = null): CSSynchronizedProperty<T> =
            CSSynchronizedPropertyImpl(value, onApply)

        fun <T> synchronizedProperty(
            property: CSProperty<T>, onApply: ((value: T) -> Unit)? = null)
                : CSSynchronizedProperty<T> =
            synchronizedProperty(property.value, onApply).apply { connect(property) }

        fun <T> lateProperty(
            onApply: ((value: T) -> Unit)? = null): CSLateProperty<T> =
            CSLateProperty(onApply)

        fun <T> nullableProperty(
            onApply: ((value: T?) -> Unit)? = null): CSProperty<T?> =
            CSPropertyImpl(null, onApply)
    }
}