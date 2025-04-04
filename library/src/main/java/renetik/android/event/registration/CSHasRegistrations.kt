package renetik.android.event.registration

interface CSHasRegistrations {
    companion object

    val registrations: CSRegistrationsMap
}

//TODO: Not ok named in many classes now is meaningless isActive
val CSHasRegistrations?.isActive: Boolean get() = this?.registrations?.isActive != false