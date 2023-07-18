package renetik.android.event.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import renetik.android.core.kotlin.reflect.lazyValue
import renetik.android.core.lang.CSAssociations
import renetik.android.core.lang.CSEnvironment.app
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.lang.catchAllWarn
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrationsMap

abstract class CSContext : ContextWrapper, CSHasContext {
    constructor() : super(app)
    constructor(context: Context) : super(context)

    constructor(parent: CSContext) : this(parent.context) {
        registerParent(parent)
    }

    constructor(parent: CSHasContext) : this(parent.context) {
        registerParent(parent)
    }

    final override val context: Context get() = this

    @Deprecated("Just one use in project..")
    val associated by lazy(::CSAssociations)

    final override val registrations by lazy { CSRegistrationsMap(this) }
    final override val eventDestruct by lazy { event<Unit>() }

    final override var isDestructed = false
        private set

    override fun onDestruct() {
        if (isDestructed) {
            logWarnTrace { "Already destroyed: $this" }
            return
        }
        isDestructed = true
        ::registrations.lazyValue?.cancel()
        ::eventDestruct.lazyValue?.fire()?.clear()
        expectWeaklyReachable("CSContext $this onDestroy")
    }

    override fun unregisterReceiver(receiver: BroadcastReceiver) =
        catchAllWarn { super.unregisterReceiver(receiver) }
}