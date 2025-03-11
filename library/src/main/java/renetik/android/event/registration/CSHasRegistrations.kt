package renetik.android.event.registration

interface CSHasRegistrations {
    companion object

    val registrations: CSRegistrationsMap
}

val CSHasRegistrations?.isActive: Boolean get() = this?.registrations?.isActive != false