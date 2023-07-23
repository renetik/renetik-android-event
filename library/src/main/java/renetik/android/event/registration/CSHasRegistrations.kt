package renetik.android.event.registration

//TODO: Does CSHasRegistrations always extend HasDestruct ?
interface CSHasRegistrations {
    companion object

    val registrations: CSRegistrations
}