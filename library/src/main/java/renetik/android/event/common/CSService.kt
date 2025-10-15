package renetik.android.event.common

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import renetik.android.core.kotlin.className
import renetik.android.core.lang.CSLeakCanary.expectWeaklyReachable
import renetik.android.core.lang.atomic.CSAtomic.Companion.atomic
import renetik.android.core.lang.variable.CSWeakVariable.Companion.weak
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire
import renetik.android.event.registration.CSRegistrationsMap

open class CSServiceConnection<Service : CSService> : ServiceConnection {
    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        @Suppress("UNCHECKED_CAST")
        onConnected((binder as CSServiceBinder).service as Service)
    }

    override fun onServiceDisconnected(name: ComponentName?) = onDisconnected()

    override fun onBindingDied(name: ComponentName?) = onDisconnected()

    override fun onNullBinding(name: ComponentName?) = onDisconnected()

    open fun onConnected(service: Service) = Unit
    open fun onDisconnected() = Unit
}

class CSServiceBinder(service: CSService) : Binder() {
    val service: CSService? by weak(service)
}

abstract class CSService : Service(), CSHasRegistrationsHasDestruct {
    private val binder by lazy { CSServiceBinder(this) }
    override fun onBind(intent: Intent) = binder

    final override val registrations = CSRegistrationsMap(className)
    final override val eventDestruct = event<Unit>()
    final override var isDestructed by atomic(false)
        private set

    override fun onDestruct() {
        if (isDestructed) {
            logWarnTrace { "Already destroyed: $this" }; return
        }
        isDestructed = true
        registrations.cancel()
        eventDestruct.fire().clear()
        expectWeaklyReachable { "$className $this onDestruct" }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isDestructed) destruct()
    }
}