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

fun CSHasChangeValue<Boolean>.isTrue(parent: CSHasRegistrations? = null) =
    delegate(parent, from = { it })

@JvmName("CSHasChangeValueOptionalBooleanDelegateIsTrue")
fun CSHasChangeValue<Boolean?>.isTrue(parent: CSHasRegistrations? = null) =
    delegate(parent, from = { it == true })

fun CSHasChangeValue<Boolean>.isFalse(parent: CSHasRegistrations? = null) =
    delegate(parent, from = { !it })

val CSHasChangeValue<Boolean>.eventIsTrue: CSHasChange<Unit>
    get() = object : CSHasChange<Unit> {
        override fun onChange(function: (Unit) -> Unit) =
            this@eventIsTrue.onTrue { function(Unit) }
    }

val CSHasChangeValue<Boolean>.eventIsFalse: CSHasChange<Unit>
    get() = (!this).eventIsTrue

fun CSHasChangeValue<Boolean>.actionTrue(function: () -> Unit): CSRegistration {
    if (isTrue) function()
    return onTrue(function)
}

fun CSHasChangeValue<Boolean>.actionFalse(function: () -> Unit): CSRegistration {
    if (isFalse) function()
    return onFalse(function)
}

operator fun CSHasChangeValue<Boolean>.not() = delegate(from = { !it })

fun CSHasChangeValue<Boolean>.onTrueUntilFalse(
    registration: () -> CSRegistration?): CSRegistration =
    actionTrue { untilFalse(registration()) }

fun CSHasChangeValue<Boolean>.untilFalse(
    registration: CSRegistration): CSRegistration =
    onFalse { registration.cancel() }

@JvmName("untilFalseCSRegistrationNullable")
fun CSHasChangeValue<Boolean>.untilFalse(
    registration: CSRegistration?): CSRegistration? =
    registration?.let(::untilFalse)