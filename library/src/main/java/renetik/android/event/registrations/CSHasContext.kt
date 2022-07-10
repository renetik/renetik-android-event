package renetik.android.event.registrations

import android.content.Context

interface CSHasContext : CSHasRegistrationsHasDestroy {
    val context: Context
}