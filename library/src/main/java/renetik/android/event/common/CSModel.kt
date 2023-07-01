package renetik.android.event.common

import androidx.annotation.AnyThread
import renetik.android.core.kotlin.className
import renetik.android.core.lang.CSAssociations
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.lang.lazy.CSLazyVal.Companion.lazyVal
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistration
import renetik.android.event.registration.CSRegistrationsMap

open class CSModel(
    parent: CSHasDestruct? = null
) : CSHasRegistrationsHasDestruct {

    val associated by lazy(::CSAssociations)

    private val registrationsLazyVal = lazyVal { CSRegistrationsMap(this) }
    final override val registrations by registrationsLazyVal

    private val eventDestructLazyVal = lazyVal { event<Unit>() }
    final override val eventDestruct by eventDestructLazyVal

    final override var isDestructed = false
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
        if (registrationsLazyVal.isInitialized) registrations.cancel()
        if (eventDestructLazyVal.isInitialized) eventDestruct.fire().clear()
        expectWeaklyReachable("$className $this onDestroy")
    }
}

@Synchronized
@AnyThread
fun CSModel.register(key: String, registration: CSRegistration?): CSRegistration? =
    registrations.register(key, registration)

