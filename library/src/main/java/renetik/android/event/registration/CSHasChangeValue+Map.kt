package renetik.android.event.registration

import renetik.android.core.lang.ArgFun
import renetik.android.core.lang.notNull
import renetik.android.core.lang.variable.assign
import renetik.android.event.common.CSHasDestruct
import renetik.android.event.common.destruct
import renetik.android.event.property.CSProperty.Companion.lateProperty

fun <T> CSHasChangeValue<T>.hasChangeValue(
    parent: CSHasRegistrations? = null, onChange: ArgFun<T>? = null,
): CSHasChangeValue<T> = hasChangeValue(parent, from = { it }, onChange)

fun <Argument, Return> CSHasChangeValue<Argument>.hasChangeValue(
    parent: CSHasRegistrations? = null,
    from: (Argument) -> Return,
    onChange: ArgFun<Return>? = null
): CSHasChangeValue<Return> = let { source ->
    object : CSHasChangeValueBase<Return>(parent, onChange) {
        override var value: Return = from(source.value)

        init {
            this + source.onChange { value(from(it)) }
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

fun <Argument, Return> List<CSHasChangeValue<Argument>>.hasChangeValue(
    parent: CSHasRegistrations,
    from: (List<Argument>) -> Return
): CSHasChangeValue<Return> =
    lateProperty<Return>().also { property ->
        parent + action { list -> property assign from(list) }
    }

