@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import renetik.android.core.kotlin.className
import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.notNull
import renetik.android.core.lang.synchronized
import renetik.android.core.lang.tuples.CSQuadruple
import renetik.android.core.lang.tuples.CSQuintuple
import renetik.android.core.lang.tuples.CSSeventuple
import renetik.android.core.lang.tuples.CSSixtuple
import renetik.android.core.lang.tuples.to
import renetik.android.core.lang.value.CSValue
import renetik.android.core.lang.variable.assign
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.common.CSDebouncer.Companion.debouncer
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.destruct
import renetik.android.event.property.CSProperty.Companion.lateProperty
import renetik.android.event.registration.CSHasChange.Companion.action
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

interface CSHasChangeValue<T> : CSValue<T>, CSHasChange<T> {
    companion object {
        inline fun <T> emptyNullable() = object : CSHasChangeValue<T?> {
            override val value: T? = null
            override fun onChange(function: (T?) -> Unit) = CSRegistration.Empty
        }

        inline fun <T> empty(value: T) = object : CSHasChangeValue<T> {
            override val value: T = value
            override fun onChange(function: (T) -> Unit) = CSRegistration.Empty
        }

        class ValueFunction<Return>(
            val parent: Any,
            private var value: Return,
            private val function: (Return) -> Unit,
        ) {
            operator fun invoke(newValue: Return) = synchronized(parent) {
                if (value != newValue) {
                    value = newValue
                    function(newValue)
                }
            }
        }

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
                    val value = ValueFunction(this, value, function)
                    return property.onChange {
                        if (parent.isActive) value(from(it))
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
                    val value = ValueFunction(this, value, function)
                    return properties.onChange {
                        if (parent.isActive) value(from(it))
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
                        if (parent.isActive) function(true)
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
                val value = ValueFunction(this, value, function)
                return CSRegistration(
                    first.onChange { if (parent.isActive) value(from(it, second.value)) },
                    second.onChange { if (parent.isActive) value(from(first.value, it)) },
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
                val value = ValueFunction(this, value, function)
                return CSRegistration(first.onChange {
                    if (parent.isActive) value(from(it, second.value, third.value))
                }, second.onChange {
                    if (parent.isActive) value(from(first.value, it, third.value))
                }, third.onChange {
                    if (parent.isActive) value(from(first.value, second.value, it))
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
                val value = ValueFunction(this, value, function)
                return CSRegistration(first.onChange {
                    if (parent.isActive) value(from(it, second.value, third.value, fourth.value))
                }, second.onChange {
                    if (parent.isActive) value(from(first.value, it, third.value, fourth.value))
                }, third.onChange {
                    if (parent.isActive) value(from(first.value, second.value, it, fourth.value))
                }, fourth.onChange {
                    if (parent.isActive) value(from(first.value, second.value, third.value, it))
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
                    val value = ValueFunction(this, value, function)
                    return property.onChange {
                        if (parent.isActive) value(from())
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
                    val value = ValueFunction(this, value, function)
                    var registration: CSRegistration? = null
                    var childRegistration: CSRegistration? = null
                    val parentRegistration = property.action {
                        childRegistration?.cancel()
                        val childItem = child()
                        if (parent.isActive && registration.isActive) childItem.also { value(it.value) }
                        childRegistration = childItem.onChange {
                            if (parent.isActive && registration.isActive) value(it)
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
                    val value = ValueFunction(this, value, function)
                    var registration: CSRegistration? = null
                    var childRegistration: CSRegistration? = null
                    val parentRegistration = property.action { parentValue ->
                        childRegistration?.cancel()
                        val childItem = child(parentValue)
                        if (parent.isActive && registration.isActive) childItem.also { value(it.value) }
                        childRegistration = childItem.onChange {
                            if (parent.isActive && registration.isActive) value(it)
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
                    val value = ValueFunction(this, value, function)
                    var registration: CSRegistration? = null
                    var childRegistration: CSRegistration? = null
                    val parentRegistration = properties.action { parentValue ->
                        childRegistration?.cancel()
                        val childItem = child(parentValue)
                        if (parent.isActive && registration.isActive)
                            childItem.also { value(it.value) }
                        childRegistration = childItem.onChange {
                            if (parent.isActive && registration.isActive) value(it)
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
                            if (parent.isActive && registration.isActive) function(it)
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
                            if (parent.isActive && registration.isActive) function(it)
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
                    val value = ValueFunction(this, value, function)
                    var registration: CSRegistration? = null
                    var childRegistration: CSRegistration? = null
                    val parentRegistration = property.action { parentValue ->
                        childRegistration?.cancel()
                        val childItem = child(parentValue)
                        if (parent.isActive && registration.isActive) childItem.also { value(it?.value) }
                        childRegistration = childItem?.onChange {
                            if (parent.isActive && registration.isActive) value.invoke(it)
                        }
                    }
                    return CSRegistration(isActive = true, onCancel = {
                        parentRegistration.cancel()
                        childRegistration?.cancel()
                    }).also { registration = it }.registerTo(parent)
                }
            }
        }

        private abstract class CSHasChangeValueBase<Return>(
            val parent: CSHasRegistrations?,
            val onChange: ArgFun<Return>? = null
        ) : CSHasChangeValue<Return> {
            abstract override var value: Return
            val eventChange by lazy { event<Return>() }
            override fun onChange(function: (Return) -> Unit) = eventChange.listen(function)

            @Synchronized
            open fun value(newValue: Return) {
                if (value == newValue) return
                value = newValue
                onValueChanged(newValue)
            }

            fun onValueChanged(newValue: Return) {
                onChange?.invoke(newValue)
                eventChange.fire(newValue)
            }

            operator fun plus(registration: CSRegistration): CSRegistration? =
                parent?.register(registration)
        }

        fun <T> CSHasChangeValue<T>.hasChangeValue(
            parent: CSHasRegistrations? = null, onChange: ArgFun<T>? = null,
        ): CSHasChangeValue<T> = hasChangeValue(parent, from = { it }, onChange)

        fun <Argument, Return> CSHasChangeValue<Argument>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument) -> Return,
            onChange: ArgFun<Return>? = null
        ): CSHasChangeValue<Return> = let { property ->
            object : CSHasChangeValueBase<Return>(parent, onChange) {
                override var value: Return = from(property.value)

                init {
                    this + property.onChange { value(from(it)) }
                }
            }
        }

        fun <Argument, Return> CSHasChangeValue<Argument>.safeHasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument) -> Return,
            onChange: ArgFun<Return>? = null
        ): CSHasChangeValue<Return> = let { property ->
            object : CSHasChangeValueBase<Return>(parent, onChange) {
                @Volatile
                override var value: Return = from(property.value)

                init {
                    this + property.onChange { value(from(it)) }
                }
            }
        }

        fun <Argument, Return> CSHasChangeValue<Argument>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            fromWithPrevious: (Argument, Return?) -> Return,
            onChange: ArgFun<Return>? = null
        ): CSHasChangeValue<Return> =
            let { property ->
                object : CSHasChangeValueBase<Return>(parent, onChange) {
                    var previous: Return? = null

                    override var value: Return = fromWithPrevious(property.value, previous)

                    init {
                        this + property.onChange {
                            value(fromWithPrevious(it, previous))
                            previous = value
                        }
                    }
                }
            }

        fun <Argument, Return : CSHasDestruct>
                CSHasChangeValue<Argument>.hasChangeValueDestruct(
            parent: CSHasRegistrations? = null,
            from: (Argument) -> Return,
            onChange: ArgFun<Return>? = null
        ): CSHasChangeValue<Return> = hasChangeValue(
            parent, fromWithPrevious = { type, previous ->
                previous?.destruct(); from(type)
            }, onChange)


        @JvmName("hasChangeValueChild")
        fun <ParentValue, Return> CSHasChangeValue<ParentValue>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            child: (ParentValue) -> CSHasChangeValue<Return>,
            onChange: ((Return) -> Unit)? = null
        ): CSHasChangeValue<Return> = let { property ->
            object : CSHasChangeValueBase<Return>(parent, onChange) {
                var isInitialized = false
                override var value: Return by notNull()

                init {
                    this + property.action(
                        child = { child(it) }, action = { value(it) })
                }

                @Synchronized
                override fun value(newValue: Return) {
                    if (isInitialized && value == newValue) return
                    value = newValue
                    isInitialized = true
                    onValueChanged(newValue)
                }
            }
        }

        fun <ParentValue, Return>
                CSHasChangeValue<ParentValue>.hasChangeValueNullable(
            parent: CSHasRegistrations? = null,
            child: (ParentValue) -> CSHasChangeValue<Return>?,
            onChange: ((Return?) -> Unit)? = null
        ): CSHasChangeValue<Return?> =
            let { property ->
                object : CSHasChangeValueBase<Return?>(parent, onChange) {
                    override var value: Return? = null

                    init {
                        this + property.action(
                            nullableChild = { child(it) },
                            action = ::value)
                    }
                }
            }

        fun <Argument1, Argument2, Return> hasChangeValue(
            parent: CSHasRegistrations? = null,
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            from: (Argument1, Argument2) -> Return,
            onChange: ArgFun<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSHasChangeValueBase<Return>(parent, onChange) {
                override var value: Return = from(item1.value, item2.value)

                init {
                    this + (item1 to item2).onChange { item1, item2 ->
                        value(from(item1, item2))
                    }
                }
            }

        fun <Argument1, Argument2, Return>
                Pair<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument1, Argument2) -> Return,
            onChange: ArgFun<Return>? = null
        ): CSHasChangeValue<Return> =
            hasChangeValue(parent, first, second, from, onChange)

        fun <Argument1, Argument2> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            onChange: (Argument1, Argument2) -> Unit,
        ): CSRegistration = listOf(item1, item2).onChange {
            onChange(item1.value, item2.value)
        }

        fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.onChange(
            onChange: (Argument1, Argument2) -> Unit,
        ): CSRegistration = onChange(first, second, onChange)

        fun <Argument1, Argument2> Triple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChange<*>>.onChange(
            onChange: (Argument1, Argument2) -> Unit,
        ): CSRegistration = listOf(first, second, third).onChange {
            onChange(first.value, second.value)
        }

        fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.onChange(
            onChange: () -> Unit,
        ): CSRegistration = onChange(first, second) { _, _ -> onChange() }

        fun <Argument1, Argument2> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            onAction: (Argument1, Argument2) -> Unit,
        ): CSRegistration = listOf(item1, item2).action {
            onAction(item1.value, item2.value)
        }

        fun <Argument1, Argument2> Pair<CSHasChangeValue<out Argument1>, CSHasChangeValue<out Argument2>>.action(
            onAction: (Argument1, Argument2) -> Unit,
        ): CSRegistration = action(first, second) { first, second -> onAction(first, second) }


        fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.action(
            onAction: () -> Unit,
        ): CSRegistration = action(first, second) { _, _ -> onAction() }


        fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.onChangeLaterOnce(
            onChange: () -> Unit,
        ): CSRegistration = listOf(first, second).onChangeLaterOnce { onChange() }

        fun <Argument1, Argument2> Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.onChangeLaterOnce(
            onChange: (Argument1, Argument2) -> Unit,
        ): CSRegistration =
            listOf(first, second).onChangeLaterOnce { onChange(first.value, second.value) }

        fun <Argument1, Argument2, Argument3, Return>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument1, Argument2, Argument3) -> Return,
            onChange: ArgFun<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSHasChangeValueBase<Return>(parent, onChange) {
                override var value: Return = from(first.value, second.value, third.value)

                init {
                    this + (first to second to third).onChange { item1, item2, item3 ->
                        value(from(item1, item2, item3))
                    }
                }
            }

        fun <Argument1, Argument2, Return>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChange<*>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument1, Argument2) -> Return,
            onChange: ArgFun<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSHasChangeValueBase<Return>(parent, onChange) {
                override var value: Return = from(first.value, second.value)

                init {
                    this + (first to second to third).onChange { item1, item2 ->
                        value(from(item1, item2))
                    }
                }
            }

        fun <Argument, Return> List<CSHasChangeValue<Argument>>.hasChangeValue(
            parent: CSHasRegistrations,
            from: (List<Argument>) -> Return
        ): CSHasChangeValue<Return> =
            lateProperty<Return>().also { property ->
                parent + action { list -> property assign from(list) }
            }

        fun <Argument1, Argument2, Argument3, Argument4, Return>
                CSQuadruple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>,
                        CSHasChangeValue<Argument4>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument1, Argument2, Argument3, Argument4) -> Return,
            onChange: ArgFun<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSHasChangeValueBase<Return>(parent, onChange) {
                override var value: Return =
                    from(first.value, second.value, third.value, fourth.value)

                init {
                    this + (first to second to third to fourth)
                        .onChange { item1, item2, item3, item4 ->
                            value(from(item1, item2, item3, item4))
                        }
                }
            }

        fun <Argument1, Argument2, Argument3, Argument4, Return>
                CSQuadruple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>,
                        CSHasChangeValue<Argument4>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (CSQuadruple<Argument1, Argument2, Argument3, Argument4>) -> Return,
            onChange: ArgFun<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSHasChangeValueBase<Return>(parent, onChange) {
                override var value: Return =
                    from(CSQuadruple(first.value, second.value, third.value, fourth.value))

                init {
                    this + (first to second to third to fourth)
                        .onChange { item1, item2, item3, item4 ->
                            value(from(CSQuadruple(item1, item2, item3, item4)))
                        }
                }
            }

        fun <Argument1, Argument2, Argument3, Argument4>
                CSQuadruple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>,
                        CSHasChangeValue<Argument4>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            onChange: ArgFun<CSQuadruple<Argument1, Argument2, Argument3, Argument4>>? = null
        ): CSHasChangeValue<CSQuadruple<Argument1, Argument2, Argument3, Argument4>> =
            hasChangeValue(parent, from = { item1, item2, item3, item4 ->
                CSQuadruple(item1, item2, item3, item4)
            }, onChange)

        fun <Argument1, Argument2, Argument3> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            onChange: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = listOf(item1, item2, item3).onChange {
            onChange(item1.value, item2.value, item3.value)
        }

        fun <Argument1, Argument2, Argument3>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.onChange(
            onChange: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = onChange(first, second, third, onChange)

        fun <Argument1, Argument2, Argument3>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.onChange(
            onChange: () -> Unit,
        ): CSRegistration = onChange(first, second, third) { _, _, _ -> onChange() }

        fun <Argument1, Argument2, Argument3>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.onChangeLaterOnce(
            onChange: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = listOf(first, second, third).onChangeLaterOnce {
            onChange(first.value, second.value, third.value)
        }

        fun <Argument1, Argument2, Argument3>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.onChangeLaterOnce(
            onChange: () -> Unit,
        ): CSRegistration = listOf(first, second, third).onChangeLaterOnce { onChange() }

        fun <Argument1, Argument2, Argument3> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            onAction: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = listOf(item1, item2, item3).action {
            onAction(item1.value, item2.value, item3.value)
        }

        fun <Argument1, Argument2, Argument3> Triple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>>.action(
            onAction: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = action(first, second, third, onAction)

        fun <Argument1, Argument2, Argument3> Triple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>>.actionLaterOnce(
            onAction: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = listOf(first, second, third).actionLaterOnce {
            onAction(first.value,
                second.value,
                third.value)
        }

        fun <Argument1, Argument2, Argument3> Triple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>>.actionLaterOnce(
            onAction: () -> Unit,
        ): CSRegistration = actionLaterOnce { _, _, _ -> onAction() }

        fun <Argument1, Argument2, Argument3, Argument4> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            onAction: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = listOf(item1, item2, item3, item4).onChange {
            onAction(item1.value, item2.value, item3.value, item4.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4> CSQuadruple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>>.onChange(
            onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = onChange(first, second, third, fourth, onChange)

        fun <Argument1, Argument2, Argument3, Argument4> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            onAction: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = listOf(item1, item2, item3, item4).action {
            onAction(item1.value, item2.value, item3.value, item4.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4> CSQuadruple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>>.action(
            onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = action(first, second, third, fourth, onChange)

        fun <Argument1, Argument2, Argument3, Argument4> CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.actionLaterOnce(
            isActionNow: Boolean = false,
            onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = listOf(first, second, third, fourth).actionLaterOnce(isActionNow) {
            onChange(first.value, second.value, third.value, fourth.value)
        }


        fun <Argument1, Argument2, Argument3, Argument4> CSQuadruple<CSHasChangeValue<Argument1>,
                CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>,
                CSHasChangeValue<Argument4>>.onChangeLaterOnce(
            onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = listOf(first, second, third, fourth).onChangeLaterOnce {
            onChange(first.value, second.value, third.value, fourth.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            item5: CSHasChangeValue<Argument5>,
            onAction: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = listOf(item1, item2, item3, item4, item5).onChange {
            onAction(item1.value, item2.value, item3.value, item4.value, item5.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5> CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>, CSHasChangeValue<Argument5>>.onChange(
            onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = onChange(first, second, third, fourth, fifth, onChange)

        fun <Argument1, Argument2, Argument3, Argument4, Argument5> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            item5: CSHasChangeValue<Argument5>,
            onAction: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = listOf(item1, item2, item3, item4, item5).action {
            onAction(item1.value, item2.value, item3.value, item4.value, item5.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5> CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>, CSHasChangeValue<Argument5>>.action(
            onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = action(first, second, third, fourth, fifth, onChange)

        fun <Argument1, Argument2, Argument3, Argument4, Argument5> CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>, CSHasChangeValue<Argument5>>.onChangeLaterOnce(
            onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = listOf(first, second, third, fourth, fifth).onChangeLaterOnce {
            onChange(first.value, second.value, third.value, fourth.value, fifth.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5> CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>, CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>, CSHasChangeValue<Argument5>>.actionLaterOnce(
            onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = listOf(first, second, third, fourth, fifth).actionLaterOnce {
            onChange(first.value, second.value, third.value, fourth.value, fifth.value)
        }


        fun <Argument1, Argument2, Argument3, Argument4, Argument5, Return>
                CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Return,
            onChange: ArgFun<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSHasChangeValueBase<Return>(parent, onChange) {
                override var value: Return =
                    from(first.value, second.value, third.value, fourth.value, fifth.value)

                init {
                    this + (first to second to third to fourth to fifth).onChange { item1, item2, item3, item4, item5 ->
                        value(from(item1, item2, item3, item4, item5))
                    }
                }
            }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5>
                CSQuintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            onChange: ArgFun<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>? = null
        ): CSHasChangeValue<CSQuintuple<Argument1, Argument2, Argument3, Argument4, Argument5>> =
            hasChangeValue(parent, from = { item1, item2, item3, item4, item5 ->
                CSQuintuple(item1, item2, item3, item4, item5)
            }, onChange)

        fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
                CSSixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>>.onChange(
            onChange: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Unit,
        ): CSRegistration = listOf(first, second, third, fourth, fifth, sixth).onChange {
            onChange(first.value, second.value, third.value, fourth.value, fifth.value, sixth.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
                CSSixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>
                        >.action(
            onChange: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Unit,
        ): CSRegistration = listOf(first, second, third, fourth, fifth, sixth).action {
            onChange(first.value, second.value, third.value, fourth.value, fifth.value, sixth.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
                CSSixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>
                        >.actionLaterOnce(
            dispatcher: CoroutineDispatcher = Main,
            onChange: suspend (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Unit
        ): CSRegistration {
            val registrations = CSRegistrationsMap(className)
            var value1: Argument1? = null
            var value2: Argument2? = null
            var value3: Argument3? = null
            var value4: Argument4? = null
            var value5: Argument5? = null
            var value6: Argument6? = null
            fun clearValues() {
                value1 = null; value2 = null; value3 = null
                value4 = null; value5 = null; value6 = null
            }

            val laterOnceFunction = registrations.debouncer(dispatcher) {
                if (registrations.isActive) {
                    onChange((value1 ?: first.value),
                        (value2 ?: second.value),
                        (value3 ?: third.value),
                        (value4 ?: fourth.value),
                        (value5 ?: fifth.value),
                        (value6 ?: sixth.value))
                    clearValues()
                }
            }
            registrations + first.onChange { value1 = it; laterOnceFunction() }
            registrations + second.onChange { value2 = it; laterOnceFunction.invoke() }
            registrations + third.onChange { value3 = it; laterOnceFunction.invoke() }
            registrations + fourth.onChange { value4 = it; laterOnceFunction.invoke() }
            registrations + fifth.onChange { value5 = it; laterOnceFunction.invoke() }
            registrations + sixth.onChange { value6 = it; laterOnceFunction.invoke() }
            laterOnceFunction()
            registrations.onCancel(::clearValues)
            return registrations
        }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Return>
                CSSixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Return,
            onChange: ArgFun<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSHasChangeValueBase<Return>(parent, onChange) {
                override var value: Return = from(first.value, second.value,
                    third.value, fourth.value, fifth.value, sixth.value)

                init {
                    this + (first to second to third to fourth to fifth to sixth)
                        .onChange { item1, item2, item3, item4, item5, item6 ->
                            value(from(item1, item2, item3, item4, item5, item6))
                        }
                }
            }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
                CSSixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            onChange: ArgFun<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>>? = null
        ): CSHasChangeValue<CSSixtuple<Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>> =
            hasChangeValue(parent, from = { item1, item2, item3, item4, item5, item6 ->
                CSSixtuple(item1, item2, item3, item4, item5, item6)
            }, onChange)

        fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Argument7>
                CSSeventuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>,
                        CSHasChangeValue<Argument7>>.action(
            onChange: (
                Argument1, Argument2, Argument3, Argument4,
                Argument5, Argument6, Argument7
            ) -> Unit,
        ): CSRegistration = listOf(first, second, third, fourth,
            fifth, sixth, seventh).action {
            onChange(first.value, second.value, third.value,
                fourth.value, fifth.value, sixth.value, seventh.value)
        }

        inline fun <T : CSHasChangeValue<Value>, Value> List<T>.onChange(
            crossinline function: ArgFun<List<Value>>
        ): CSRegistration = CSRegistrationsMap(className).also { registrations ->
            forEach { item: CSHasChangeValue<Value> ->
                registrations + item.onChange {
                    if (registrations.isActive) function(map { it.value })
                }
            }
        }

        inline fun <T : CSHasChangeValue<Value>, Value> List<T>.action(
            crossinline function: ArgFun<List<Value>>
        ): CSRegistration {
            function(map { it.value })
            return onChange(function)
        }
    }
}