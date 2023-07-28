package renetik.android.event.common

import renetik.android.core.java.util.concurrent.background
import renetik.android.event.registration.task.CSBackground
import renetik.android.event.registration.task.CSBackground.executor

fun CSHasDestruct.destruct() = onDestruct()
