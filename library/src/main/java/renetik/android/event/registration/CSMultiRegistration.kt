package renetik.android.event.registration

import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun CSMultiRegistration(
    vararg registrations: CSRegistration) = CSRegistration(
    onResume = { registrations.forEach { it.resume() } },
    onPause = { registrations.forEach { it.pause() } },
    onCancel = { registrations.forEach { it.cancel() } })