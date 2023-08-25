package renetik.android.event.registration

import androidx.annotation.AnyThread
import java.io.Closeable
import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.CSHasCancel
import renetik.android.core.lang.Func

interface CSRegistration : CSHasCancel, Closeable {
    val isActive: Boolean
    val isCanceled: Boolean

    @AnyThread
    fun resume()

    @AnyThread
    fun pause()

    @AnyThread
    override fun cancel()

    companion object {
        fun paused(registration: CSRegistration?, function: Func) {
            registration?.pause()
            function()
            registration?.resume()
        }

        fun paused(vararg registrations: CSRegistration, function: Func) {
            registrations.onEach { it.pause() }
            function()
            registrations.onEach { it.resume() }
        }

        fun resume(vararg registrations: CSRegistration) {
            registrations.onEach { it.resume() }
        }

        fun pause(vararg registrations: CSRegistration) {
            registrations.onEach { it.pause() }
        }

        fun CSRegistration(
            isActive: Boolean = false,
            onResume: ArgFunc<CSRegistration>? = null,
            onPause: ArgFunc<CSRegistration>? = null,
            onCancel: ArgFunc<CSRegistration>? = null,
        ) = object : CSRegistrationImpl(isActive) {
            override fun onResume() {
                super.onResume()
                onResume?.invoke(this)
            }

            override fun onPause() {
                super.onPause()
                onPause?.invoke(this)
            }

            override fun onCancel() {
                super.onCancel()
                onCancel?.invoke(this)
            }

        }
    }

    override fun close() = cancel()
}
