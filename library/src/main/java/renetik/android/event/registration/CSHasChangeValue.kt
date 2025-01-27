package renetik.android.event.registration

import renetik.android.core.kotlin.collections.list
import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.Quadruple
import renetik.android.core.lang.Quintuple
import renetik.android.core.lang.Seventuple
import renetik.android.core.lang.Sixtuple
import renetik.android.core.lang.to
import renetik.android.core.lang.value.CSValue
import renetik.android.core.lang.variable.assign
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.CSLaterOnceFunc.Companion.laterOnceFunc
import renetik.android.event.common.destruct
import renetik.android.event.property.CSProperty.Companion.lateProperty
import renetik.android.event.property.CSPropertyBase
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import kotlin.properties.Delegates.notNull

interface CSHasChangeValue<T> : CSValue<T>, CSHasChange<T> {
    companion object {
        fun <T> emptyNullable() = object : CSHasChangeValue<T?> {
            override val value: T? = null
            override fun onChange(function: (T?) -> Unit) = CSRegistration.Empty
        }

        fun <T> empty(value: T) = object : CSHasChangeValue<T> {
            override val value: T = value
            override fun onChange(function: (T) -> Unit) = CSRegistration.Empty
        }

        class DelegateValue<Return>(
            var value: Return, val onChange: ArgFunc<Return>? = null,
            val function: (Return) -> Unit
        ) {
            operator fun invoke(newValue: Return) {
                if (value != newValue) {
                    value = newValue
                    onChange?.invoke(newValue)
                    function(newValue)
                }
            }
        }

        fun <T> CSHasChangeValue<T>.delegate(
            parent: CSHasRegistrations? = null,
            onChange: ArgFunc<T>? = null,
        ): CSHasChangeValue<T> = delegate(parent, from = { it }, onChange)

        fun <T, Return> CSHasChangeValue<T>.delegate(
            parent: CSHasRegistrations? = null,
            from: (T) -> Return,
            onChange: ArgFunc<Return>? = null,
        ): CSHasChangeValue<Return> = let { property ->
            object : CSHasChangeValue<Return> {
                override val value: Return get() = from(property.value)
                override fun onChange(function: (Return) -> Unit): CSRegistration {
                    val value = DelegateValue(value, onChange, function)
                    return property.onChange { value(from(it)) }.registerTo(parent)
                }
            }
        }

        fun <Argument, Return> List<CSHasChangeValue<Argument>>.delegate(
            parent: CSHasRegistrations? = null,
            from: (List<Argument>) -> Return,
            onChange: ArgFunc<Return>? = null,
        ): CSHasChangeValue<Return> = let { property ->
            object : CSHasChangeValue<Return> {
                override val value: Return get() = from(property.map { it.value })
                override fun onChange(function: (Return) -> Unit): CSRegistration {
                    val value = DelegateValue(value, onChange, function)
                    return property.onChange { value(from(it)) }.registerTo(parent)
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
                        function(true)
                        value = false
                    }.registerTo(parent)
                }
            }
        }

        fun <T, V, Return> Pair<CSHasChangeValue<T>,
                CSHasChangeValue<V>>.delegate(
            parent: CSHasRegistrations? = null,
            from: (T, V) -> Return,
            onChange: ArgFunc<Return>? = null,
        ): CSHasChangeValue<Return> = object : CSHasChangeValue<Return> {
            override val value: Return get() = from(first.value, second.value)
            override fun onChange(function: (Return) -> Unit): CSRegistration {
                val value = DelegateValue(value, onChange, function)
                return CSRegistration(
                    first.onChange { value(from(it, second.value)) },
                    second.onChange { value(from(first.value, it)) },
                ).registerTo(parent)
            }
        }

        fun <T, V, K, Return>
                Triple<CSHasChangeValue<T>,
                        CSHasChangeValue<V>,
                        CSHasChangeValue<K>>.delegate(
            parent: CSHasRegistrations? = null,
            from: (T, V, K) -> Return,
            onChange: ArgFunc<Return>? = null,
        ): CSHasChangeValue<Return> = object : CSHasChangeValue<Return> {
            override val value: Return
                get() = from(first.value, second.value, third.value)

            override fun onChange(function: (Return) -> Unit): CSRegistration {
                val value = DelegateValue(value, onChange, function)
                return CSRegistration(
                    first.onChange { value(from(it, second.value, third.value)) },
                    second.onChange { value(from(first.value, it, third.value)) },
                    third.onChange { value(from(first.value, second.value, it)) }
                ).registerTo(parent)
            }
        }

        fun <T, V, K, L, Return>
                Quadruple<CSHasChangeValue<T>,
                        CSHasChangeValue<V>,
                        CSHasChangeValue<K>,
                        CSHasChangeValue<L>>.delegate(
            parent: CSHasRegistrations? = null,
            from: (T, V, K, L) -> Return,
            onChange: ArgFunc<Return>? = null,
        ): CSHasChangeValue<Return> = object : CSHasChangeValue<Return> {
            override val value: Return
                get() = from(first.value, second.value, third.value, fourth.value)

            override fun onChange(function: (Return) -> Unit): CSRegistration {
                val value = DelegateValue(value, onChange, function)
                return CSRegistration(
                    first.onChange { value(from(it, second.value, third.value, fourth.value)) },
                    second.onChange { value(from(first.value, it, third.value, fourth.value)) },
                    third.onChange { value(from(first.value, second.value, it, fourth.value)) },
                    fourth.onChange { value(from(first.value, second.value, third.value, it)) }
                ).registerTo(parent)
            }
        }

        @JvmName("delegateChild")
        fun <ParentValue, ChildValue>
                CSHasChangeValue<ParentValue>.delegate(
            parent: CSHasRegistrations? = null,
            child: (ParentValue) -> CSHasChangeValue<ChildValue>,
            onChange: ((ChildValue?) -> Unit)? = null
        ): CSHasChangeValue<ChildValue> = let { property ->
            object : CSHasChangeValue<ChildValue> {
                override val value: ChildValue get() = child(property.value).value
                override fun onChange(function: (ChildValue) -> Unit): CSRegistration {
                    val value = DelegateValue(value, onChange, function)
                    var registration: CSRegistration? = null
                    var childRegistration: CSRegistration? = null
                    val parentRegistration = property.action { parentValue ->
                        childRegistration?.cancel()
                        val childItem = child(parentValue)
                        if (registration.isActive) childItem.also { value(it.value) }
                        childRegistration = childItem.onChange {
                            if (registration.isActive) value(it)
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
        fun <ParentValue, ChildValue>
                CSHasChangeValue<ParentValue>.delegateChange(
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
                            if (registration.isActive) function(it)
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
        fun <ParentValue, ChildValue>
                CSHasChangeValue<ParentValue>.delegateNullable(
            parent: CSHasRegistrations? = null,
            child: (ParentValue) -> CSHasChangeValue<ChildValue>?,
            onChange: ((ChildValue?) -> Unit)? = null
        ): CSHasChangeValue<ChildValue?> = let { property ->
            object : CSHasChangeValue<ChildValue?> {
                override val value: ChildValue? get() = child(property.value)?.value
                override fun onChange(function: (ChildValue?) -> Unit): CSRegistration {
                    val value = DelegateValue(value, onChange, function)
                    var registration: CSRegistration? = null
                    var childRegistration: CSRegistration? = null
                    val parentRegistration = property.action { parentValue ->
                        childRegistration?.cancel()
                        val childItem = child(parentValue)
                        if (registration.isActive) childItem.also { value(it?.value) }
                        childRegistration = childItem?.onChange {
                            if (registration.isActive) value.invoke(it)
                        }
                    }
                    return CSRegistration(isActive = true, onCancel = {
                        parentRegistration.cancel()
                        childRegistration?.cancel()
                    }).also { registration = it }.registerTo(parent)
                }
            }
        }

        fun <T> CSHasChangeValue<T>.hasChangeValue(
            parent: CSHasDestruct? = null, onChange: ArgFunc<T>? = null,
        ): CSHasChangeValue<T> = hasChangeValue(parent, from = { it }, onChange)

        fun <Argument, Return>
                CSHasChangeValue<Argument>.hasChangeValue(
            parent: CSHasDestruct? = null,
            from: (Argument) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> = let { property ->
            object : CSPropertyBase<Return>(parent, onChange) {
                override var value: Return = from(property.value)

                init {
                    this + property.onChange { value(from(it)) }
                }
            }
        }

        fun <Argument, Return>
                CSHasChangeValue<Argument>.hasChangeValue(
            parent: CSHasDestruct? = null,
            fromWithPrevious: (Argument, Return?) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> = let { property ->
            object : CSPropertyBase<Return>(parent, onChange) {
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
            parent: CSHasDestruct? = null,
            from: (Argument) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> = hasChangeValue(
            parent, fromWithPrevious = { type, previous ->
                previous?.destruct(); from(type)
            }, onChange
        )

        @JvmName("hasChangeValueChild")
        fun <ParentValue, Return : Any>
                CSHasChangeValue<ParentValue>.hasChangeValue(
            parent: CSHasDestruct? = null,
            child: (ParentValue) -> CSHasChangeValue<Return>,
            onChange: ((Return) -> Unit)? = null
        ): CSHasChangeValue<Return> = let { property ->
            object : CSPropertyBase<Return>(parent, onChange) {
                var isInitialized = false
                override var value: Return by notNull()

                init {
                    this + property.action(
                        child = { child(it) },
                        action = { value(it) }
                    )
                }

                override fun value(newValue: Return, fire: Boolean) {
                    if (isInitialized && value == newValue) return
                    value = newValue
                    isInitialized = true
                    onValueChanged(newValue, fire)
                }
            }
        }

        fun <ParentValue, Return>
                CSHasChangeValue<ParentValue>.hasChangeValueNullable(
            parent: CSHasDestruct? = null,
            child: (ParentValue) -> CSHasChangeValue<Return>?,
            onChange: ((Return?) -> Unit)? = null
        ): CSHasChangeValue<Return?> = let { property ->
            object : CSPropertyBase<Return?>(parent, onChange) {
                override var value: Return? = null

                init {
                    this + property.action(
                        nullableChild = { child(it) },
                        onChange = { value(it) }
                    )
                }
            }
        }

        fun <Argument1, Argument2, Return> hasChangeValue(
            parent: CSHasDestruct? = null,
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            from: (Argument1, Argument2) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> = object : CSPropertyBase<Return>(parent, onChange) {
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
            parent: CSHasDestruct? = null,
            from: (Argument1, Argument2) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> =
            hasChangeValue(parent, first, second, from, onChange)

        fun <Argument1, Argument2> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            onChange: (Argument1, Argument2) -> Unit,
        ): CSRegistration = list(item1, item2).onChange {
            onChange(item1.value, item2.value)
        }

        fun <Argument1, Argument2>
                Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.onChange(
            onChange: (Argument1, Argument2) -> Unit,
        ): CSRegistration = onChange(first, second, onChange)

        fun <Argument1, Argument2>
                Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.onChange(
            onChange: () -> Unit,
        ): CSRegistration = onChange(first, second) { _, _ -> onChange() }

        fun <Argument1, Argument2> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            onAction: (Argument1, Argument2) -> Unit,
        ): CSRegistration = list(item1, item2).action {
            onAction(item1.value, item2.value)
        }

        fun <Argument1, Argument2>
                Pair<CSHasChangeValue<out Argument1>, CSHasChangeValue<out Argument2>>.action(
            onAction: (Argument1, Argument2) -> Unit,
        ): CSRegistration =
            action(first, second) { first, second -> onAction(first, second) }


        fun <Argument1, Argument2>
                Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.action(
            onAction: () -> Unit,
        ): CSRegistration = action(first, second) { _, _ -> onAction() }


        fun <Argument1, Argument2>
                Pair<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>>.onChangeLaterOnce(
            onChange: () -> Unit,
        ): CSRegistration = list(first, second).onChangeLaterOnce { onChange() }

        fun <Argument1, Argument2, Argument3, Return>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.hasChangeValue(
            parent: CSHasDestruct? = null,
            from: (Argument1, Argument2, Argument3) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSPropertyBase<Return>(parent, onChange) {
                override var value: Return = from(first.value, second.value, third.value)

                init {
                    this + (first to second to third)
                        .onChange { item1, item2, item3 ->
                            value(from(item1, item2, item3))
                        }
                }
            }

        fun <Argument, Return> List<CSHasChangeValue<Argument>>.hasChangeValue(
            parent: CSHasRegistrations, from: (List<Argument>) -> Return
        ): CSHasChangeValue<Return> = lateProperty<Return>().also { property ->
            parent + action { list -> property assign from(list) }
        }

        fun <Argument1, Argument2, Argument3, Argument4, Return>
                Quadruple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>,
                        CSHasChangeValue<Argument4>>.hasChangeValue(
            parent: CSHasDestruct? = null,
            from: (Argument1, Argument2, Argument3, Argument4) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSPropertyBase<Return>(parent, onChange) {
                override var value: Return = from(
                    first.value, second.value, third.value, fourth.value
                )

                init {
                    this + (first to second to third to fourth)
                        .onChange { item1, item2, item3, item4 ->
                            value(from(item1, item2, item3, item4))
                        }
                }
            }

        fun <Argument1, Argument2, Argument3, Argument4, Return>
                Quadruple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>,
                        CSHasChangeValue<Argument4>>.hasChangeValue(
            parent: CSHasDestruct? = null,
            from: (Quadruple<Argument1, Argument2, Argument3, Argument4>) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSPropertyBase<Return>(parent, onChange) {
                override var value: Return = from(
                    Quadruple(first.value, second.value, third.value, fourth.value)
                )

                init {
                    this + (first to second to third to fourth)
                        .onChange { item1, item2, item3, item4 ->
                            value(from(Quadruple(item1, item2, item3, item4)))
                        }
                }
            }

        fun <Argument1, Argument2, Argument3, Argument4>
                Quadruple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>,
                        CSHasChangeValue<Argument4>>.hasChangeValue(
            parent: CSHasDestruct? = null,
            onChange: ArgFunc<Quadruple<Argument1, Argument2, Argument3, Argument4>>? = null
        ): CSHasChangeValue<Quadruple<Argument1, Argument2, Argument3, Argument4>> =
            hasChangeValue(parent, from = { item1, item2, item3, item4 ->
                Quadruple(item1, item2, item3, item4)
            }, onChange)

        fun <Argument1, Argument2, Argument3> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            onChange: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = list(item1, item2, item3).onChange {
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
        ): CSRegistration = list(first, second, third)
            .onChangeLaterOnce { onChange(first.value, second.value, third.value) }

        fun <Argument1, Argument2, Argument3>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.onChangeLaterOnce(
            onChange: () -> Unit,
        ): CSRegistration = list(first, second, third)
            .onChangeLaterOnce { onChange() }

        fun <Argument1, Argument2, Argument3> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            onAction: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = list(item1, item2, item3).action {
            onAction(item1.value, item2.value, item3.value)
        }

        fun <Argument1, Argument2, Argument3>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.action(
            onAction: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = action(first, second, third, onAction)

        fun <Argument1, Argument2, Argument3>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.actionLaterOnce(
            onAction: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = list(first, second, third)
            .actionLaterOnce { onAction(first.value, second.value, third.value) }

        fun <Argument1, Argument2, Argument3>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.actionLaterOnce(
            onAction: () -> Unit,
        ): CSRegistration = actionLaterOnce { _, _, _ -> onAction() }

        fun <Argument1, Argument2, Argument3, Argument4> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            onAction: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = list(item1, item2, item3, item4).onChange {
            onAction(item1.value, item2.value, item3.value, item4.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4>
                Quadruple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>>.onChange(
            onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = onChange(first, second, third, fourth, onChange)

        fun <Argument1, Argument2, Argument3, Argument4> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            onAction: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = list(item1, item2, item3, item4).action {
            onAction(item1.value, item2.value, item3.value, item4.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4>
                Quadruple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>>.action(
            onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = action(first, second, third, fourth, onChange)

        fun <Argument1, Argument2, Argument3, Argument4>
                Quadruple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>
                        >.actionLaterOnce(
            onChange: (
                Argument1, Argument2, Argument3, Argument4
            ) -> Unit,
        ): CSRegistration = list(first, second, third, fourth)
            .actionLaterOnce {
                onChange(
                    first.value, second.value, third.value, fourth.value
                )
            }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            item5: CSHasChangeValue<Argument5>,
            onAction: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = list(item1, item2, item3, item4, item5).onChange {
            onAction(item1.value, item2.value, item3.value, item4.value, item5.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5>
                Quintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>>.onChange(
            onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = onChange(first, second, third, fourth, fifth, onChange)

        fun <Argument1, Argument2, Argument3, Argument4, Argument5> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            item5: CSHasChangeValue<Argument5>,
            onAction: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = list(item1, item2, item3, item4, item5).action {
            onAction(item1.value, item2.value, item3.value, item4.value, item5.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5>
                Quintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>>.action(
            onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = action(first, second, third, fourth, fifth, onChange)

        fun <Argument1, Argument2, Argument3, Argument4, Argument5>
                Quintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>>.onChangeLaterOnce(
            onChange: (
                Argument1, Argument2, Argument3, Argument4, Argument5
            ) -> Unit,
        ): CSRegistration = list(first, second, third, fourth, fifth)
            .onChangeLaterOnce {
                onChange(
                    first.value, second.value, third.value, fourth.value, fifth.value
                )
            }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5>
                Quintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>>.actionLaterOnce(
            onChange: (
                Argument1, Argument2, Argument3, Argument4, Argument5
            ) -> Unit,
        ): CSRegistration = list(first, second, third, fourth, fifth)
            .actionLaterOnce {
                onChange(
                    first.value, second.value, third.value, fourth.value, fifth.value
                )
            }


        fun <Argument1, Argument2, Argument3, Argument4, Argument5, Return>
                Quintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>>.hasChangeValue(
            parent: CSHasDestruct? = null,
            from: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSPropertyBase<Return>(parent, onChange) {
                override var value: Return = from(
                    first.value, second.value, third.value, fourth.value, fifth.value
                )

                init {
                    this + (first to second to third to fourth to fifth)
                        .onChange { item1, item2, item3, item4, item5 ->
                            value(from(item1, item2, item3, item4, item5))
                        }
                }
            }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5>
                Quintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>>.hasChangeValue(
            parent: CSHasDestruct? = null,
            onChange: ArgFunc<Quintuple<Argument1, Argument2, Argument3, Argument4, Argument5>>? = null
        ): CSHasChangeValue<Quintuple<Argument1, Argument2, Argument3, Argument4, Argument5>> =
            hasChangeValue(parent, from = { item1, item2, item3, item4, item5 ->
                Quintuple(item1, item2, item3, item4, item5)
            }, onChange)

        fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
                Sixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>>.onChange(
            onChange: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Unit,
        ): CSRegistration = list(first, second, third, fourth, fifth, sixth).onChange {
            onChange(
                first.value, second.value, third.value, fourth.value, fifth.value,
                sixth.value
            )
        }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
                Sixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>>.action(
            onChange: (
                Argument1, Argument2, Argument3, Argument4, Argument5, Argument6
            ) -> Unit,
        ): CSRegistration = list(first, second, third, fourth, fifth, sixth).action {
            onChange(first.value, second.value, third.value,
                fourth.value, fifth.value, sixth.value)
        }

        fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
                Sixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>
                        >.actionLaterOnce(
            onChange: (Argument1, Argument2, Argument3,
                       Argument4, Argument5, Argument6) -> Unit): CSRegistration {
            val registrations = CSRegistrationsMap(this)
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

            val laterOnceFunction = registrations.laterOnceFunc {
                if (registrations.isActive) {
                    onChange(
                        (value1 ?: first.value), (value2 ?: second.value), (value3 ?: third.value),
                        (value4 ?: fourth.value), (value5 ?: fifth.value), (value6 ?: sixth.value)
                    )
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

        fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Argument7>
                Seventuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>,
                        CSHasChangeValue<Argument7>>.action(
            onChange: (
                Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Argument7
            ) -> Unit,
        ): CSRegistration =
            list(first, second, third, fourth, fifth, sixth, seventh).action {
                onChange(
                    first.value, second.value, third.value, fourth.value,
                    fifth.value, sixth.value, seventh.value
                )
            }

        inline fun <T : CSHasChangeValue<Value>, Value> List<T>.onChange(
            crossinline function: ArgFunc<List<Value>>
        ): CSRegistration {
            val registrations = CSRegistrationsMap(this)
            forEach { item ->
                registrations.register(item.onChange {
                    if (registrations.isActive) function(map { it.value })
                })
            }
            return registrations
        }

        inline fun <T : CSHasChangeValue<Value>, Value> List<T>.action(
            crossinline function: ArgFunc<List<Value>>
        ): CSRegistration {
            function(map { it.value })
            return onChange(function)
        }
    }
}