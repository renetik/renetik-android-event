package renetik.android.event.property

import renetik.android.core.lang.variable.CSVariable
import renetik.android.event.registration.CSRegistration

interface CSProperty<T> : CSVariable<T> {
    fun value(newValue: T, fire: Boolean = true)
    fun onChange(function: (T) -> Unit): CSRegistration
    fun fireChange()

    companion object {
        fun <T> property(
            value: T, onChange: ((value: T) -> Unit)? = null): CSProperty<T> =
            CSPropertyImpl(value, onChange)

        fun <T> property(
            onChange: ((value: T?) -> Unit)? = null): CSProperty<T?> =
            CSPropertyImpl(null, onChange)

        fun <T> synchronizedProperty(
            value: T, onChange: ((value: T) -> Unit)? = null): CSSynchronizedProperty<T> =
            CSSynchronizedPropertyImpl(value, onChange)

        fun <T> synchronizedProperty(
            property: CSProperty<T>,
            onChange: ((value: T) -> Unit)? = null): CSSynchronizedProperty<T> =
            synchronizedProperty(property.value, onChange).apply { connect(property) }

        fun <T> lateProperty(
            onChange: ((value: T) -> Unit)? = null): CSLateProperty<T> =
            CSLateProperty(onChange)

        fun <T> nullableProperty(
            onChange: ((value: T?) -> Unit)? = null): CSProperty<T?> =
            CSPropertyImpl(null, onChange)
    }
}