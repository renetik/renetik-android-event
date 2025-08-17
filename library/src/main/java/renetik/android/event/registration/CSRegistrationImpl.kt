package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.logging.CSLog.logWarnTrace
import renetik.android.event.CSEvent.Companion.event
import renetik.android.event.fire

open class CSRegistrationImpl(
    isActive: Boolean = false
) : CSRegistration {
    @Volatile
    final override var isActive: Boolean = isActive
        private set

    @Volatile
    final override var isCanceled: Boolean = false
        private set

    override val eventCancel by lazy { event().firingOnMain(this) }

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
    }

    open fun onPause() = Unit

    @Synchronized
    @AnyThread
    final override fun cancel() {
        if (isCanceled) return
        if (isActive) pause()
        isCanceled = true
        onCancel()
        eventCancel.fire()
    }

    open fun onCancel() = Unit
}