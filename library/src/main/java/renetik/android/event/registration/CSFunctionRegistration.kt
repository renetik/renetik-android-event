package renetik.android.event.registration

import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.Func

fun CSFunctionRegistration(function: ArgFunc<CSRegistration>) =
    CSFunctionRegistration(function, null)

class CSFunctionRegistration(function: ArgFunc<CSRegistration>,
                             val onCancel: ArgFunc<Func>? = null) : CSRegistration {

    override var isActive = true
    val function: Func = {
        if (isActive) function(this)
    }

    override fun cancel() {
        isActive = false
        onCancel?.invoke(function)
    }
}