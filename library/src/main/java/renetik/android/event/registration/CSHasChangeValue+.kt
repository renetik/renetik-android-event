@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import kotlinx.coroutines.suspendCancellableCoroutine
import renetik.android.core.lang.value.CSValue
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.destruct
import renetik.android.event.common.update
import renetik.android.event.property.CSLateProperty
import renetik.android.event.registration.CSHasChangeValue.Companion.delegate
import renetik.android.event.registration.CSHasChangeValue.Companion.hasChangeValue
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import kotlin.Result.Companion.success

suspend fun <T> CSHasChangeValue<T>.waitFor(condition: (T) -> Boolean) {
    if (!condition(value)) suspendCancellableCoroutine { coroutine ->
        var registration: CSRegistration? = null
        registration = onChange {
            if (condition(value)) {
                registration?.cancel()
                registration = null
                coroutine.resumeWith(success(Unit))
            }
        }
        coroutine.invokeOnCancellation { registration?.cancel() }
    }
}

inline fun <T> CSHasChangeValue<T?>.isNull(): CSHasChangeValue<Boolean> =
    isSetTo(null)

inline fun <T> CSHasChangeValue<T?>.isNotNull(): CSHasChangeValue<Boolean> =
    !isSetTo(null)

inline infix fun <T> CSHasChangeValue<T>.isSetTo(value: T): CSHasChangeValue<Boolean> =
    delegate(from = { it == value })

inline fun <reified T> CSHasChangeValue<*>.isOfType(): CSHasChangeValue<Boolean> =
    delegate(from = { it is T })

inline fun <T> CSHasChangeValue<T>.onValue(function: (T) -> Unit) {
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

fun <T> CSHasChangeValue<T?>.onNull(function: () -> Unit) =
    onChange { if (it == null) function() }

fun <T> CSHasChangeValue<T?>.onNotNull(function: () -> Unit) =
    onChange { if (it != null) function() }

fun <T> CSHasChangeValue<T?>.actionNull(function: () -> Unit): CSRegistration =
    action { if (it == null) function() }

fun <T> CSHasChangeValue<T?>.actionNotNull(function: () -> Unit) =
    action { if (it != null) function() }

fun <T> CSHasChangeValue<T?>.actionNotNull(function: (T) -> Unit) =
    action { if (it != null) function(it) }

inline fun <Value> CSHasChangeValue<Value>.onChangeTo(value: Value,
    crossinline onChange: () -> Unit): CSRegistration =
    onChange { if (this.value == value) onChange() }

fun <Value> CSHasChangeValue<Value>.hasValue(
    parent: CSHasDestruct? = null, value: Value,
): CSHasChangeValue<Boolean> = hasChangeValue(parent, from = { it == value })

@JvmName("onChangeChild")
inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.onChange(
    crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>,
    noinline onChange: (ChildValue) -> Unit): CSRegistration {
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
    crossinline onChange: (ChildValue) -> Unit): CSRegistration {
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
    crossinline action: (ChildValue) -> Unit): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = action { parentValue ->
        childRegistration?.cancel()
        childRegistration = child(parentValue).action { childValue -> action(childValue) }
    }
    return CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.action(
    crossinline nullableChild: (ParentValue) -> CSHasChangeValue<ChildValue>?,
    crossinline onChange: ((ChildValue?) -> Unit)): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = action { parentValue ->
        childRegistration?.cancel()
        childRegistration = nullableChild(parentValue)?.let {
            it.action { childValue -> onChange(childValue) }
        } ?: run { onChange(null); null }
    }
    return CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

inline fun <Item : CSHasDestruct> CSHasChangeValue<Int>.updates(list: MutableList<Item>,
    noinline function: (index: Int) -> Item): CSRegistration =
    action { value -> list.update(value, function) }

fun <V, Instance> CSHasChangeValue<V>.lazyDestructFactory(
    parent: CSHasRegistrations? = null,
    createInstance: (V) -> Instance): CSValue<Instance> where Instance : CSHasDestruct =
    object : CSValue<Instance> {
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
    createInstance: (V) -> Instance): CSValue<Instance> where Instance : CSHasDestruct =
    lazyFactory(property) { previousInstance, param ->
        previousInstance?.onDestruct()
        createInstance(param)
    }

fun <V, Instance> CSHasRegistrations.lazyFactory(property: () -> CSHasChangeValue<V>,
    createInstance: (Instance?, V) -> Instance): CSValue<Instance> where Instance : CSHasDestruct =
    object : CSValue<Instance> {
        var instance: Instance? = null
        override val value: Instance
            get() {
                if (instance == null) this@lazyFactory + property().action {
                    instance = createInstance(instance, it)
                }
                return instance!!
            }
    }