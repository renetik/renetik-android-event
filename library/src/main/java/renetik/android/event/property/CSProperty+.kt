package renetik.android.event.property

import renetik.android.core.kotlin.primitives.isFalse
import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.value.isFalse
import renetik.android.core.lang.value.isTrue
import renetik.android.core.lang.variable.CSVariable
import renetik.android.core.lang.void
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.update
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.paused
import renetik.android.event.registration.register
import kotlin.properties.Delegates.notNull

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
    if (isTrue()) function()
    return onTrue(function)
}

fun CSHasChangeValue<Boolean>.actionFalse(function: () -> Unit): CSRegistration {
    if (isFalse()) function()
    return onFalse(function)
}

//fun <T> CSHasChangeValue<T>.onChangeOnce(listener: (argument: T) -> Unit): CSRegistration {
//    lateinit var registration: CSRegistration
//    registration = onChange { argument: T ->
//        registration.cancel()
//        listener(argument)
//    }
//    return registration
//}

//fun CSHasChangeValue<Boolean>.listenUntilTrueOnce(
//    listener: (argument: Boolean) -> Unit
//): CSRegistration {
//    lateinit var registration: CSRegistration
//    registration = onChange { argument: Boolean ->
//        if (argument) {
//            registration.cancel()
//            listener(true)
//        }
//    }
//    return registration
//}

fun CSHasChangeValue<Boolean>.listenUntilFalseOnce(
    listener: (argument: Boolean) -> Unit
): CSRegistration {
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
    parent: CSHasRegistrations? = null,
    from: (T) -> Boolean, to: (Boolean) -> T,
    onChange: ArgFunc<Boolean>? = null
): CSProperty<Boolean> {
    val property: CSProperty<Boolean> = property(from(value), onChange)
    lateinit var propertyOnChange: CSRegistration
    val thisOnChange = onChange {
        propertyOnChange.paused { property.value = from(value) }
    }.also { parent?.register(it) }
    propertyOnChange = property.onChange {
        thisOnChange.paused { value = to(it) }
    }
    return property
}

fun <T, V> CSProperty<T>.computed(
    parent: CSHasRegistrations? = null,
    from: (T) -> V, to: (V) -> T,
    onChange: ArgFunc<V>? = null
): CSProperty<V> {
    val property: CSProperty<V> = property(from(value), onChange)
    lateinit var propertyOnChange: CSRegistration
    val thisOnChange = onChange {
        propertyOnChange.paused { property.value = from(value) }
    }.also { parent?.register(it) }
    propertyOnChange = property.onChange {
        thisOnChange.paused { value = to(it) }
    }
    return property
}

fun <T, V> CSProperty<T>.computed(
    parent: CSHasRegistrations? = null,
    get: (T) -> V, set: (CSProperty<T>, V) -> void,
    onChange: ArgFunc<V>? = null
): CSProperty<V> {
    val property: CSProperty<V> = property(get(value), onChange)
    lateinit var propertyOnChange: CSRegistration
    val thisOnChange = onChange {
        propertyOnChange.paused { property.value = get(value) }
    }.also { parent?.register(it) }
    propertyOnChange = property.onChange {
        thisOnChange.paused { set(this, it) }
    }
    return property
}

@Deprecated("Use hasChangeValueDelegate probably :)")
fun <T, V> CSProperty<T>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (T) -> V, onChange: ArgFunc<V>? = null
): CSHasChangeValue<V> {
    val property: CSProperty<V> = property(from(value), onChange)
    onChange { property.value = from(value) }.also { parent?.register(it) }
    return property
}

fun <T, V> CSProperty<T>.hasChangeValueDelegate(
    parent: CSHasRegistrations? = null,
    from: (T) -> V, onChange: ArgFunc<V>? = null
): CSHasChangeValue<V> = this.let { property ->
    object : CSHasChangeValue<V> {
        override val value: V get() = from(property.value)
        override fun onChange(function: (V) -> void) =
            property.onChange {
                val value = from(it)
                onChange?.invoke(value)
                function(value)
            }.also { parent?.register(it) }
    }
}

@Deprecated("Use hasChangeValueDelegate probably :)")
fun <T, V, X> Pair<CSProperty<T>, CSProperty<V>>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (T, V) -> X, onChange: ArgFunc<X>? = null
): CSHasChangeValue<X> {
    val property: CSProperty<X> = property(from(first.value, second.value), onChange)
    first.onChange { property.value = from(it, second.value) }.also { parent?.register(it) }
    second.onChange { property.value = from(first.value, it) }.also { parent?.register(it) }
    return property
}

fun <T, V, X> Pair<CSProperty<T>, CSProperty<V>>.hasChangeValueDelegate(
    parent: CSHasRegistrations? = null,
    from: (T, V) -> X, onChange: ArgFunc<X>? = null
): CSHasChangeValue<X> = this.let { properties ->
    object : CSHasChangeValue<X> {
        override val value: X get() = from(properties.first.value, properties.second.value)
        override fun onChange(function: (X) -> void) = CSRegistration(
            first.onChange {
                val value = from(it, second.value)
                onChange?.invoke(value)
                function(value)
            }.also { parent?.register(it) },
            second.onChange {
                val value = from(first.value, it)
                onChange?.invoke(value)
                function(value)
            }.also { parent?.register(it) }
        )
    }
}

operator fun CSProperty<List<Int>>.set(index: Int, newValue: Int) {
    value = value.toMutableList().also { it[index] = newValue }
}