package renetik.android.event.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.AnyThread
import renetik.android.core.extensions.content.register
import renetik.android.core.extensions.content.unregister
import renetik.android.core.lang.CSEnvironment.app
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.logging.CSLog.logErrorTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistration
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

    private val lazyRegistrations = lazy { CSRegistrationsMap(this) }
    final override val registrations by lazyRegistrations

//    @Synchronized
//    @AnyThread
//    fun register(key: String, registration: CSRegistration?): CSRegistration? =
//        registrations.register(key, registration)

    final override val eventDestruct = event<Unit>()

    final override var isDestructed = false
        private set

    override fun onDestruct() {
        if (isDestructed) {
            logErrorTrace { "Already destroyed: $this" }
            return
        }
        isDestructed = true
        if (lazyRegistrations.isInitialized()) registrations.cancel()
        eventDestruct.fire().clear()
        expectWeaklyReachable("CSContext $this onDestroy")
    }

    override fun registerReceiver(
        receiver: BroadcastReceiver?, filter: IntentFilter,
    ): Intent? = register(receiver, filter)

    override fun unregisterReceiver(receiver: BroadcastReceiver) = unregister(receiver)
}