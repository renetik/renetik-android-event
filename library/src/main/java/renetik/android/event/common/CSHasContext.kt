package renetik.android.event.common

import android.content.Context

interface CSHasContext : CSHasRegistrationsHasDestroy {
    val context: Context
}