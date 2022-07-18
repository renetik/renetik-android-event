package renetik.android.event.common

import renetik.android.core.lang.CSAssociation
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrations
import renetik.android.event.registration.listenOnce

open class CSModel(
    parent: CSHasDestroy? = null) : CSHasRegistrationsHasDestroy {
    val associated = CSAssociation()
    override val eventDestroy = event<Unit>()
    final override val registrations = CSRegistrations()

    init {
        parent?.let { listenOnce(it.eventDestroy) { onDestroy() } }
    }

    override fun onDestroy() {
        registrations.cancel()
        eventDestroy.fire().clear()
    }
}

