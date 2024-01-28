package renetik.android.event.property

import renetik.android.core.kotlin.notImplemented
import renetik.android.core.lang.variable.CSVariable
import renetik.android.event.registration.CSHasChange
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.CSHasChangeValue.Companion.action
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.plus

interface CSProperty<T> : CSVariable<T>, CSHasChange<T>, CSHasChangeValue<T> {
    fun value(newValue: T, fire: Boolean = true)
    fun fireChange()
    fun paused(function: () -> Unit): Unit = notImplemented()

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
        ): CSProperty<T?> = CSPropertyImpl(null, onChange)

        inline fun <Argument1, Argument2> property(
            parent: CSHasRegistrations,
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            crossinline onChange: (Argument1, Argument2) -> Boolean
        ): CSProperty<Boolean> {
            val property = property(false)
            parent + action(item1, item2) { value1, value2 ->
                property.value = onChange(value1, value2)
            }
            return property
        }

        inline fun <Argument1, Argument2, Argument3> property(
            parent: CSHasRegistrations,
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            crossinline onChange: (Argument1, Argument2, Argument3) -> Boolean
        ): CSProperty<Boolean> {
            val property = property(false)
            parent + action(item1, item2, item3) { value1, value2, value3 ->
                property.value = onChange(value1, value2, value3)
            }
            return property
        }
    }
}