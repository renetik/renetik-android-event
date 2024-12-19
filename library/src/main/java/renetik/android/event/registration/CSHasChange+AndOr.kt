package renetik.android.event.registration

import renetik.android.core.lang.value.CSValue
import renetik.android.event.registration.CSHasChangeValue.Companion.delegate

infix fun CSHasChangeValue<Boolean>.and(other: CSHasChangeValue<Boolean>) =
    (this to other).delegate(from = { first, second -> first && second })

infix fun CSHasChange<*>.and(other: CSValue<Boolean>): CSHasChange<Unit> {
    val self = this
    return object : CSHasChange<Unit> {
        override fun onChange(function: (Unit) -> Unit): CSRegistration =
            CSRegistration(self.onChange { if (other.value) function(Unit) })
    }
}

infix fun CSHasChangeValue<Boolean>.or(other: CSHasChangeValue<Boolean>) =
    (this to other).delegate(from = { first, second -> first || second })

infix fun <T> CSHasChangeValue<T>.or(second: CSHasChange<*>): CSHasChangeValue<T> {
    val first = this
    return object : CSHasChangeValue<T> {
        override val value: T get() = first.value
        override fun onChange(function: (T) -> Unit) = CSRegistration(
            first.onChange { function(it) },
            second.onChange { function(value) },
        )
    }
}

infix fun CSHasChange<*>.or(other: CSHasChange<*>): CSHasChange<Unit> {
    val self = this
    return object : CSHasChange<Unit> {
        override fun onChange(function: (Unit) -> Unit) = CSRegistration(
            self.onChange { function(Unit) },
            other.onChange { function(Unit) },
        )
    }
}