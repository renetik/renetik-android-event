package renetik.android.event.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import renetik.android.core.lang.CSEnvironment.app
import renetik.android.core.lang.catchAllWarn
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.listenOnce
import renetik.android.event.registration.CSRegistrations
import renetik.android.event.registration.register

abstract class CSContext : ContextWrapper, CSHasContext {
    constructor() : this(app)
    constructor(context: Context) : super(context)
    constructor(csContext: CSContext) : this(csContext as CSHasContext)
    constructor(hasContext: CSHasContext) : this(hasContext.context) {
        register(hasContext.eventDestroy.listenOnce { onDestroy() })
    }

    final override val registrations = CSRegistrations()
    override val eventDestroy = event<Unit>()
    var isDestroyed = false
        private set

    override fun onDestroy() {
        if (isDestroyed) return
        isDestroyed = true
        eventDestroy.fire().clear()
        registrations.cancel()
    }

    override val context: Context get() = this

    override fun unregisterReceiver(receiver: BroadcastReceiver) =
        catchAllWarn { super.unregisterReceiver(receiver) }
}