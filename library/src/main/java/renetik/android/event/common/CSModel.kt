package renetik.android.event.common

import androidx.annotation.AnyThread
import renetik.android.core.kotlin.className
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.lang.atomic.CSAtomic.Companion.atomic
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.invoke
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistrationsMap

open class CSModel(
    parent: CSHasDestruct? = null
) : CSHasRegistrationsHasDestruct {

    private val lazyRegistrations = lazy { CSRegistrationsMap(this) }
    final override val registrations: CSRegistrationsMap by lazyRegistrations

    @Synchronized
    @AnyThread
    fun register(key: String, registration: CSRegistration?): CSRegistration? =
        registrations.register(key, registration)

    final override val eventDestruct = event<Unit>()

    final override var isDestructed by atomic(false)
        private set

    init {
        parent?.let(::registerParent)
    }

    override fun onDestruct() {
        if (isDestructed) {
            logWarnTrace { "Already destroyed: $this" }
            return
        }
        isDestructed = true
        if (lazyRegistrations.isInitialized()) registrations.cancel()
        eventDestruct().clear()
        expectWeaklyReachable("$className $this onDestroy")
    }
}

