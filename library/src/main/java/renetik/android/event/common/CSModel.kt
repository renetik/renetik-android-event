package renetik.android.event.common


import renetik.android.core.kotlin.className
import renetik.android.core.lang.CSAssociations
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.logging.CSLog.logWarn
import renetik.android.core.logging.CSLogMessage.Companion.message
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrationsMap

open class CSModel(
    parent: CSHasDestroy? = null) : CSHasRegistrationsHasDestroy {

    val associated by lazy { CSAssociations() }
    final override val registrations by lazy { CSRegistrationsMap(this) }
    final override val eventDestroy by lazy { event<Unit>() }
    var isDestroyed = false
        private set

    init {
        parent?.let { parent(it) }
    }

    override fun onDestroy() {
        if (isDestroyed) {
            logWarn { message("Already destroyed: $this") }
            return
        }
        isDestroyed = true
        registrations.cancel()
        eventDestroy.fire().clear()
        expectWeaklyReachable("$className $this onDestroy")
    }
}

