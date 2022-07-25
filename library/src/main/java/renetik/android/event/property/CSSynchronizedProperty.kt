package renetik.android.event.property

import renetik.android.core.lang.variable.CSSynchronizedVariable

interface CSSynchronizedProperty<T> : CSSynchronizedVariable<T>, CSProperty<T>