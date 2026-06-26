package renetik.android.event.property

import renetik.android.event.change.*
import renetik.android.event.dispatch.*
import renetik.android.event.lifecycle.*
import renetik.android.event.registration.*
import renetik.android.event.registration.CSRegistration.Companion.CSRegistration

//dec should return new value so not valid for property
//operator fun CSProperty<Int>.dec(): CSProperty<Int> = apply {
//    value--
//}
//inc should return new value so not valid for property
//operator fun CSProperty<Int>.inc(): CSProperty<Int> = apply {
//    value++
//}