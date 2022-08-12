package renetik.android.event.registration

import renetik.android.core.java.lang.isMain
import renetik.android.core.lang.CSMainHandler.postOnMain
import renetik.android.core.lang.CSMainHandler.removePosted

interface CSHasRegistrations {
    companion object

    val registrations: CSRegistrations


}