package renetik.android.event.registration

import renetik.android.core.lang.value.CSValue

interface CSHasChangeValue<T> : CSValue<T>, CSHasChange<T> {
    companion object
}