package renetik.android.event.registration

import renetik.android.core.lang.ArgFunc
import renetik.android.core.lang.Func

fun CSFunctionRegistration(function: ArgFunc<CSRegistration>) =
    CSFunctionRegistration(function, null)

class CSFunctionRegistration(
    function: ArgFunc<CSFunctionRegistration>,
    val onCancel: ArgFunc<CSFunctionRegistration>? = null)
    : CSRegistrationImpl(isActive = true) {

    val function: Func = { if (isActive) function(this) }

    override fun onCancel() {
        super.onCancel()
        onCancel?.invoke(this)
    }

    fun invoke() = function()
}