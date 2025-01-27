package renetik.android.event.property

import renetik.android.core.kotlin.primitives.percentOf
import renetik.android.core.kotlin.primitives.toPercentOf
import renetik.android.core.lang.ArgFunc
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.CSHasRegistrations
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.action
import renetik.android.event.registration.paused
import renetik.android.event.registration.register
import kotlin.math.roundToInt
import kotlin.properties.Delegates.notNull

//fun <T> CSProperty<T?>.lateProperty(): CSProperty<T> =
//    delegate(from = { it!! }, to = { it })TODO: We dont have such delegate..

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

//inline fun <T, V : Any> CSProperty<T>.delegate(
//    parent: CSHasDestruct? = null,
//    crossinline from: (T) -> V,
//    crossinline to: (V) -> T,
//    noinline onChange: ArgFunc<V>? = null,
//) = let { property ->
//    object : CSPropertyBase<V>(parent, onChange) {
//        var isInitialized = false
//        override var value: V by notNull()
//
////        override var value: V
////            get() = from(property.value)
////            set(value) = property assign to(value)
//
//        init {
//            property.onChange { value(from(it)) }
//        }
//
//        override fun value(newValue: V, fire: Boolean) {
//            if (isInitialized && value == newValue) return
//            value = newValue
//            isInitialized = true
//            onValueChanged(newValue, fire)
//        }
//    }
//}

inline fun <T, V> CSProperty<T>.computed(
    parent: CSHasRegistrations? = null,
    crossinline from: (T) -> V,
    crossinline to: (V) -> T,
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

inline fun <T> CSProperty<T>.computedFrom(
    parent: CSHasRegistrations? = null,
    crossinline from: (T) -> T,
    noinline onChange: ArgFunc<T>? = null,
): CSProperty<T> = computed(parent, from, to = { it }, onChange)

inline fun <T> CSProperty<T>.computedTo(
    parent: CSHasRegistrations? = null,
    crossinline to: (T) -> T,
    noinline onChange: ArgFunc<T>? = null,
): CSProperty<T> = computed(parent, from = { it }, to, onChange)

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

inline fun <T, V> CSProperty<T>.computed(
    parent: CSHasRegistrations? = null,
    crossinline child: (T) -> CSProperty<V>,
    noinline onChange: ArgFunc<V>? = null,
): CSProperty<V> {
    lateinit var propertyOnChange: CSRegistration
    val property: CSProperty<V> = property(child(value).value, onChange)
    var fromAction: CSRegistration? = null
    val thisOnChange = onChange {
        fromAction = child(value).action {
            propertyOnChange.paused { property.value(it) }
        }.let {
            parent?.register(fromAction, it) ?: run { fromAction?.cancel(); it }
        }
    }.also { parent?.register(it) }
    propertyOnChange = property.onChange {
        thisOnChange.paused { child(value).value(it) }
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

operator fun CSProperty<Boolean>.not() = computed(from = { !it }, to = { !it })
fun CSProperty<Boolean>.computed(parent: CSHasRegistrations? = null) =
    computed(parent, from = { it }, to = { it })