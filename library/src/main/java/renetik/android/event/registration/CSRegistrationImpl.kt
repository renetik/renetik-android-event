package renetik.android.event.registration

import androidx.annotation.AnyThread

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
        pause()
        isCanceled = true
    }
}