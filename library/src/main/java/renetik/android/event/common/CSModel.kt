package renetik.android.event.common


import renetik.android.core.lang.CSAssociation
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrations

open class CSModel(
    parent: CSHasDestroy? = null) : CSHasRegistrationsHasDestroy {
    val associated = CSAssociation()
    override val eventDestroy = event<Unit>()
    final override val registrations = CSRegistrations()
    var isDestroyed = false
        private set

    init {
        parent?.let { parent(it) }
    }

    override fun onDestroy() {
        isDestroyed = true
        registrations.cancel()
        eventDestroy.fire().clear()
        expectWeaklyReachable("Model $this onDestroy")
    }
}

