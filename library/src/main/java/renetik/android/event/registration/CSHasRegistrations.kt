package renetik.android.event.registration

//TODO: Does CSHasRegistrations always extend HasDestruct ?
// TODO: Could here be CSRegistrationsMap instead so we can remove that unimplemented fun from CSRegistrationsList
interface CSHasRegistrations {

    companion object

    val registrations: CSRegistrations
}