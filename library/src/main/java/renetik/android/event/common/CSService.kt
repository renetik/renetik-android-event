package renetik.android.event.common

import android.app.Service
import renetik.android.core.kotlin.className
import renetik.android.core.kotlin.reflect.lazyValue
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.logging.CSLog
import renetik.android.event.CSEvent
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrationsMap

abstract class CSService : Service(), CSHasRegistrationsHasDestruct {
    final override val registrations by lazy { CSRegistrationsMap(this) }
    final override val eventDestruct by lazy { event<Unit>() }
    final override var isDestructed = false
        private set

    override fun onDestruct() {
        if (isDestructed) {
            CSLog.logWarnTrace { "Already destroyed: $this" }
            return
        }
        isDestructed = true
        ::registrations.lazyValue?.cancel()
        ::eventDestruct.lazyValue?.fire()?.clear()
        expectWeaklyReachable("$className $this onDestroy")
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestruct()
    }
}