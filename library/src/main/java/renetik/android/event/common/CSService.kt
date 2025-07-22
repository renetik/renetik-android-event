package renetik.android.event.common

import android.app.Service
import android.content.Intent
import android.os.Binder
import renetik.android.core.kotlin.className
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.lang.atomic.CSAtomic.Companion.atomic
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrationsMap

abstract class CSService : Service(), CSHasRegistrationsHasDestruct {

    inner class CSServiceBinder : Binder() {
        val service: CSService = this@CSService
    }

    private val binder = CSServiceBinder()
    override fun onBind(intent: Intent) = binder

    final override val registrations by lazy { CSRegistrationsMap(this) }
    final override val eventDestruct = event<Unit>()
    final override var isDestructed by atomic(false)
        private set

    override fun onDestruct() {
        if (isDestructed) {
            logWarnTrace { "Already destroyed: $this" }; return
        }
        isDestructed = true
        registrations.cancel()
        eventDestruct.fire().clear()
        expectWeaklyReachable("$className $this onDestroy")
    }

    override fun onDestroy() {
        super.onDestroy()
        destruct()
    }
}