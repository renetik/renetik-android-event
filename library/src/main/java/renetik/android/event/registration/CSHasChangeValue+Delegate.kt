package renetik.android.event.registration

import renetik.android.core.lang.tuples.CSQuadruple
import renetik.android.event.registration.CSHasChange.Companion.action
import renetik.android.event.registration.CSHasChangeValue.Companion.action
import renetik.android.event.registration.CSHasChangeValue.Companion.onChange
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun <T> CSHasChangeValue<T>.delegate(
    parent: CSHasRegistrations? = null,
): CSHasChangeValue<T> = delegateValue(parent, from = { it })

fun <T, Return> CSHasChangeValue<T>.delegateValue(
    parent: CSHasRegistrations? = null,
    from: (T) -> Return,
): CSHasChangeValue<Return> = let { property ->
    object : CSHasChangeValue<Return> {
        override val value: Return get() = from(property.value)
        override fun onChange(function: (Return) -> Unit): CSRegistration {
            val value = CSValueFunction(this, value, function)
            return property.onChange {
                if (parent?.registrations.isActive) value(from(it))
            }.registerTo(parent)
        }
    }
}


fun <Argument, Return> List<CSHasChangeValue<Argument>>.delegate(
    parent: CSHasRegistrations? = null,
    from: (List<Argument>) -> Return,
): CSHasChangeValue<Return> = let { properties ->
    object : CSHasChangeValue<Return> {
        override val value: Return get() = from(properties.map { it.value })
        override fun onChange(function: (Return) -> Unit): CSRegistration {
            val value = CSValueFunction(this, value, function)
            return properties.onChange {
                if (parent?.registrations.isActive) value(from(it))
            }.registerTo(parent)
        }
    }
}


fun <T> CSHasChangeValue<T>.delegateIsChange(
    parent: CSHasRegistrations? = null,
): CSHasChangeValue<Boolean> = let { property ->
    object : CSHasChangeValue<Boolean> {
        override var value: Boolean = false
        override fun onChange(function: (Boolean) -> Unit): CSRegistration {
            return property.onChange {
                value = true
                if (parent?.registrations.isActive) function(true)
                value = false
            }.registerTo(parent)
        }
    }
}

fun <T, V, Return> Pair<CSHasChangeValue<T>, CSHasChangeValue<V>>.delegate(
    parent: CSHasRegistrations? = null,
    from: (T, V) -> Return,
): CSHasChangeValue<Return> = object : CSHasChangeValue<Return> {
    override val value: Return get() = from(first.value, second.value)
    override fun onChange(function: (Return) -> Unit): CSRegistration {
        val value = CSValueFunction(this, value, function)
        return CSRegistration(
            first.onChange {
                if (parent?.registrations.isActive) value(from(it,
                    second.value))
            },
            second.onChange {
                if (parent?.registrations.isActive) value(from(first.value,
                    it))
            },
        ).registerTo(parent)
    }
}

fun <T, V, K, Return> Triple<CSHasChangeValue<T>, CSHasChangeValue<V>,
        CSHasChangeValue<K>>.delegate(
    parent: CSHasRegistrations? = null,
    from: (T, V, K) -> Return,
): CSHasChangeValue<Return> = object : CSHasChangeValue<Return> {
    override val value: Return
        get() = from(first.value, second.value, third.value)

    override fun onChange(function: (Return) -> Unit): CSRegistration {
        val value = CSValueFunction(this, value, function)
        return CSRegistration(first.onChange {
            if (parent?.registrations.isActive) value(from(it, second.value, third.value))
        }, second.onChange {
            if (parent?.registrations.isActive) value(from(first.value, it, third.value))
        }, third.onChange {
            if (parent?.registrations.isActive) value(from(first.value, second.value, it))
        }).registerTo(parent)
    }
}

fun <T, V, K, L, Return> CSQuadruple<CSHasChangeValue<T>,
        CSHasChangeValue<V>, CSHasChangeValue<K>, CSHasChangeValue<L>>.delegate(
    parent: CSHasRegistrations? = null,
    from: (T, V, K, L) -> Return,
): CSHasChangeValue<Return> = object : CSHasChangeValue<Return> {
    override val value: Return
        get() = from(first.value, second.value, third.value, fourth.value)

    override fun onChange(function: (Return) -> Unit): CSRegistration {
        val value = CSValueFunction(this, value, function)
        return CSRegistration(first.onChange {
            if (parent?.registrations.isActive)
                value(from(it, second.value, third.value, fourth.value))
        }, second.onChange {
            if (parent?.registrations.isActive)
                value(from(first.value, it, third.value, fourth.value))
        }, third.onChange {
            if (parent?.registrations.isActive)
                value(from(first.value, second.value, it, fourth.value))
        }, fourth.onChange {
            if (parent?.registrations.isActive)
                value(from(first.value, second.value, third.value, it))
        }).registerTo(parent)
    }
}

inline fun <Return> CSHasChange<out Any>.delegate(
    parent: CSHasRegistrations? = null,
    crossinline from: () -> Return,
): CSHasChangeValue<Return> = let { property ->
    object : CSHasChangeValue<Return> {
        override val value: Return get() = from()
        override fun onChange(function: (Return) -> Unit): CSRegistration {
            val value = CSValueFunction(this, value, function)
            return property.onChange {
                if (parent?.registrations.isActive) value(from())
            }.registerTo(parent)
        }
    }
}

@JvmName("delegateChild")
fun <ChildValue> CSHasChange<out Any>.delegate(
    parent: CSHasRegistrations? = null,
    child: () -> CSHasChangeValue<ChildValue>,
): CSHasChangeValue<ChildValue> = let { property ->
    object : CSHasChangeValue<ChildValue> {
        override val value: ChildValue get() = child().value
        override fun onChange(function: (ChildValue) -> Unit): CSRegistration {
            val value = CSValueFunction(this, value, function)
            var registration: CSRegistration? = null
            var childRegistration: CSRegistration? = null
            val parentRegistration = property.action {
                childRegistration?.cancel()
                val childItem = child()
                if (parent?.registrations.isActive && registration.isActive) childItem.also {
                    value(it.value)
                }
                childRegistration = childItem.onChange {
                    if (parent?.registrations.isActive && registration.isActive) value(it)
                }
            }
            return CSRegistration(isActive = true, onCancel = {
                parentRegistration.cancel()
                childRegistration?.cancel()
            }).also { registration = it }.registerTo(parent)
        }
    }
}

@JvmName("delegateChild")
fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.delegateValue(
    parent: CSHasRegistrations? = null,
    child: (ParentValue) -> CSHasChangeValue<ChildValue>,
): CSHasChangeValue<ChildValue> = let { property ->
    object : CSHasChangeValue<ChildValue> {
        override val value: ChildValue get() = child(property.value).value
        override fun onChange(function: (ChildValue) -> Unit): CSRegistration {
            val value = CSValueFunction(this, value, function)
            var registration: CSRegistration? = null
            var childRegistration: CSRegistration? = null
            val parentRegistration = property.action { parentValue ->
                childRegistration?.cancel()
                val childItem = child(parentValue)
                if (parent?.registrations.isActive && registration.isActive) childItem.also {
                    value(it.value)
                }
                childRegistration = childItem.onChange {
                    if (parent?.registrations.isActive && registration.isActive) value(it)
                }
            }
            return CSRegistration(isActive = true, onCancel = {
                parentRegistration.cancel()
                childRegistration?.cancel()
            }).also { registration = it }.registerTo(parent)
        }
    }
}

@JvmName("delegateChild")
fun <Argument, Return> List<CSHasChangeValue<Argument>>.delegate(
    parent: CSHasRegistrations? = null,
    child: (List<Argument>) -> CSHasChangeValue<Return>,
): CSHasChangeValue<Return> = let { properties ->
    object : CSHasChangeValue<Return> {
        override val value: Return get() = child(properties.map { it.value }).value
        override fun onChange(function: (Return) -> Unit): CSRegistration {
            val value = CSValueFunction(this, value, function)
            var registration: CSRegistration? = null
            var childRegistration: CSRegistration? = null
            val parentRegistration = properties.action { parentValue ->
                childRegistration?.cancel()
                val childItem = child(parentValue)
                if (parent?.registrations.isActive && registration.isActive)
                    childItem.also { value(it.value) }
                childRegistration = childItem.onChange {
                    if (parent?.registrations.isActive && registration.isActive) value(it)
                }
            }
            return CSRegistration(isActive = true, onCancel = {
                parentRegistration.cancel()
                childRegistration?.cancel()
            }).also { registration = it }.registerTo(parent)
        }
    }
}

@JvmName("delegateChildren")
fun <Argument, Return> CSHasChangeValue<Argument>.delegate(
    parent: CSHasRegistrations? = null,
    children: (Argument) -> List<CSHasChange<out Return>>,
): CSHasChange<Return> = let { properties ->
    object : CSHasChange<Return> {
        override fun onChange(function: (Return) -> Unit): CSRegistration {
            var registrations: List<CSRegistration>? = null
            val parentRegistration = properties.action { parentValue ->
                registrations?.forEach(CSRegistration::cancel)
                val childList = children(parentValue)
                registrations = childList.map { it.onChange { function(it) } }
            }
            return CSRegistration(isActive = true, onCancel = {
                parentRegistration.cancel()
                registrations?.forEach(CSRegistration::cancel)
            }).registerTo(parent)
        }
    }
}

@JvmName("delegateChild")
fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.delegateChange(
    parent: CSHasRegistrations? = null,
    child: (ParentValue) -> CSHasChange<ChildValue>,
): CSHasChange<ChildValue> = let { property ->
    object : CSHasChange<ChildValue> {
        override fun onChange(function: (ChildValue) -> Unit): CSRegistration {
            var registration: CSRegistration? = null
            var childRegistration: CSRegistration? = null
            val parentRegistration = property.action { parentValue ->
                childRegistration?.cancel()
                val childItem = child(parentValue)
                childRegistration = childItem.onChange {
                    if (parent?.registrations.isActive && registration.isActive) function(
                        it)
                }
            }
            return CSRegistration(isActive = true, onCancel = {
                parentRegistration.cancel()
                childRegistration?.cancel()
            }).also { registration = it }.registerTo(parent)
        }
    }
}

fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.delegateChangeNullable(
    parent: CSHasRegistrations? = null,
    child: (ParentValue) -> CSHasChange<ChildValue>?,
): CSHasChange<ChildValue> = let { property ->
    object : CSHasChange<ChildValue> {
        override fun onChange(function: (ChildValue) -> Unit): CSRegistration {
            var registration: CSRegistration? = null
            var childRegistration: CSRegistration? = null
            val parentRegistration = property.action { parentValue ->
                childRegistration?.cancel()
                val childItem = child(parentValue)
                childRegistration = childItem?.onChange {
                    if (parent?.registrations.isActive && registration.isActive) function(
                        it)
                }
            }
            return CSRegistration(isActive = true, onCancel = {
                parentRegistration.cancel()
                childRegistration?.cancel()
            }).also { registration = it }.registerTo(parent)
        }
    }
}

@JvmName("delegateNullable")
fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.delegateNullable(
    parent: CSHasRegistrations? = null,
    child: (ParentValue) -> CSHasChangeValue<ChildValue>?,
): CSHasChangeValue<ChildValue?> = let { property ->
    object : CSHasChangeValue<ChildValue?> {
        override val value: ChildValue? get() = child(property.value)?.value
        override fun onChange(function: (ChildValue?) -> Unit): CSRegistration {
            val value = CSValueFunction(this, value, function)
            var registration: CSRegistration? = null
            var childRegistration: CSRegistration? = null
            val parentRegistration = property.action { parentValue ->
                childRegistration?.cancel()
                val childItem = child(parentValue)
                if (parent?.registrations.isActive && registration.isActive) childItem.also {
                    value(it?.value)
                }
                childRegistration = childItem?.onChange {
                    if (parent?.registrations.isActive && registration.isActive) value.invoke(
                        it)
                }
            }
            return CSRegistration(isActive = true, onCancel = {
                parentRegistration.cancel()
                childRegistration?.cancel()
            }).also { registration = it }.registerTo(parent)
        }
    }
}
