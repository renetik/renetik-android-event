package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.logging.CSLog.logWarn
import renetik.android.core.logging.CSLogMessage.Companion.traceMessage

open class CSRegistrationImpl : CSRegistration {
    override var isActive: Boolean = true
    override var isCanceled: Boolean = false

    @Synchronized
    @AnyThread
    override fun pause() {
        isActive = false
    }

    @Synchronized
    @AnyThread
    override fun resume() {
        if (!isCanceled) isActive = true
    }

    @Synchronized
    @AnyThread
    override fun cancel() {
        if (isCanceled) {
            logWarn { traceMessage("Already canceled:$this") }
            return
        }
        pause()
        isCanceled = true
    }
}