package renetik.android.event.common

import renetik.android.core.kotlin.className
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.lang.atomic.CSAtomic.Companion.atomic
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrationsMap

open class CSModel(
    parent: CSHasDestruct? = null
) : CSHasRegistrationsHasDestruct {
    final override val registrations by lazy { CSRegistrationsMap(this) }
    final override val eventDestruct = event<Unit>()
    final override var isDestructed by atomic(false)
        private set

    init {
        parent?.let(::parent)
    }

    override fun onDestruct() {
        if (isDestructed) {
            logWarnTrace { "Already destroyed: $this" }
            return
        }
        isDestructed = true
        registrations.cancel()
        eventDestruct.fire().clear()
        expectWeaklyReachable { "$className $this onDestruct" }
    }
}

