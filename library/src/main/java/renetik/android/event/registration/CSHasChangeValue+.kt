package renetik.android.event.registration

import kotlinx.coroutines.suspendCancellableCoroutine
import renetik.android.core.kotlin.primitives.isFalse
import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.value.CSValue
import renetik.android.core.lang.value.isFalse
import renetik.android.core.lang.value.isTrue
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.destruct
import renetik.android.event.common.update
import renetik.android.event.property.CSLateProperty
import renetik.android.event.property.CSProperty
import renetik.android.event.property.CSProperty.Companion.property
import renetik.android.event.registration.CSHasChangeValue.Companion.delegate
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import kotlin.Result.Companion.success

suspend fun CSHasChangeValue<Boolean>.waitIsTrue(): Unit = suspendCancellableCoroutine {
    if (isTrue) it.resumeWith(success(Unit))
    else {
        var registration: CSRegistration? = null
        registration = onTrue {
            registration?.cancel()
            registration = null
            it.resumeWith(success(Unit))
        }
        it.invokeOnCancellation { registration?.cancel() }
    }
}

suspend fun CSHasChangeValue<Boolean>.waitIsFalse(): Unit = suspendCancellableCoroutine {
    if (isFalse) it.resumeWith(success(Unit))
    else {
        var registration: CSRegistration? = null
        registration = onFalse {
            registration?.cancel()
            registration = null
            it.resumeWith(success(Unit))
        }
        it.invokeOnCancellation { registration?.cancel() }
    }

}

fun <T> CSHasChangeValue<T>.onValue(function: (T) -> Unit) {
    val lateProperty = (this as? CSLateProperty<T>)
    if (lateProperty != null) lateProperty.lateValue?.let(function)
    else function(value)
}

fun <T> CSHasChangeValue<T>.action(function: (T) -> Unit): CSRegistration {
    onValue(function)
    return onChange(function)
}

fun <T> CSHasChangeValue<T>.onChangeFromTo(
    function: (from: T, to: T) -> Unit,
): CSRegistration {
    var value = this.value
    return onChange { function(value, it); value = it }
}

fun <T> CSHasChangeValue<T>.actionFromTo(
    function: (from: T?, to: T) -> Unit,
): CSRegistration {
    function(null, value)
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

fun <T> CSHasChangeValue<T?>.onNull(function: () -> Unit) =
    onChange { if (it == null) function() }

fun <T> CSHasChangeValue<T?>.onNotNull(function: () -> Unit) =
    onChange { if (it != null) function() }

fun <T> CSHasChangeValue<T?>.actionNull(function: () -> Unit): CSRegistration =
    action { if (it == null) function() }

fun <T> CSHasChangeValue<T?>.actionNotNull(function: () -> Unit) =
    action { if (it != null) function() }

operator fun CSHasChangeValue<Boolean>.not() = delegate(from = { !it })

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

@JvmName("onChangeChild")
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

@JvmName("onChangeChildNullable")
inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.onChange(
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

@JvmName("onChangeChildChange")
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

@JvmName("actionChild")
inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.action(
    crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>,
    crossinline action: (ChildValue) -> Unit
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = action { parentValue ->
        childRegistration?.cancel()
        childRegistration = child(parentValue)
            .action { childValue -> action(childValue) }
    }
    return CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.action(
    crossinline optionalChild: (ParentValue) -> CSHasChangeValue<ChildValue>?,
    crossinline onChange: ((ChildValue?) -> Unit)
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = action { parentValue ->
        childRegistration?.cancel()
        childRegistration = optionalChild(parentValue)?.let {
            it.action { childValue -> onChange(childValue) }
        } ?: run { onChange(null); null }
    }
    return CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

//Inline not possible, don't try again :)
fun <Item : CSHasDestruct> CSHasChangeValue<Int>.updates(
    list: MutableList<Item>, function: (index: Int) -> Item
): CSRegistration = action { value -> list.update(value, function) }

fun <V, Instance> CSHasChangeValue<V>.lazyDestructFactory(
    parent: CSHasRegistrations? = null,
    createInstance: (V) -> Instance
): CSValue<Instance>
        where Instance : CSHasDestruct = object : CSValue<Instance> {
    var instance: Instance? = null
    override val value: Instance
        get() {
            if (instance == null) action {
                instance?.destruct()
                instance = createInstance(it)
            }.also { parent?.register(it) }
            return instance!!
        }
}

fun <V, Instance> CSHasRegistrations.lazyDestructFactory(
    property: () -> CSHasChangeValue<V>,
    createInstance: (V) -> Instance
): CSValue<Instance> where Instance : CSHasDestruct =
    lazyFactory(property) { previousInstance, param ->
        previousInstance?.onDestruct()
        createInstance(param)
    }

fun <V, Instance> CSHasRegistrations.lazyFactory(
    property: () -> CSHasChangeValue<V>,
    createInstance: (Instance?, V) -> Instance
): CSValue<Instance> where Instance : CSHasDestruct = object : CSValue<Instance> {
    var instance: Instance? = null
    override val value: Instance
        get() {
            if (instance == null) this@lazyFactory + property().action {
                instance = createInstance(instance, it)
            }
            return instance!!
        }
}

// I don't even understand this now... and not used.
//fun <V, Instance> CSHasRegistrations.lazyHasChangeValue(
//    property: () -> CSHasChangeValue<V>,
//    createInstance: (V) -> Instance
//): CSHasChangeValue<Instance> where Instance : CSHasDestruct {
//    var instance: Instance? = null
//    return object : CSHasChangeValue<Instance> {
//        val outputModelInstance1: CSHasChangeValue<Instance> by lazy {
//            property().hasChangeValue(this@lazyHasChangeValue, from = {
//                createInstance(it).also {
//                    instance?.destruct()
//                    instance = it
//                }
//            })
//        }
//        override val value: Instance get() = outputModelInstance1.value
//
//        override fun onChange(function: (Instance) -> Unit): CSRegistration =
//            outputModelInstance1.onChange(function)
//    }
//}