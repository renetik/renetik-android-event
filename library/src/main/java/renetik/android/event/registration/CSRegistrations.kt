package renetik.android.event.registration

import androidx.annotation.AnyThread

// Implement CSRegistrations Map and List with weak values wrappers ?
interface CSRegistrations : CSRegistration {

    @AnyThread
    fun register(registration: CSRegistration): CSRegistration

    @AnyThread
    fun register(replace: CSRegistration?, registration: CSRegistration?): CSRegistration?

    @AnyThread
    fun register(key: String, registration: CSRegistration?): CSRegistration?
}