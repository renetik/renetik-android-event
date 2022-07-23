package renetik.android.event.registration

import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

fun CSRegistration(vararg registrations: CSRegistration) = CSRegistration(
    isActive = true,
    onResume = { registrations.forEach { it.resume() } },
    onPause = { registrations.forEach { it.pause() } },
    onCancel = { registrations.forEach { it.cancel() } })