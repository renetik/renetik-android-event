package renetik.android.event.registration

import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration
import kotlin.properties.Delegates.notNull

val mainScope = MainScope()

fun CSHasRegistrations.launch(func: suspend () -> Unit) {
    val self = this
    var job: Job by notNull()
    val registration = this + CSRegistration { job.cancel() }
    job = mainScope.launch {
        func()
        self.cancel(registration)
    }
}