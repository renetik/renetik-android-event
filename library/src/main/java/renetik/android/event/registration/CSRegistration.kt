package renetik.android.event.registration

import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.Func

interface CSRegistration {
    val isActive: Boolean
    val isCanceled: Boolean
    fun resume()
    fun pause()
    fun cancel()

    companion object {
        fun pause(vararg registrations: CSRegistration, function: Func) {
            registrations.onEach { it.pause() }
            function()
            registrations.onEach { it.resume() }
        }

        fun CSRegistration(onResume: ArgFunc<CSRegistration>? = null,
                           onPause: ArgFunc<CSRegistration>? = null,
                           onCancel: ArgFunc<CSRegistration>? = null) =
            object : CSRegistrationImpl() {
                init {
                    onResume?.invoke(this)
                }

                override fun pause() {
                    super.pause()
                    onPause?.invoke(this)
                }

                override fun resume() {
                    super.resume()
                    onResume?.invoke(this)
                }

                override fun cancel() {
                    super.cancel()
                    onCancel?.invoke(this)
                }
            }
    }
}