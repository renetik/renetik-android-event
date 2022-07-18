package renetik.android.event.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import renetik.android.core.kotlin.unexpected
import renetik.android.core.lang.CSEnvironment.app
import renetik.android.core.lang.catchAllWarn
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrations
import renetik.android.event.registration.listenOnce

abstract class CSContext : ContextWrapper, CSHasContext {
    constructor() : super(app)
    constructor(context: Context) : super(context)

    constructor(parent: CSContext) : this(parent.context) {
        if (parent.isDestroyed) unexpected()
        listenOnce(parent.eventDestroy) { onDestroy() }
    }

    constructor(parent: CSHasContext) : this(parent.context) {
        listenOnce(parent.eventDestroy) { onDestroy() }
    }

    final override val registrations = CSRegistrations()

    override val eventDestroy = event<Unit>()

    var isDestroyed = false
        private set

    override fun onDestroy() {
        if (isDestroyed) return
        isDestroyed = true
        registrations.cancel()
        eventDestroy.fire().clear()
    }

    override val context: Context get() = this

    override fun unregisterReceiver(receiver: BroadcastReceiver) =
        catchAllWarn { super.unregisterReceiver(receiver) }
}