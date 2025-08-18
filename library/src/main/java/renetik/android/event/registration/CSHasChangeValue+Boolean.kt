@file:OptIn(ExperimentalAtomicApi::class)

package renetik.android.event.registration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.suspendCancellableCoroutine
import renetik.android.core.kotlin.className
import renetik.android.core.kotlin.primitives.isFalse
import renetik.android.core.kotlin.primitives.isTrue
import renetik.android.core.lang.value.isFalse
import renetik.android.core.lang.value.isTrue
import renetik.android.event.registration.CSHasChangeValue.Companion.delegate
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import kotlin.Result.Companion.success
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

// isTrue|isFalse|onFalse can changed on other thread
suspend fun CSHasChangeValue<Boolean>.waitIsTrue() {
    if (isFalse) suspendCancellableCoroutine {
        val registration = AtomicReference<CSRegistration?>(null)
        fun resume() = registration.exchange(null)?.apply {
            cancel()
            it.resumeWith(success(Unit))
        }
        registration.store(onTrue { resume() })
        if (isTrue) resume()
        it.invokeOnCancellation { registration.exchange(null)?.cancel() }
    }
}

// isTrue|isFalse|onFalse can changed on other thread
suspend fun CSHasChangeValue<Boolean>.waitIsFalse() {
    if (isTrue) suspendCancellableCoroutine {
        val registration = AtomicReference<CSRegistration?>(null)
        fun resume() = registration.exchange(null)?.apply {
            cancel()
            it.resumeWith(success(Unit))
        }
        registration.store(onFalse { resume() })
        if (isFalse) resume()
        it.invokeOnCancellation { registration.exchange(null)?.cancel() }
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

fun CSHasChangeValue<Boolean>.actionTrueLaunch(
    dispatcher: CoroutineDispatcher = Main,
    function: () -> Unit
): CSRegistration = CSRegistrationsMap(className).also {
    it + actionTrue { it.launch(dispatcher) { function() } }
}

fun CSHasChangeValue<Boolean>.actionFalse(function: () -> Unit): CSRegistration {
    if (isFalse) function()
    return onFalse(function)
}

fun CSHasChangeValue<Boolean>.actionFalseLaunch(
    dispatcher: CoroutineDispatcher = Main,
    function: () -> Unit
): CSRegistration = CSRegistrationsMap(className).also {
    it + actionFalse { it.launch(dispatcher) { function() } }
}

operator fun CSHasChangeValue<Boolean>.not() = delegate(from = { !it })

fun CSHasChangeValue<Boolean>.onTrueUntilFalse(
    registration: () -> CSRegistration?
): CSRegistration {
    var untilFalseRegistration: CSRegistration? = null
    val actionTrueRegistration = actionTrue {
        untilFalseRegistration?.cancel()
        untilFalseRegistration = untilFalse(registration())
    }
    return CSRegistration {
        actionTrueRegistration.cancel()
        untilFalseRegistration?.cancel()
    }
}

fun CSHasChangeValue<Boolean>.untilFalse(
    registration: CSRegistration
): CSRegistration = onFalse { registration.cancel() }

@JvmName("untilFalseCSRegistrationNullable")
fun CSHasChangeValue<Boolean>.untilFalse(
    registration: CSRegistration?
): CSRegistration? = registration?.let { untilFalse(it) }