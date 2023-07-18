package renetik.android.event.common

import renetik.android.core.kotlin.className
import renetik.android.core.kotlin.reflect.lazyValue
import renetik.android.core.lang.CSAssociations
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.lang.lazy.CSLazyVal.Companion.lazyVal
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrationsMap

open class CSModel(
    parent: CSHasDestruct? = null
) : CSHasRegistrationsHasDestruct {

    @Deprecated("Just one use in project..")
    val associated by lazy(::CSAssociations)

    final override val registrations by lazyVal { CSRegistrationsMap(this) }
    final override val eventDestruct by lazyVal { event<Unit>() }

    final override var isDestructed = false
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
        ::registrations.lazyValue?.cancel()
        ::eventDestruct.lazyValue?.fire()?.clear()
        expectWeaklyReachable("$className $this onDestroy")
    }
}

