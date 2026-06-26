package renetik.android.event.property

import renetik.android.event.change.*
import renetik.android.event.dispatch.*
import renetik.android.event.lifecycle.*
import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun CSProperty<Float>.min(value: Float) = apply {
    if (this.value < value) this.value(value, fire = false)
}

fun CSProperty<Float>.max(value: Float) = apply {
    if (this.value > value) this.value(value, fire = false)
}

fun CSProperty<Float>.range(range: ClosedRange<Float>) = apply {
    min(range.start).max(range.endInclusive)
}