package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.logging.CSLog.logWarn
import renetik.android.core.logging.CSLogMessage.Companion.traceMessage

open class CSRegistrationImpl(
    override var isActive: Boolean = false) : CSRegistration {
    override var isCanceled: Boolean = false

    @Synchronized
    @AnyThread
    final override fun resume() {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
        if (isPaused) {
            isActive = true
            onResume()
        } else logWarn { traceMessage("Already resume:$this") }
    }

    open fun onResume() = Unit

    @Synchronized
    @AnyThread
    final override fun pause() {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
        if (isActive) {
            isActive = false
            onPause()
        } else
            logWarn { traceMessage("Already pause:$this") }
    }

    open fun onPause() = Unit

    @Synchronized
    @AnyThread
    final override fun cancel() {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
        if (isActive) pause()
        isCanceled = true
        onCancel()
//TODO: this will be probably leaking for now
// as it can stan in registrations list after canceled
// expectWeaklyReachable("$className $this cancel")
    }

    open fun onCancel() = Unit
}