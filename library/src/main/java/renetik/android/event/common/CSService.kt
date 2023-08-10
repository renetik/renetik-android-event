package renetik.android.event.common

import android.app.Service
import renetik.android.core.kotlin.className
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrationsMap

abstract class CSService : Service(), CSHasRegistrationsHasDestruct {
    private val lazyRegistrations = lazy { CSRegistrationsMap(this) }
    final override val registrations by lazyRegistrations
    final override val eventDestruct = event<Unit>()
    final override var isDestructed = false
        private set

    override fun onDestruct() {
        if (isDestructed) {
            logWarnTrace { "Already destroyed: $this" }; return
        }
        isDestructed = true
        if (lazyRegistrations.isInitialized()) registrations.cancel()
        eventDestruct.fire().clear()
        expectWeaklyReachable("$className $this onDestroy")
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestruct()
    }
}