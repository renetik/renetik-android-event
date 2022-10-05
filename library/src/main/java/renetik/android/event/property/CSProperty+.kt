package renetik.android.event.property

import renetik.android.core.kotlin.primitives.isFalse
import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.value.isFalse
import renetik.android.core.lang.value.isTrue
import renetik.android.core.lang.variable.CSVariable
import renetik.android.event.CSEvent
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.common.CSHasDestroy
import renetik.android.event.common.update
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.paused

fun <T : CSProperty<*>> T.apply() = apply { fireChange() }

fun <T> CSProperty<T>.onChange(function: (CSRegistration, T) -> Unit): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange {
        function(registration, it)
    }
    return registration
}

fun <T> CSProperty<T>.onChange(function: () -> Unit): CSRegistration =
    onChange { function() }

fun <T> CSProperty<T>.connect(property: CSProperty<T>): CSRegistration {
    value = property.value
    lateinit var propertyOnChange: CSRegistration
    val thisOnChange: CSRegistration = onChange { value ->
        propertyOnChange.paused { property.value = value }
    }
    propertyOnChange = property.onChange { value ->
        thisOnChange.paused { this.value = value }
    }
    return CSRegistration(thisOnChange, propertyOnChange)
}

fun <T> CSProperty<T?>.clear() = value(null)
val <T> CSProperty<T?>.isEmpty get() = value == null
val <T> CSProperty<T?>.isSet get() = !isEmpty

fun <T> CSProperty<T>.action(function: (T) -> Unit): CSRegistration {
    function(value)
    return onChange(function)
}

fun CSProperty<Boolean>.onFalse(function: () -> Unit) =
    onChange { if (it.isFalse) function() }

fun CSProperty<Boolean>.onTrue(function: () -> Unit) =
    onChange { if (it.isTrue) function() }

fun CSProperty<Boolean>.actionTrue(function: () -> Unit): CSRegistration {
    if (isTrue) function()
    return onTrue(function)
}

fun CSProperty<Boolean>.actionFalse(function: () -> Unit): CSRegistration {
    if (isFalse) function()
    return onFalse(function)
}

fun <T> CSProperty<T>.onChangeOnce(listener: (argument: T) -> Unit): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange { argument: T ->
        registration.cancel()
        listener(argument)
    }
    return registration
}

fun CSProperty<Boolean>.listenUntilTrueOnce(
    listener: (argument: Boolean) -> Unit): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange { argument: Boolean ->
        if (argument) {
            registration.cancel()
            listener(true)
        }
    }
    return registration
}

fun CSProperty<Boolean>.listenUntilFalseOnce(
    listener: (argument: Boolean) -> Unit): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange { argument: Boolean ->
        if (!argument) {
            registration.cancel()
            listener(false)
        }
    }
    return registration
}

//fun <T : CSProperty<Int>> T.keepMax(maxValue: Int, fire: Boolean = true) = apply {
//    action { if (value > maxValue) value(maxValue, fire) }
//}

fun CSVariable<Boolean>.connect(property: CSProperty<Boolean>): CSRegistration =
    property.action { value = it }

fun <T> CSProperty<T>.eventBoolean(condition: (T) -> Boolean): CSEvent<Boolean> {
    val event = event<Boolean>()
    var value = condition(value)
    onChange {
        val newValue = condition(it)
        if (value != newValue) {
            event.fire(newValue)
            value = newValue
        }
    }
    return event
}

fun <T> CSProperty<T>.propertyBoolean(
    from: (T) -> Boolean, to: (Boolean) -> T,
    onChange: ArgFunc<Boolean>? = null): CSProperty<Boolean> {
    val property: CSProperty<Boolean> = property(from(value), onChange)
    lateinit var propertyOnChange: CSRegistration
    val thisOnChange = onChange {
        propertyOnChange.paused { property.value = from(value) }
    }
    propertyOnChange = property.onChange {
        thisOnChange.paused { value = to(it) }
    }
    return property
}

fun <T, V> CSProperty<T>.propertyComputed(
    from: (T) -> V, to: (V) -> T,
    onChange: ArgFunc<V>? = null): CSProperty<V> {
    val property: CSProperty<V> = property(from(value), onChange)
    lateinit var propertyOnChange: CSRegistration
    val thisOnChange = onChange {
        propertyOnChange.paused { property.value = from(value) }
    }
    propertyOnChange = property.onChange {
        thisOnChange.paused { value = to(it) }
    }
    return property
}

fun <Item : CSHasDestroy> CSProperty<Int>.updates(
    list: MutableList<Item>, function: (index: Int) -> Item): CSRegistration =
    action { value -> list.update(value, function) }

operator fun CSProperty<List<Int>>.set(index: Int, newValue: Int) {
    value = value.toMutableList().also { it[index] = newValue }
}