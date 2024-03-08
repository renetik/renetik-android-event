package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.logging.CSLog.logWarnTrace

open class CSRegistrationImpl(
    isActive: Boolean = false
) : CSRegistration {
    @get:Synchronized
    final override var isActive: Boolean = isActive
        private set

    @get:Synchronized
    final override var isCanceled: Boolean = false
        private set

    @Synchronized
    @AnyThread
    final override fun resume() {
        if (isCanceled) {
            logWarnTrace { "Already canceled:$this" }
            return
        }
        if (!isActive) {
            isActive = true
            onResume()
        }
//        else logWarnTrace { "Already resume:$this" }
    }

    open fun onResume() = Unit

    @Synchronized
    @AnyThread
    final override fun pause() {
        if (isCanceled) {
            logWarnTrace { "Already canceled:$this" }
            return
        }
        if (isActive) {
            isActive = false
            onPause()
        }
//        else
//            logWarnTrace { "Already pause:$this" }
    }

    open fun onPause() = Unit

    @Synchronized
    @AnyThread
    final override fun cancel() {
        if (isCanceled) return
        if (isActive) pause()
        isCanceled = true
        onCancel()
    }

    open fun onCancel() = Unit
}