package renetik.android.event.registration

import renetik.android.core.lang.tuples.CSSeventuple

fun <Argument1, Argument2, Argument3, Argument4, Argument5, Argument6, Argument7>
        CSSeventuple<CSHasChangeValue<Argument1>, CSHasChangeValue<Argument2>,
                CSHasChangeValue<Argument3>, CSHasChangeValue<Argument4>,
                CSHasChangeValue<Argument5>, CSHasChangeValue<Argument6>,
                CSHasChangeValue<Argument7>>.action(
    onChange: (
        Argument1, Argument2, Argument3, Argument4,
        Argument5, Argument6, Argument7
    ) -> Unit,
): CSRegistration = listOf(first, second, third, fourth,
    fifth, sixth, seventh).action {
    onChange(first.value, second.value, third.value,
        fourth.value, fifth.value, sixth.value, seventh.value)
}