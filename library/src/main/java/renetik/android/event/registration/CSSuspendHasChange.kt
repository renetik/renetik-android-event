package renetik.android.event.registration

interface CSSuspendHasChange<Argument> {
    fun onChange(function: suspend (Argument) -> Unit): CSRegistration
}