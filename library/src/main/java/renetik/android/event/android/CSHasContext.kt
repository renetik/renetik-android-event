package renetik.android.event.android

import renetik.android.event.lifecycle.*

import android.content.Context

interface CSHasContext : CSHasRegistrationsHasDestruct {
    val context: Context
}