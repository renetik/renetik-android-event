package renetik.android.event.common

import renetik.android.core.lang.CSHasId
import renetik.android.event.registration.CSHasRegistrations

interface CSHasRegistrationsHasDestruct : CSHasRegistrations, CSHasDestruct

@Deprecated("Try to get rid of this.. ")
interface CSHasRegistrationsHasDestructHasId : CSHasRegistrationsHasDestruct, CSHasId