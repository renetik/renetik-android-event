package renetik.android.event.registration

import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.Func

interface CSRegistration {
    var isActive: Boolean
    fun cancel() {
        isActive = false
    }

    companion object {
        fun pause(vararg registrations: CSRegistration, function: Func) {
            registrations.onEach { it.isActive = false }
            function()
            registrations.onEach { it.isActive = true }
        }

        fun CSRegistration(onCancel: ArgFunc<CSRegistration>? = null) =
            object : CSRegistration {
                override var isActive = true
                override fun cancel() {
                    super.cancel()
                    onCancel?.invoke(this)
                }
            }
    }
}