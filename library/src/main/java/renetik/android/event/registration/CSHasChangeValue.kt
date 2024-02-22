package renetik.android.event.registration

import renetik.android.core.kotlin.collections.list
import renetik.android.core.lang.value.CSValue
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.property.CSProperty

interface CSHasChangeValue<T> : CSValue<T>, CSHasChange<T> {
    companion object {
        fun <T> CSHasChangeValue<T>.action(function: (T) -> Unit): CSRegistration {
            function(value)
            return onChange(function)
        }

//        inline fun <Argument1, Argument2, T : Any> hasChangeValue(
//            parent: CSHasRegistrations? = null,
//            item1: CSHasChangeValue<Argument1>,
//            item2: CSHasChangeValue<Argument2>,
//            crossinline from: (Argument1, Argument2) -> T,
//        ): CSHasChangeValue<T> {
//            val property = CSProperty.property<T>()
//            val sss = object : CSHasChangeValue<T> {
//                override var value: T by notNull()
//                override fun onChange(function: (T) -> void): CSRegistration {
//                    TODO("Not yet implemented")
//                }
//
//            }
//            action(item1, item2) { arg1, arg2 ->
//                property.value = from(arg1, arg2)
//            }.also { parent?.register(it) }
//            return property
//        }

        inline fun <Argument1, Argument2> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            crossinline onChange: (Argument1, Argument2) -> Unit,
        ): CSRegistration = list(item1, item2).onChange {
            onChange(item1.value, item2.value)
        }

        inline fun <Argument1, Argument2> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            crossinline onAction: (Argument1, Argument2) -> Unit,
        ): CSRegistration = list(item1, item2).action {
            onAction(item1.value, item2.value)
        }

        inline fun <Argument1, Argument2, Argument3> hasChangeValue(
            parent: CSHasRegistrations? = null,
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            crossinline from: (Argument1, Argument2) -> Argument3,
        ): CSHasChangeValue<Argument3> = object : CSProperty<Argument3> {
            val event = event<Argument3>()
            override var value: Argument3 = from(item1.value, item2.value)
            override fun onChange(function: (Argument3) -> Unit) = event.listen { function(value) }

            init {
                onChange(item1, item2) { item1, item2 ->
                    value = from(item1, item2)
                    event.fire(value)
                }.also { parent?.register(it) }
            }

            override fun value(newValue: Argument3, fire: Boolean) {
                if (value == newValue) return
                value = newValue
                if (fire) fireChange()
            }

            override fun fireChange() = event.fire(value)
        }

        inline fun <Argument1, Argument2, Argument3> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            crossinline onChange: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = list(item1, item2, item3).onChange {
            onChange(item1.value, item2.value, item3.value)
        }

        inline fun <Argument1, Argument2, Argument3> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            crossinline onAction: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = list(item1, item2, item3).action {
            onAction(item1.value, item2.value, item3.value)
        }

        inline fun <Argument1, Argument2, Argument3, Argument4> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            crossinline onAction: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = list(item1, item2, item3, item4).action {
            onAction(item1.value, item2.value, item3.value, item4.value)
        }
    }
}