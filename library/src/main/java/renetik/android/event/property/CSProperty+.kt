package renetik.android.event.property

import renetik.android.core.kotlin.primitives.percentOf
import renetik.android.core.kotlin.primitives.toPercentOf
import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.variable.CSVariable
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.CSHasChangeValue
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.action
import renetik.android.event.registration.paused
import renetik.android.event.registration.register
import kotlin.math.roundToInt

fun <T : CSProperty<*>> T.fire() = apply { fireChange() }
fun <T : CSProperty<*>> T.paused(
    fire: Boolean = true, function: (T).() -> Unit
) = apply {
    pause()
    function(this)
    resume(fire)
}

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

inline fun <T, V> CSProperty<T>.computed(
    parent: CSHasRegistrations? = null,
    crossinline from: (T) -> V, crossinline to: (V) -> T,
    noinline onChange: ArgFunc<V>? = null,
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

inline fun <T, V> CSProperty<T>.computed(
    parent: CSHasRegistrations? = null,
    crossinline get: (T) -> V, crossinline set: (CSProperty<T>, V) -> Unit,
    noinline onChange: ArgFunc<V>? = null,
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

operator fun CSProperty<Int>.rangeTo(
    other: CSProperty<Int>,
): ClosedRange<Int> = value..other.value

operator fun CSProperty<Int>.rangeUntil(
    other: CSProperty<Int>,
): ClosedRange<Int> = value..<other.value

fun CSProperty<Int>.computedAsPercentOf(
    parent: CSHasRegistrations, max: Int = 100,
): CSProperty<Float> = computed(
    parent, from = { it.toPercentOf(max) },
    to = { it.percentOf(max.toFloat()).roundToInt() }
)