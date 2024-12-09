package renetik.android.event.registration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import renetik.android.core.lang.result.mainScope

inline fun CoroutineDispatcher.launch(
    crossinline func: suspend (CSRegistration) -> Unit,
): CSRegistration {
    var job: Job? = null
    val registration = CSRegistration.CSRegistration(isActive = true, onCancel = {
        job?.let { if (!it.isCompleted) it.cancel() }
    })
    job = mainScope.launch(this) {
        if (isActive && registration.isActive) func(registration)
    }
    return registration
}