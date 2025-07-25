@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.suspendCancellableCoroutine
import renetik.android.core.lang.value.CSValue
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.destruct
import renetik.android.event.common.update
import renetik.android.event.property.CSLateProperty
import renetik.android.event.registration.CSHasChangeValue.Companion.delegate
import renetik.android.event.registration.CSHasChangeValue.Companion.emptyNullable
import renetik.android.event.registration.CSHasChangeValue.Companion.hasChangeValue
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import kotlin.Result.Companion.success

@JvmName("destructPreviousNullable")
fun <T : CSHasDestruct, P : CSHasChangeValue<T?>> P.destructPrevious() =
    apply { onChangeFrom { it?.destruct() } }

fun <T : CSHasDestruct, P : CSHasChangeValue<T>> P.destructPrevious() =
    apply { onChangeFrom { it.destruct() } }

val <T> CSHasChangeValue<T>?.nullable: CSHasChangeValue<T?>
    get() = this?.delegate(from = { it }) ?: emptyNullable()

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

inline fun <T> CSHasChangeValue<T?>.isNull(): CSHasChangeValue<Boolean> = isSetTo(null)

inline fun <T> CSHasChangeValue<T?>.isNotNull(): CSHasChangeValue<Boolean> = !isSetTo(null)

inline infix fun <T> CSHasChangeValue<T>.isSetTo(value: T): CSHasChangeValue<Boolean> =
    delegate(from = { it == value })

inline infix fun <T> CSHasChangeValue<T>.isTrue(
    crossinline condition: (T) -> Boolean
): CSHasChangeValue<Boolean> = delegate(from = { condition(it) })

inline infix fun <T> CSHasChangeValue<T>.isFalse(
    crossinline condition: (T) -> Boolean
): CSHasChangeValue<Boolean> = delegate(from = { !condition(it) })

inline fun <T> CSHasChangeValue<T>.isSetTo(parent: CSHasRegistrations, value: T)
        : CSHasChangeValue<Boolean> = hasChangeValue(parent, from = { it == value })

inline infix fun <T> CSHasChangeValue<T>.isNotSetTo(value: T): CSHasChangeValue<Boolean> =
    delegate(from = { it != value })

inline fun <reified T> CSHasChangeValue<*>.isOfType(): CSHasChangeValue<Boolean> =
    delegate(from = { it is T })

inline fun <reified T> CSHasChangeValue<*>.asType(): CSHasChangeValue<T?> =
    delegate(from = { it as? T })

inline fun <reified T> CSHasChangeValue<*>.onType(
    crossinline function: (T) -> Unit
): CSRegistration = onChange {
    (it as? T)?.also { type -> function(type) }
}

inline fun <T> CSHasChangeValue<T>.onValue(function: (T) -> Unit) {
    val lateProperty = (this as? CSLateProperty<T>)
    if (lateProperty != null) lateProperty.lateValue?.let(function)
    else function(value)
}

fun <T> CSHasChangeValue<T>.action(function: (T) -> Unit): CSRegistration {
    onValue(function)
    return onChange(function)
}

fun <T> CSHasChangeValue<T>.actionLaunch(function: suspend (T) -> Unit): CSRegistration {
    val registrations = CSRegistrationsMap(this)
    registrations + action { param ->
        registrations + Main.launch { function(param) }
    }
    return registrations
}

fun <T> CSHasChangeValue<T>.onChangeFrom(
    function: (from: T) -> Unit,
): CSRegistration {
    var value = this.value
    return onChange { function(value); value = it }
}

fun <T> CSHasChangeValue<T?>.onChangeNotNull(
    function: (from: T) -> Unit,
): CSRegistration = onChange { it?.let(function) }

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

fun <T> CSHasChangeValue<out T?>.onNull(function: () -> Unit) =
    onChange { if (it == null) function() }

fun <T> CSHasChangeValue<out T?>.onNotNull(function: () -> Unit) =
    onChange { if (it != null) function() }

fun <T> CSHasChangeValue<out T?>.onNotNull(function: (T) -> Unit) =
    onChange { if (it != null) function(it) }

fun <T> CSHasChangeValue<T?>.actionNull(function: () -> Unit): CSRegistration =
    action { if (it == null) function() }

fun <T> CSHasChangeValue<T?>.actionNotNull(function: () -> Unit) =
    action { if (it != null) function() }

fun <T> CSHasChangeValue<T?>.actionNotNull(function: (T) -> Unit) =
    action { if (it != null) function(it) }

inline fun <Value> CSHasChangeValue<Value>.onChangeTo(
    value: Value, crossinline onChange: () -> Unit
): CSRegistration =
    onChange { if (this.value == value) onChange() }

fun <Value> CSHasChangeValue<Value>.hasValue(
    parent: CSHasRegistrations? = null, value: Value,
): CSHasChangeValue<Boolean> = hasChangeValue(parent, from = { it == value })

@JvmName("onChangeChildHasValue")
inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.onChange(
    crossinline valueChild: (ParentValue) -> CSHasChangeValue<ChildValue>,
    noinline onChange: (ChildValue) -> Unit
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = action {
        childRegistration?.cancel()
        childRegistration = valueChild(value).onChange(onChange)
    }
    return CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

@JvmName("onChangeChild")
inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.onChange(
    crossinline child: (ParentValue) -> CSHasChange<ChildValue>,
    noinline onChange: (ChildValue) -> Unit
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = action {
        childRegistration?.cancel()
        childRegistration = child(value).onChange(onChange)
    }
    return CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

@JvmName("onChangeChildNullable")
inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.onChange(
    crossinline valueChild: (ParentValue) -> CSHasChangeValue<ChildValue>?,
    crossinline onChange: (ChildValue) -> Unit
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = action { parentValue ->
        childRegistration?.cancel()
        childRegistration = valueChild(parentValue)?.onChange { childValue ->
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
        childRegistration = child(parentValue).action { childValue -> action(childValue) }
    }
    return CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.action(
    crossinline nullableChild: (ParentValue) -> CSHasChangeValue<ChildValue>?,
    crossinline onChange: ((ChildValue?) -> Unit)
): CSRegistration {
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

inline fun <Item : CSHasDestruct> CSHasChangeValue<Int>.updates(
    list: MutableList<Item>,
    noinline function: (index: Int) -> Item
): CSRegistration = action { value -> list.update(value, function = function) }

fun <V, Instance> CSHasChangeValue<V>.lazyDestructFactory(
    parent: CSHasRegistrations? = null,
    createInstance: (V) -> Instance
): CSValue<Instance> where Instance : CSHasDestruct =
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
    createInstance: (V) -> Instance
): CSValue<Instance> where Instance : CSHasDestruct =
    lazyFactory(property) { previousInstance, param ->
        previousInstance?.onDestruct()
        createInstance(param)
    }

fun <V, Instance> CSHasRegistrations.lazyFactory(
    property: () -> CSHasChangeValue<V>,
    createInstance: (Instance?, V) -> Instance
): CSValue<Instance> where Instance : CSHasDestruct =
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

val CSHasChangeValue<out Any?>.eventIsNull: CSHasChange<Unit>
    get() = delegate(from = { it == null }).eventIsTrue

val CSHasChangeValue<out Any?>.eventIsNotNull: CSHasChange<Unit>
    get() = delegate(from = { it != null }).eventIsTrue

fun <T> CSHasChangeValue<out T?>.eventIsNotNull(): CSHasChange<T> {
    val self = this
    return object : CSHasChange<T> {
        override fun onChange(function: (T) -> Unit): CSRegistration {
            var wasNull: Boolean = value == null
            return self.onChange {
                if (it != null) {
                    if (wasNull) {
                        function(it)
                        wasNull = false
                    }
                } else wasNull = true
            }
        }
    }
}