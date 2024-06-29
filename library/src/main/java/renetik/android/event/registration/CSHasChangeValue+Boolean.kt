package renetik.android.event.registration

import kotlinx.coroutines.suspendCancellableCoroutine
import renetik.android.core.kotlin.primitives.isFalse
import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.value.isFalse
import renetik.android.core.lang.value.isTrue
import renetik.android.event.registration.CSHasChangeValue.Companion.delegate
import kotlin.Result.Companion.success

suspend fun CSHasChangeValue<Boolean>.waitIsTrue() {
    if (isFalse) suspendCancellableCoroutine {
        var registration: CSRegistration? = null
        registration = onTrue {
            registration?.cancel()
            registration = null
            it.resumeWith(success(Unit))
        }
        it.invokeOnCancellation { registration?.cancel() }
    }
}

suspend fun CSHasChangeValue<Boolean>.waitIsFalse() {
    if (isTrue) suspendCancellableCoroutine {
        var registration: CSRegistration? = null
        registration = onFalse {
            registration?.cancel()
            registration = null
            it.resumeWith(success(Unit))
        }
        it.invokeOnCancellation { registration?.cancel() }
    }
}

fun CSHasChangeValue<Boolean>.onFalse(function: () -> Unit): CSRegistration =
    onChange { if (it.isFalse) function() }


fun CSHasChangeValue<Boolean>.onTrue(function: () -> Unit): CSRegistration =
    onChange { if (it.isTrue) function() }

fun CSHasChangeValue<Boolean>.eventTrue(): CSHasChange<Unit> {
    val self = this
    return object : CSHasChange<Unit> {
        override fun onChange(function: (Unit) -> Unit): CSRegistration =
            self.onTrue { function(Unit) }
    }
}

fun CSHasChangeValue<Boolean>.actionTrue(function: () -> Unit): CSRegistration {
    if (isTrue) function()
    return onTrue(function)
}

fun CSHasChangeValue<Boolean>.actionFalse(function: () -> Unit): CSRegistration {
    if (isFalse) function()
    return onFalse(function)
}

operator fun CSHasChangeValue<Boolean>.not() = delegate(from = { !it })

infix fun CSHasChangeValue<Boolean>.and(other: CSHasChangeValue<Boolean>) =
    (this to other).delegate(from = { first, second -> first && second })

infix fun CSHasChangeValue<Boolean>.or(other: CSHasChangeValue<Boolean>) =
    (this to other).delegate(from = { first, second -> first || second })