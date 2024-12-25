package renetik.android.event.registration

import renetik.android.core.lang.value.CSValue
import renetik.android.core.lang.value.ifTrue
import renetik.android.event.registration.CSHasChangeValue.Companion.delegate

@JvmName("CSHasChangeValueBooleanAndCSHasChangeValueBoolean")
infix fun CSHasChangeValue<Boolean>.and(other: CSHasChangeValue<Boolean>) =
    (this to other).delegate(from = { first, second -> first && second })

@JvmName("CSHasChangeAndCSValue")
infix fun CSHasChange<*>.and(other: CSValue<Boolean>): CSHasChange<Unit> {
    val self = this
    return object : CSHasChange<Unit> {
        override fun onChange(function: (Unit) -> Unit): CSRegistration =
            CSRegistration(self.onChange { if (other.value) function(Unit) })
    }
}

@JvmName("CSHasChangeValueAndCSHasChangeValueBoolean")
infix fun <T> CSHasChangeValue<T>.and(other: CSHasChangeValue<Boolean>): CSHasChangeValue<T> {
    val first = this
    return object : CSHasChangeValue<T> {
        override val value: T get() = first.value
        override fun onChange(function: (T) -> Unit) = CSRegistration(
            first.onChange { other.ifTrue { function(it) } },
            other.onTrue { function(value) },
        )
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