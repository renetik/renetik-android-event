package renetik.android.event.common


import renetik.android.core.kotlin.className
import renetik.android.core.lang.CSAssociations
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.logging.CSLog.logWarn
import renetik.android.core.logging.CSLogMessage.Companion.message
import renetik.android.core.logging.CSLogMessage.Companion.traceMessage
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrationsMap

open class CSModel(
    parent: CSHasDestruct? = null) : CSHasRegistrationsHasDestruct {

    val associated by lazy { CSAssociations() }
    final override val registrations by lazy { CSRegistrationsMap(this) }
    final override val eventDestruct by lazy { event<Unit>() }
    final override var isDestructed = false
        private set

    init {
        parent?.let { parent(it) }
    }

    override fun onDestruct() {
        if (isDestructed) {
            logWarn { traceMessage("Already destroyed: $this") }
            return
        }
        isDestructed = true
        registrations.cancel()
        eventDestruct.fire().clear()
        expectWeaklyReachable("$className $this onDestroy")
    }
}

