package renetik.android.event.registration

interface CSRegistrations : CSRegistration {
    fun register(registration: CSRegistration): CSRegistration
    fun register(replace: CSRegistration?, registration: CSRegistration?): CSRegistration?
    fun cancel(registration: CSRegistration)
}