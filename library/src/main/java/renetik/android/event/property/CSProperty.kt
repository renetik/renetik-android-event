package renetik.android.event.property

import renetik.android.core.lang.variable.CSVariable
import renetik.android.event.registration.CSHasChange
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.plus

interface CSProperty<T> : CSVariable<T>, CSHasChange<T>, CSHasChangeValue<T> {
    fun value(newValue: T, fire: Boolean = true)
    fun fireChange()

    companion object {
        fun <T> property(
            value: T, onChange: ((value: T) -> Unit)? = null
        ): CSProperty<T> =
            CSPropertyImpl(value, onChange)

        fun <T> property(
            onChange: ((value: T?) -> Unit)? = null
        ): CSProperty<T?> =
            CSPropertyImpl(null, onChange)

        fun <T> lateProperty(
            onChange: ((value: T) -> Unit)? = null
        ): CSLateProperty<T> =
            CSLateProperty(onChange)

        fun <T> propertyDelegate(
            parent: CSHasRegistrations,
            property: CSProperty<T>,
            onChange: ((value: T) -> Unit)? = null
        ): CSProperty<T> = lateProperty(onChange)
            .apply { parent + connect(property) }

        fun <T> propertyDelegate(
            property: CSProperty<T>,
            onChange: ((value: T) -> Unit)? = null
        ): CSProperty<T> = lateProperty(onChange)
            .apply { connect(property) }

        fun <T> nullableProperty(
            onChange: ((value: T?) -> Unit)? = null
        ): CSProperty<T?> =
            CSPropertyImpl(null, onChange)
    }
}