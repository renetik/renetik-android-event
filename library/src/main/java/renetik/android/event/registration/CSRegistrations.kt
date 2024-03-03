package renetik.android.event.registration

import androidx.annotation.AnyThread

//TODO: Implement CSRegistrations Map and List with weak values wrappers ?
interface CSRegistrations : CSRegistration {

    // TODO!!! Why register don't return new registration,
    //  that when cancelled will remove also from registrations
    @AnyThread
    fun register(registration: CSRegistration): CSRegistration

    @AnyThread
    fun register(replace: CSRegistration?, registration: CSRegistration?): CSRegistration?

    @AnyThread
    fun cancel(registration: CSRegistration)
}