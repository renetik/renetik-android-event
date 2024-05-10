package renetik.android.event.registration

import renetik.android.core.kotlin.collections.list
import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.Quadruple
import renetik.android.core.lang.Quintuple
import renetik.android.core.lang.Seventuple
import renetik.android.core.lang.Sixtuple
import renetik.android.core.lang.to
import renetik.android.core.lang.value.CSValue
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.property.CSPropertyBase
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import kotlin.properties.Delegates.notNull

interface CSHasChangeValue<T> : CSValue<T>, CSHasChange<T> {
    companion object {
        inline fun <T> CSHasChangeValue<T>.delegate(
            parent: CSHasRegistrations? = null,
            noinline onChange: ArgFunc<T>? = null,
        ): CSHasChangeValue<T> = delegate(parent, from = { it }, onChange)

        inline fun <T, Return> CSHasChangeValue<T>.delegate(
            parent: CSHasRegistrations? = null,
            crossinline from: (T) -> Return,
            noinline onChange: ArgFunc<Return>? = null,
        ): CSHasChangeValue<Return> = let { property ->
            object : CSHasChangeValue<Return> {
                override val value: Return get() = from(property.value)
                override fun onChange(function: (Return) -> Unit) =
                    property.onChange {
                        val value = from(it)
                        onChange?.invoke(value)
                        function(value)
                    }.also { parent?.register(it) }
            }
        }

        inline fun <T, V, Return> Pair<CSHasChangeValue<T>, CSHasChangeValue<V>>.delegate(
            parent: CSHasRegistrations? = null,
            crossinline from: (T, V) -> Return,
            noinline onChange: ArgFunc<Return>? = null,
        ): CSHasChangeValue<Return> = object : CSHasChangeValue<Return> {
            override val value: Return get() = from(first.value, second.value)
            override fun onChange(function: (Return) -> Unit) = CSRegistration(
                first.onChange {
                    val value = from(it, second.value)
                    onChange?.invoke(value)
                    function(value)
                }.also { parent?.register(it) },
                second.onChange {
                    val value = from(first.value, it)
                    onChange?.invoke(value)
                    function(value)
                }.also { parent?.register(it) }
            )
        }

        inline fun <T, V, K, Return> Triple<CSHasChangeValue<T>, CSHasChangeValue<V>, CSHasChangeValue<K>>.delegate(
            parent: CSHasRegistrations? = null,
            crossinline from: (T, V, K) -> Return,
            noinline onChange: ArgFunc<Return>? = null,
        ): CSHasChangeValue<Return> = object : CSHasChangeValue<Return> {
            override val value: Return get() = from(first.value, second.value, third.value)
            override fun onChange(function: (Return) -> Unit) = CSRegistration(
                first.onChange {
                    val value = from(it, second.value, third.value)
                    onChange?.invoke(value)
                    function(value)
                }.also { parent?.register(it) },
                second.onChange {
                    val value = from(first.value, it, third.value)
                    onChange?.invoke(value)
                    function(value)
                }.also { parent?.register(it) },
                third.onChange {
                    val value = from(first.value, second.value, it)
                    onChange?.invoke(value)
                    function(value)
                }.also { parent?.register(it) }
            )
        }

        @JvmName("delegateChild")
        inline fun <ParentValue, ChildValue>
                CSHasChangeValue<ParentValue>.delegate(
            parent: CSHasRegistrations? = null,
            crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>,
            noinline onChange: ((ChildValue?) -> Unit)? = null
        ): CSHasChangeValue<ChildValue> = let { property ->
            object : CSHasChangeValue<ChildValue> {
                override val value: ChildValue get() = child(property.value).value
                override fun onChange(function: (ChildValue) -> Unit): CSRegistration {
                    var childRegistration: CSRegistration? = null
                    var isPaused = false
                    val parentRegistration = property.action { parentValue ->
                        childRegistration?.cancel()
                        val childItem = child(parentValue)
                        if (childRegistration != null) childItem.also {
                            if (!isPaused) {
                                onChange?.invoke(it.value)
                                function(it.value)
                            }
                        }
                        childRegistration = childItem.onChange { childValue ->
                            if (!isPaused) {
                                onChange?.invoke(childValue)
                                function(childValue)
                            }
                        }
                    }
                    return CSRegistration(isActive = true,
                        onPause = { isPaused = true }, onResume = { isPaused = false },
                        onCancel = { parentRegistration.cancel(); childRegistration?.cancel() })
                        .also { parent?.register(it) }
                }
            }
        }

        @JvmName("delegateNullable")
        inline fun <ParentValue, ChildValue>
                CSHasChangeValue<ParentValue>.delegateNullable(
            parent: CSHasRegistrations? = null,
            crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>?,
            noinline onChange: ((ChildValue?) -> Unit)? = null
        ): CSHasChangeValue<ChildValue?> = let { property ->
            object : CSHasChangeValue<ChildValue?> {
                override val value: ChildValue? get() = child(property.value)?.value
                override fun onChange(function: (ChildValue?) -> Unit): CSRegistration {
                    var childRegistration: CSRegistration? = null
                    var isInitialized = false
                    var isPaused = false
                    val parentRegistration = property.action { parentValue ->
                        childRegistration?.cancel()
                        val childItem = child(parentValue)
                        if (isInitialized) childItem.also {
                            if (!isPaused) {
                                onChange?.invoke(it?.value)
                                function(it?.value)
                            }
                        }
                        isInitialized = true
                        childRegistration = childItem?.onChange { childValue ->
                            if (!isPaused) {
                                onChange?.invoke(childValue)
                                function(childValue)
                            }
                        }
                    }
                    return CSRegistration(isActive = true,
                        onPause = { isPaused = true }, onResume = { isPaused = false },
                        onCancel = { parentRegistration.cancel(); childRegistration?.cancel() })
                        .also { parent?.register(it) }
                }
            }
        }

        //TODO: This and "computed" is same in essence just different implementation we need to choose one and use that.
        // but this is probably more optimized.
        fun <Argument, Return>
                CSHasChangeValue<Argument>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> = let { property ->
            object : CSPropertyBase<Return>(onChange) {
                override var value: Return = from(property.value)

                init {
                    property.onChange { item1 ->
                        value(from(item1))
                    }.also { parent?.register(it) }
                }

                override fun value(newValue: Return, fire: Boolean) {
                    if (value == newValue) return
                    value = newValue
                    onValueChanged(newValue, fire)
                }
            }
        }

        fun <Argument, Return>
                CSHasChangeValue<Argument>.hasChangeValueWithPrevious(
            parent: CSHasRegistrations? = null,
            from: (Return?, Argument) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> = let { property ->
            object : CSPropertyBase<Return>(onChange) {
                override var value: Return = from(null, property.value)

                init {
                    property.onChange { item1 -> value(from(value, item1)) }
                        .also { parent?.register(it) }
                }

                override fun value(newValue: Return, fire: Boolean) {
                    if (value == newValue) return
                    value = newValue
                    onValueChanged(newValue, fire)
                }
            }
        }

        @JvmName("hasChangeValueChild")
        inline fun <ParentValue, ChildValue : Any>
                CSHasChangeValue<ParentValue>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>,
            noinline onChange: ((ChildValue) -> Unit)? = null
        ): CSHasChangeValue<ChildValue> {
            val event = event<ChildValue>()
            var value: ChildValue by notNull()
            action(
                child = { child(it) },
                action = {
                    value = it
                    onChange?.invoke(value)
                    event.fire(it)
                }
            ).also { parent?.register(it) }
            return object : CSHasChangeValue<ChildValue> {
                override val value: ChildValue get() = value
                override fun onChange(function: (ChildValue) -> Unit) =
                    event.listen(function)
            }
        }

        inline fun <ParentValue, ChildValue>
                CSHasChangeValue<ParentValue>.hasChangeValueNullable(
            parent: CSHasRegistrations? = null,
            crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>?,
            noinline onChange: ((ChildValue?) -> Unit)? = null
        ): CSHasChangeValue<ChildValue?> {
            val event = event<ChildValue?>()
            var value: ChildValue? = null
            action(
                optionalChild = { child(it) },
                onChange = {
                    value = it
                    onChange?.invoke(value)
                    event.fire(it)
                }
            ).also { parent?.register(it) }
            return object : CSHasChangeValue<ChildValue?> {
                override val value: ChildValue? get() = value
                override fun onChange(function: (ChildValue?) -> Unit) =
                    event.listen(function)
            }
        }

        inline fun <Argument1, Argument2, Return> hasChangeValue(
            parent: CSHasRegistrations? = null,
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            crossinline from: (Argument1, Argument2) -> Return,
            noinline onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> = object : CSPropertyBase<Return>(onChange) {
            override var value: Return = from(item1.value, item2.value)

            init {
                (item1 to item2).onChange { item1, item2 ->
                    value(from(item1, item2))
                }.also { parent?.register(it) }
            }

            override fun value(newValue: Return, fire: Boolean) {
                if (value == newValue) return
                value = newValue
                onValueChanged(newValue, fire)
            }
        }

        fun <Argument1, Argument2, Return>
                Pair<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
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

        inline fun <Argument1, Argument2> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            crossinline onAction: (Argument1, Argument2) -> Unit,
        ): CSRegistration = list(item1, item2).action {
            onAction(item1.value, item2.value)
        }

        inline fun <Argument1, Argument2>
                Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.action(
            crossinline onAction: (Pair<Argument1, Argument2>) -> Unit,
        ): CSRegistration = action(first, second) { first, second -> onAction(first to second) }


        fun <Argument1, Argument2, Argument3, Return>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument1, Argument2, Argument3) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSPropertyBase<Return>(onChange) {
                override var value: Return = from(first.value, second.value, third.value)

                init {
                    (first to second to third).onChange { item1, item2, item3 ->
                        value(from(item1, item2, item3))
                    }.also { parent?.register(it) }
                }

                override fun value(newValue: Return, fire: Boolean) {
                    if (value == newValue) return
                    value = newValue
                    onValueChanged(newValue, fire)
                }
            }

        fun <Argument1, Argument2, Argument3, Argument4, Return>
                Quadruple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>,
                        CSHasChangeValue<Argument4>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument1, Argument2, Argument3, Argument4) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> =
            object : CSPropertyBase<Return>(onChange) {
                override var value: Return = from(
                    first.value, second.value, third.value, fourth.value
                )

                init {
                    (first to second to third to fourth).onChange { item1, item2, item3, item4 ->
                        value(from(item1, item2, item3, item4))
                    }.also { parent?.register(it) }
                }

                override fun value(newValue: Return, fire: Boolean) {
                    if (value == newValue) return
                    value = newValue
                    onValueChanged(newValue, fire)
                }
            }

        inline fun <Argument1, Argument2, Argument3> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            crossinline onChange: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = list(item1, item2, item3).onChange {
            onChange(item1.value, item2.value, item3.value)
        }

        inline fun <Argument1, Argument2, Argument3>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.onChange(
            crossinline onChange: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = onChange(first, second, third, onChange)

        inline fun <Argument1, Argument2, Argument3>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.onChange(
            crossinline onChange: () -> Unit,
        ): CSRegistration = onChange(first, second, third) { _, _, _ -> onChange() }

        inline fun <Argument1, Argument2, Argument3> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            crossinline onAction: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = list(item1, item2, item3).action {
            onAction(item1.value, item2.value, item3.value)
        }

        inline fun <Argument1, Argument2, Argument3>
                Triple<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>>.action(
            crossinline onAction: (Argument1, Argument2, Argument3) -> Unit,
        ): CSRegistration = action(first, second, third, onAction)

        inline fun <Argument1, Argument2, Argument3, Argument4> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            crossinline onAction: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = list(item1, item2, item3, item4).onChange {
            onAction(item1.value, item2.value, item3.value, item4.value)
        }

        inline fun <Argument1, Argument2, Argument3, Argument4>
                Quadruple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>>.onChange(
            crossinline onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = onChange(first, second, third, fourth, onChange)

        inline fun <Argument1, Argument2, Argument3, Argument4> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            crossinline onAction: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = list(item1, item2, item3, item4).action {
            onAction(item1.value, item2.value, item3.value, item4.value)
        }

        inline fun <Argument1, Argument2, Argument3, Argument4>
                Quadruple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>>.action(
            crossinline onChange: (Argument1, Argument2, Argument3, Argument4) -> Unit,
        ): CSRegistration = action(first, second, third, fourth, onChange)


        inline fun <Argument1, Argument2, Argument3, Argument4, Argument5> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            item5: CSHasChangeValue<Argument5>,
            crossinline onAction: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = list(item1, item2, item3, item4, item5).onChange {
            onAction(item1.value, item2.value, item3.value, item4.value, item5.value)
        }

        inline fun <Argument1, Argument2, Argument3, Argument4, Argument5>
                Quintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>>.onChange(
            crossinline onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = onChange(first, second, third, fourth, fifth, onChange)

        inline fun <Argument1, Argument2, Argument3, Argument4, Argument5> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            item5: CSHasChangeValue<Argument5>,
            crossinline onAction: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = list(item1, item2, item3, item4, item5).action {
            onAction(item1.value, item2.value, item3.value, item4.value, item5.value)
        }

        inline fun <Argument1, Argument2, Argument3, Argument4, Argument5>
                Quintuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>>.action(
            crossinline onChange: (Argument1, Argument2, Argument3, Argument4, Argument5) -> Unit,
        ): CSRegistration = action(first, second, third, fourth, fifth, onChange)

        inline fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
                Sixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>>.onChange(
            crossinline onChange: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Unit,
        ): CSRegistration = list(first, second, third, fourth, fifth, sixth).onChange {
            onChange(first.value, second.value, third.value, fourth.value, fifth.value, sixth.value)
        }

        inline fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
                Sixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>>.action(
            crossinline onChange: (
                Argument1, Argument2, Argument3, Argument4, Argument5, Argument6
            ) -> Unit,
        ): CSRegistration = list(first, second, third, fourth, fifth, sixth).action {
            onChange(first.value, second.value, third.value, fourth.value, fifth.value, sixth.value)
        }

        inline fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Argument7>
                Seventuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>,
                        CSHasChangeValue<Argument7>>.action(
            crossinline onChange: (
                Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Argument7
            ) -> Unit,
        ): CSRegistration = list(first, second, third, fourth, fifth, sixth, seventh).action {
            onChange(
                first.value, second.value, third.value, fourth.value,
                fifth.value, sixth.value, seventh.value
            )
        }
    }
}