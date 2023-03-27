package renetik.android.event.common

import android.content.Context

interface CSHasContext : CSHasRegistrationsHasDestruct {
    val context: Context
}