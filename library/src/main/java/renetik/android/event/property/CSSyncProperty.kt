package renetik.android.event.property

import renetik.android.core.lang.variable.CSSynchronizedVariable

interface CSSyncProperty<T> : CSSynchronizedVariable<T>, CSProperty<T>