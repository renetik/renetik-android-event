package renetik.android.event.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import renetik.android.core.extensions.content.register
import renetik.android.core.extensions.content.unregister
import renetik.android.core.lang.CSEnvironment.app
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.logging.CSLog.logErrorTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.invoke
import renetik.android.event.registration.CSRegistrationsMap

abstract class CSContext : ContextWrapper, CSHasContext {
    constructor() : super(app)
    constructor(context: Context) : super(context)

    constructor(parent: CSContext) : this(parent.context) {
        parent(parent)
    }

    constructor(parent: CSHasContext) : this(parent.context) {
        parent(parent)
    }

    constructor(parent: CSHasDestruct, context: Context) : this(context) {
        parent(parent)
    }

    // Returning wrapped context as there is no use of this wrapped in child anywhere....
    final override val context: Context get() = baseContext

    final override val registrations by lazy { CSRegistrationsMap(this) }

    final override val eventDestruct = event<Unit>()
    final override var isDestructed = false
        private set

    override fun onDestruct() {
        if (isDestructed) {
            logErrorTrace { "Already destroyed: $this" }
            return
        }
        isDestructed = true
        registrations.cancel()
        eventDestruct().clear()
        expectWeaklyReachable("CSContext $this onDestroy")
    }
}