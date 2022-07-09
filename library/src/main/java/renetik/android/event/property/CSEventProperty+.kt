package renetik.android.event.property

import renetik.android.core.kotlin.primitives.isFalse
import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.isFalse
import renetik.android.core.lang.isTrue
import renetik.android.core.lang.property.CSVariable
import renetik.android.event.registration.CSMultiRegistration
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.pause

fun <T : CSEventProperty<*>> T.apply() = apply { invokeChange() }

fun <T> CSEventProperty<T>.onChange(function: (CSRegistration, T) -> Unit): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange {
        function(registration, it)
    }
    return registration
}

fun <T> CSEventProperty<T>.connect(property: CSEventProperty<T>): CSRegistration {
    value = property.value
    lateinit var propertyOnChange: CSRegistration
    val thisOnChange: CSRegistration = onChange { value ->
        propertyOnChange.pause().use { property.value = value }
    }
    propertyOnChange = property.onChange { value ->
        thisOnChange.pause().use { this.value = value }
    }
    return CSMultiRegistration(thisOnChange, propertyOnChange)
}

fun <T> CSEventProperty<T?>.clear() = value(null)
val <T> CSEventProperty<T?>.isEmpty get() = value == null
val <T> CSEventProperty<T?>.isSet get() = !isEmpty

fun <T> CSEventProperty<T>.action(function: (T) -> Unit): CSRegistration {
    function(value)
    return onChange(function)
}

fun CSEventProperty<Boolean>.onFalse(function: () -> Unit) =
    onChange { if (it.isFalse) function() }

fun CSEventProperty<Boolean>.onTrue(function: () -> Unit) =
    onChange { if (it.isTrue) function() }

fun CSEventProperty<Boolean>.actionTrue(function: () -> Unit): CSRegistration {
    if (isTrue) function()
    return onTrue(function)
}

fun CSEventProperty<Boolean>.actionFalse(function: () -> Unit): CSRegistration {
    if (isFalse) function()
    return onFalse(function)
}

fun <T> CSEventProperty<T>.onChangeOnce(listener: (argument: T) -> Unit): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange { argument: T ->
        registration.cancel()
        listener(argument)
    }
    return registration
}

fun CSEventProperty<Boolean>.listenUntilTrueOnce(
    listener: (argument: Boolean) -> Unit): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange { argument: Boolean ->
        if (argument) {
            registration.cancel()
            listener(argument)
        }
    }
    return registration
}

fun CSEventProperty<Boolean>.listenUntilFalseOnce(
    listener: (argument: Boolean) -> Unit): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange { argument: Boolean ->
        if (!argument) {
            registration.cancel()
            listener(argument)
        }
    }
    return registration
}

fun <T : CSEventProperty<Int>> T.keepMax(maxValue: Int, fire: Boolean = true) = apply {
    if (value > maxValue) value(maxValue, fire)
    onChange { if (value > maxValue) value(maxValue, fire) }
}

fun CSVariable<Boolean>.connect(property: CSEventProperty<Boolean>): CSRegistration {
    value = property.isTrue
    return property.onChange { value = it }
}