package renetik.android.event.property

class CSSynchronizedVariableImpl<T>(
    value: T,
    val onChange: ((value: T) -> Unit)? = null)
    : CSSynchronizedVariable<T> {

    @get:Synchronized
    @set:Synchronized
    override var value: T = value
        set(value) {
            field = value
            onChange?.invoke(value)
        }

    fun value(value: T) {
        this.value = value
    }
}