package renetik.android.event.registration

import androidx.annotation.AnyThread
import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.Func

// TODO: ActivityRegistration vs Registration
interface CSRegistration {
    val isActive: Boolean
    val isCanceled: Boolean

    @AnyThread
    fun resume()

    @AnyThread
    fun pause()

    @AnyThread
    fun cancel()

    companion object {
        fun pause(vararg registrations: CSRegistration, function: Func) {
            registrations.onEach { it.pause() }
            function()
            registrations.onEach { it.resume() }
        }

        fun CSRegistration(isActive: Boolean = false,
                           onResume: ArgFunc<CSRegistration>? = null,
                           onPause: ArgFunc<CSRegistration>? = null,
                           onCancel: ArgFunc<CSRegistration>? = null) =
            object : CSRegistrationImpl(isActive) {
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
}