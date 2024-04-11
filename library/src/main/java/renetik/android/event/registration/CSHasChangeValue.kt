package renetik.android.event.registration

import renetik.android.core.kotlin.collections.list
import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.Quadruple
import renetik.android.core.lang.Quintuple
import renetik.android.core.lang.Sixtuple
import renetik.android.core.lang.value.CSValue
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.property.CSProperty

interface CSHasChangeValue<T> : CSValue<T>, CSHasChange<T> {
    companion object {
        inline fun <T, Return> CSHasChangeValue<T>.delegate(
            parent: CSHasRegistrations? = null,
            crossinline from: (T) -> Return, noinline onChange: ArgFunc<Return>? = null,
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

        inline fun <ParentValue, ChildValue>
                CSHasChangeValue<ParentValue>.delegateNullableChild(
            parent: CSHasRegistrations? = null,
            crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>?,
            noinline onChange: ((ChildValue?) -> Unit)? = null
        ): CSHasChangeValue<ChildValue?> = let { property ->
            object : CSHasChangeValue<ChildValue?> {
                override val value: ChildValue? get() = child(property.value)?.value
                override fun onChange(function: (ChildValue?) -> Unit): CSRegistration {
                    var childRegistration: CSRegistration? = null
                    val parentRegistration = property.action { parentValue ->
                        childRegistration?.cancel()
                        childRegistration = child(parentValue)?.let {
                            it.onChange { childValue ->
                                onChange?.invoke(childValue)
                                function(childValue)
                            }
                        } ?: null.also { onChange?.invoke(it); function(it); }
                    }
                    return CSRegistration.CSRegistration(isActive = true, onCancel = {
                        parentRegistration.cancel()
                        childRegistration?.cancel()
                    }).also { parent?.register(it) }
                }
            }
        }

        //TODO: This and "computed" is same in essence just different implementation we need to choose one and use that.
        fun <Argument, Return>
                CSHasChangeValue<Argument>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument) -> Return,
            onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> = let { property ->
            object : CSProperty<Return> {
                val event = event<Return>()
                override var value: Return = from(property.value)
                override fun onChange(function: (Return) -> Unit) =
                    event.listen { function(value) }

                init {
                    property.onChange { item1 ->
                        value = from(item1)
                        onChange?.invoke(value)
                        event.fire(value)
                    }.also { parent?.register(it) }
                }

                override fun value(newValue: Return, fire: Boolean) {
                    if (value == newValue) return
                    value = newValue
                    if (fire) fireChange()
                }

                override fun fireChange() {
                    onChange?.invoke(value)
                    event.fire(value)
                }
            }
        }

        inline fun <ParentValue, ChildValue>
                CSHasChangeValue<ParentValue>.hasChangeValueNullableChild(
            parent: CSHasRegistrations? = null,
            crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>?,
            noinline onChange: ((ChildValue?) -> Unit)? = null
        ): CSHasChangeValue<ChildValue?> {
            val event = event<ChildValue?>()
            var value: ChildValue? = null
            actionNullableChild(
                child = { child(it) },
                onChange = {
                    value = it
                    onChange?.invoke(value)
                    event.fire(it)
                }
            ).also { parent?.register(it) }
            return object : CSHasChangeValue<ChildValue?> {
                override val value: ChildValue? get() = value
                override fun onChange(function: (ChildValue?) -> Unit): CSRegistration =
                    event.listen(function)
            }
        }

        inline fun <Argument1, Argument2, Return> hasChangeValue(
            parent: CSHasRegistrations? = null,
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            crossinline from: (Argument1, Argument2) -> Return,
            noinline onChange: ArgFunc<Return>? = null
        ): CSHasChangeValue<Return> = object : CSProperty<Return> {
            val event = event<Return>()
            override var value: Return = from(item1.value, item2.value)
            override fun onChange(function: (Return) -> Unit) = event.listen { function(value) }

            init {
                (item1 to item2).onChange { item1, item2 ->
                    value = from(item1, item2)
                    onChange?.invoke(value)
                    event.fire(value)
                }.also { parent?.register(it) }
            }

            override fun value(newValue: Return, fire: Boolean) {
                if (value == newValue) return
                value = newValue
                if (fire) fireChange()
            }

            override fun fireChange() {
                onChange?.invoke(value)
                event.fire(value)
            }
        }

        fun <Argument1, Argument2, Return>
                Pair<CSHasChangeValue<Argument1>,
                        CSHasChangeValue<Argument2>>.hasChangeValue(
            parent: CSHasRegistrations? = null,
            from: (Argument1, Argument2) -> Return,
        ): CSHasChangeValue<Return> = hasChangeValue(parent, first, second, from)

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

        inline fun <Argument1, Argument2> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            crossinline onAction: (Argument1, Argument2) -> Unit,
        ): CSRegistration = list(item1, item2).action {
            onAction(item1.value, item2.value)
        }

        inline fun <Argument1, Argument2>
                Pair<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>>.action(
            crossinline onAction: (Argument1, Argument2) -> Unit,
        ): CSRegistration = action(first, second, onAction)

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

        ///
        inline fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6> onChange(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            item5: CSHasChangeValue<Argument5>,
            item6: CSHasChangeValue<Argument6>,
            crossinline onAction: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Unit,
        ): CSRegistration = list(item1, item2, item3, item4, item5, item6).onChange {
            onAction(item1.value, item2.value, item3.value, item4.value, item5.value, item6.value)
        }

        inline fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
                Sixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>>.onChange(
            crossinline onChange: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Unit,
        ): CSRegistration = onChange(first, second, third, fourth, fifth, sixth, onChange)

        inline fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6> action(
            item1: CSHasChangeValue<Argument1>,
            item2: CSHasChangeValue<Argument2>,
            item3: CSHasChangeValue<Argument3>,
            item4: CSHasChangeValue<Argument4>,
            item5: CSHasChangeValue<Argument5>,
            item6: CSHasChangeValue<Argument6>,
            crossinline onAction: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Unit,
        ): CSRegistration = list(item1, item2, item3, item4, item5, item6).action {
            onAction(item1.value, item2.value, item3.value, item4.value, item5.value, item6.value)
        }

        inline fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6>
                Sixtuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                        CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                        CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>>.action(
            crossinline onChange: (Argument1, Argument2, Argument3, Argument4, Argument5, Argument6) -> Unit,
        ): CSRegistration = action(first, second, third, fourth, fifth, sixth, onChange)
    }
}