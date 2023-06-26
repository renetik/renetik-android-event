package renetik.android.event.common

import androidx.annotation.AnyThread
import renetik.android.core.kotlin.className
import renetik.android.core.lang.CSAssociations
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistrationsMap

open class CSModel(
    parent: CSHasDestruct? = null
) : CSHasRegistrationsHasDestruct {

    val associated by lazy(::CSAssociations)
    final override val registrations by lazy { CSRegistrationsMap(this) }
    final override val eventDestruct by lazy { event<Unit>() }
    final override var isDestructed = false
        private set

    init {
        parent?.let { parent(it) }
    }

    override fun onDestruct() {
        if (isDestructed) {
            logWarnTrace { "Already destroyed: $this" }
            return
        }
        isDestructed = true
        registrations.cancel()
        eventDestruct.fire().clear()
        expectWeaklyReachable("$className $this onDestroy")
    }
}

@Synchronized
@AnyThread
fun CSModel.register(key: String, registration: CSRegistration?): CSRegistration? =
    registrations.register(key, registration)

