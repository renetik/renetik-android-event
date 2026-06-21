package renetik.android.event.registration

import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

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
