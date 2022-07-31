package renetik.android.event.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import renetik.android.core.lang.CSAssociations
import renetik.android.core.lang.CSEnvironment.app
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.lang.catchAllWarn
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrations

abstract class CSContext : ContextWrapper, CSHasContext {
    constructor() : super(app)
    constructor(context: Context) : super(context)

    constructor(parent: CSContext) : this(parent.context) {
        parent(parent)
    }

    constructor(parent: CSHasContext) : this(parent.context) {
        parent(parent)
    }

    final override val context: Context get() = this
    val associated = CSAssociations()
    final override val registrations = CSRegistrations()
    final override val eventDestroy = event<Unit>()
    var isDestroyed = false
        private set

    override fun onDestroy() {
        isDestroyed = true
        registrations.cancel()
        eventDestroy.fire().clear()
        expectWeaklyReachable("CSContext $this onDestroy")
    }


    override fun unregisterReceiver(receiver: BroadcastReceiver) =
        catchAllWarn { super.unregisterReceiver(receiver) }
}