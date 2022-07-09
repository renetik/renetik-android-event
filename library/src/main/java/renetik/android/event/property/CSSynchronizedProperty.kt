package renetik.android.event.property

import renetik.android.core.lang.CSSynchronizedValue
import renetik.android.core.lang.property.CSVariable

interface CSSynchronizedProperty<T> : CSSynchronizedValue<T>, CSVariable<T>

interface CSSynchronizedEventProperty<T> : CSSynchronizedProperty<T>, CSEventProperty<T>