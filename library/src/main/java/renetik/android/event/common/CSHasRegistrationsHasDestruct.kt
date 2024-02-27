package renetik.android.event.common

import renetik.android.core.lang.CSHasId
import renetik.android.event.registration.CSHasRegistrations

interface CSHasRegistrationsHasDestruct : CSHasRegistrations, CSHasDestruct

interface CSHasRegistrationsHasDestructHasId : CSHasRegistrationsHasDestruct, CSHasId