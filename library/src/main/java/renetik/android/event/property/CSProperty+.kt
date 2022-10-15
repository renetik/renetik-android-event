package renetik.android.event.property

import renetik.android.core.kotlin.primitives.isFalse
import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.value.isFalse
import renetik.android.core.lang.value.isTrue
import renetik.android.core.lang.variable.CSVariable
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.update
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.paused

fun <T : CSProperty<*>> T.apply() = apply { fireChange() }

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

fun <T> CSHasChangeValue<T>.action(function: (T) -> Unit): CSRegistration {
    function(value)
    return onChange(function)
}

fun CSHasChangeValue<Boolean>.onFalse(function: () -> Unit) =
    onChange { if (it.isFalse) function() }

fun CSHasChangeValue<Boolean>.onTrue(function: () -> Unit) =
    onChange { if (it.isTrue) function() }

fun CSHasChangeValue<Boolean>.actionTrue(function: () -> Unit): CSRegistration {
    if (isTrue) function()
    return onTrue(function)
}

fun CSHasChangeValue<Boolean>.actionFalse(function: () -> Unit): CSRegistration {
    if (isFalse) function()
    return onFalse(function)
}

fun <T> CSHasChangeValue<T>.onChangeOnce(listener: (argument: T) -> Unit): CSRegistration {
    lateinit var registration: CSRegistration
    registration = onChange { argument: T ->
        registration.cancel()
        listener(argument)
    }
    return registration
}

fun CSHasChangeValue<Boolean>.listenUntilTrueOnce(
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

fun CSHasChangeValue<Boolean>.listenUntilFalseOnce(
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

fun CSVariable<Boolean>.connect(property: CSProperty<Boolean>): CSRegistration =
    property.action { value = it }

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

fun <T, V> CSProperty<T>.valueComputed(
    from: (T) -> V, onChange: ArgFunc<V>? = null): CSHasChangeValue<V> {
    val property: CSProperty<V> = property(from(value), onChange)
    onChange { property.value = from(value) }
    return property
}

fun <Item : CSHasDestruct> CSHasChangeValue<Int>.updates(
    list: MutableList<Item>, function: (index: Int) -> Item): CSRegistration =
    action { value -> list.update(value, function) }

operator fun CSProperty<List<Int>>.set(index: Int, newValue: Int) {
    value = value.toMutableList().also { it[index] = newValue }
}