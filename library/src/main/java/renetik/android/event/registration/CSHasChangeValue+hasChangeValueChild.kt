@file:Suppress("NOTHING_TO_INLINE")

package renetik.android.event.registration

import renetik.android.core.lang.notNull

@JvmName("stateDelegateChild")
fun <ParentValue, Return> CSHasChangeValue<ParentValue>.stateDelegate(
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
        CSHasChangeValue<ParentValue>.stateDelegateNullable(
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
