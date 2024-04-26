package renetik.android.event.property

import renetik.android.core.kotlin.notImplemented
import renetik.android.core.lang.variable.CSVariable
import renetik.android.event.registration.CSHasChange
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.CSHasChangeValue.Companion.onChange
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.plus
import renetik.android.event.registration.register

interface CSProperty<T> : CSVariable<T>, CSHasChange<T>, CSHasChangeValue<T> {
    fun value(newValue: T, fire: Boolean = true)
    fun fireChange()
    fun pause(): Unit = notImplemented()
    fun resume(): Unit = notImplemented()

    companion object {
        fun <T> property(
            value: T, onChange: ((value: T) -> Unit)? = null,
        ): CSProperty<T> =
            CSPropertyImpl(value, onChange)

        fun <T> property(
            onChange: ((value: T?) -> Unit)? = null,
        ): CSProperty<T?> =
            CSPropertyImpl(null, onChange)

        fun <T> lateProperty(
            onChange: ((value: T) -> Unit)? = null,
        ): CSLateProperty<T> =
            CSLateProperty(onChange)

        fun <T> propertyDelegate(
            parent: CSHasRegistrations,
            property: CSProperty<T>,
            onChange: ((value: T) -> Unit)? = null,
        ): CSProperty<T> = lateProperty(onChange)
            .apply { parent + connect(property) }

        fun <T> propertyDelegate(
            property: CSProperty<T>,
            onChange: ((value: T) -> Unit)? = null,
        ): CSProperty<T> = lateProperty(onChange)
            .apply { connect(property) }

        fun <T> nullableProperty(
            onChange: ((value: T?) -> Unit)? = null,
        ): CSProperty<T?> = CSPropertyImpl(null, onChange)

        inline fun <Argument1, Argument2, Argument3> property(
            parent: CSHasRegistrations? = null,
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            crossinline from: (Argument1, Argument2) -> Argument3,
        ): CSProperty<Argument3> {
            val property = property(from(item1.value, item2.value))
            onChange(item1, item2) { value1, value2 ->
                property.value = from(value1, value2)
            }.also { parent?.register(it) }
            return property
        }

        inline fun <Argument1, Argument2, Argument3, Argument4> property(
            parent: CSHasRegistrations? = null,
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            crossinline from: (Argument1, Argument2, Argument3) -> Argument4,
        ): CSProperty<Argument4> {
            val property = property(from(item1.value, item2.value, item3.value))
            onChange(item1, item2, item3) { value1, value2, value3 ->
                property.value = from(value1, value2, value3)
            }.also { parent?.register(it) }
            return property
        }
    }
}