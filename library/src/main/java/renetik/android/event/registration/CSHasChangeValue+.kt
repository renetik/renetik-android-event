package renetik.android.event.registration

import renetik.android.core.kotlin.primitives.isFalse
import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.value.CSValue
import renetik.android.core.lang.value.isFalse
import renetik.android.core.lang.value.isTrue
import renetik.android.core.lang.void
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.destruct
import renetik.android.event.common.update
import renetik.android.event.property.CSProperty
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.CSHasChangeValue.Companion.hasChangeValue
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun <T> CSHasChangeValue<T>.action(function: (T) -> Unit): CSRegistration {
    function(value)
    return onChange(function)
}

fun <T> CSHasChangeValue<T>.onChangeFromTo(
    function: (from: T, to: T) -> Unit,
): CSRegistration {
    var value = this.value
    return onChange { function(value, it); value = it }
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

inline fun <Value> CSHasChangeValue<Value>.onChangeTo(
    value: Value, crossinline onChange: () -> Unit
): CSRegistration = onChange { if (this.value == value) onChange() }

inline fun <T, V> CSHasChangeValue<T>.computed(
    parent: CSHasRegistrations? = null,
    crossinline from: (T) -> V, noinline onChange: ArgFunc<V>? = null
): CSHasChangeValue<V> {
    val property: CSProperty<V> = property(from(value), onChange)
    onChange { property.value = from(value) }.also { parent?.register(it) }
    return property
}

fun <Value> CSHasChangeValue<Value>.hasValue(
    parent: CSHasRegistrations? = null, value: Value,
): CSHasChangeValue<Boolean> = computed(parent, from = { it == value })

inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.onChange(
    crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>,
    noinline onChange: (ChildValue) -> Unit
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = onChange {
        childRegistration?.cancel()
        childRegistration = child(value).action(onChange)
    }
    return CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.onChange(
    crossinline childChange: (ParentValue) -> CSHasChange<ChildValue>,
    crossinline onChange: () -> Unit,
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = onChange {
        childRegistration?.cancel()
        childRegistration = childChange(value).onChange(onChange)
        onChange()
    }
    return CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.action(
    crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>,
    noinline onChange: (ChildValue) -> Unit
): CSRegistration {
    onChange(child(value).value)
    return onChange(child, onChange)
}

inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.onChangeNullableChild(
    crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>?,
    crossinline onChange: (ChildValue) -> Unit
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = action { parentValue ->
        childRegistration?.cancel()
        childRegistration = child(parentValue)?.onChange { childValue ->
            onChange(childValue)
        }
    }
    return CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.actionChild(
    crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>,
    crossinline onChange: (ChildValue) -> Unit
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = action { parentValue ->
        childRegistration?.cancel()
        childRegistration = child(parentValue).action { childValue -> onChange(childValue) }
    }
    return CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.actionNullableChild(
    crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>?,
    crossinline onChange: (ChildValue?) -> Unit
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = action { parentValue ->
        childRegistration?.cancel()
        childRegistration = child(parentValue)?.let {
            it.action { childValue -> onChange(childValue) }
        } ?: run { onChange(null); null }
    }
    return CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

//inline not possible
fun <Item : CSHasDestruct> CSHasChangeValue<Int>.updates(
    list: MutableList<Item>, function: (index: Int) -> Item
): CSRegistration = action { value -> list.update(value, function) }

fun <V, Instance> CSHasChangeValue<V>.lazyFactory(
    createInstance: (V) -> Instance
): CSValue<Instance> where Instance : CSHasDestruct = object : CSValue<Instance> {
    var outputModelInstance: Instance? = null
    override val value: Instance
        get() {
            if (outputModelInstance == null) action {
                outputModelInstance?.destruct()
                outputModelInstance = createInstance(it)
            }
            return outputModelInstance!!
        }
}

fun <V, Instance> CSHasRegistrations.lazyFactory(
    property: () -> CSHasChangeValue<V>,
    createInstance: (V) -> Instance
): CSValue<Instance> where Instance : CSHasDestruct = object : CSValue<Instance> {
    var outputModelInstance: Instance? = null
    override val value: Instance
        get() {
            if (outputModelInstance == null)
                this@lazyFactory + property().action {
                    outputModelInstance?.destruct()
                    outputModelInstance = createInstance(it)
                }
            return outputModelInstance!!
        }
}

fun <V, Instance> CSHasRegistrations.lazyHasChangeValue(
    property: () -> CSHasChangeValue<V>,
    createInstance: (V) -> Instance
): CSHasChangeValue<Instance> where Instance : CSHasDestruct {
    var outputModelInstance: Instance? = null
    return object : CSHasChangeValue<Instance> {
        val outputModelInstance1: CSHasChangeValue<Instance> by lazy {
            property().hasChangeValue(this@lazyHasChangeValue, from = {
                createInstance(it).also {
                    outputModelInstance?.destruct()
                    outputModelInstance = it
                }
            })
        }
        override val value: Instance get() = outputModelInstance1.value

        override fun onChange(function: (Instance) -> void): CSRegistration =
            outputModelInstance1.onChange(function)
    }
}