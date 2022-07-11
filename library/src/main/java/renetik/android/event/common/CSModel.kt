package renetik.android.event.common

import renetik.android.core.lang.CSAssociation
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.listenOnce
import renetik.android.event.registration.CSRegistrations
import renetik.android.event.registration.register

open class CSModel(
    parent: CSHasDestroy? = null) : CSHasRegistrationsHasDestroy {
    val associated = CSAssociation()
    override val eventDestroy = event<Unit>()
    final override val registrations = CSRegistrations()

    init {
        parent?.let { register(it.eventDestroy.listenOnce { onDestroy() }) }
    }

    override fun onDestroy() {
        registrations.cancel()
        eventDestroy.fire().clear()
    }
}

