package renetik.android.event.registration

import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun CSMultiRegistration(vararg registrations: CSRegistration) =
    CSRegistration(onCancel = { registrations.forEach(CSRegistration::cancel) })

//class CSMultiRegistration(
//    private vararg val registrations: CSRegistration) : CSRegistration {
//    override var isActive = true
//    override fun cancel() {
//        isActive = false
//        registrations.forEach(CSRegistration::cancel)
//    }
//}