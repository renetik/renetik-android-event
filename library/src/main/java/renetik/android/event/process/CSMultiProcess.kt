package renetik.android.event.process

import renetik.android.event.common.CSHasDestruct
import renetik.android.event.util.CSLater.later

@Deprecated("In favor of coroutines")
class CSMultiProcess<Data : Any>(
    parent: CSHasDestruct, function: CSMultiProcess<Data>.() -> Unit
) : CSMultiProcessBase<Data>(parent) {
    init {
        later { function() }
    }
}