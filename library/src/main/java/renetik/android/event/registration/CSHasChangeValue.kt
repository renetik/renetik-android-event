package renetik.android.event.registration

import renetik.android.core.lang.value.CSValue
import renetik.android.event.property.action

interface CSHasChangeValue<T> : CSValue<T>, CSHasChange<T> {
    companion object {

    }
}

inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.onChange(
    crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>,
    crossinline onChange: (ChildValue) -> Unit
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = onChange {
        childRegistration?.cancel()
        childRegistration = child(value).action { onChange(it) }
    }
    return CSRegistration.CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}


inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.action(
    crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>,
    crossinline onChange: (ChildValue) -> Unit
): CSRegistration {
    onChange(child(value).value)
    return onChange(child, onChange)
}

//TODO: Test this !!! Not used at all maybe not working good..
inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.onChangeNullableChild(
    crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>?,
    crossinline onChange: (ChildValue) -> Unit
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = action { parentValue ->
        childRegistration?.cancel()
        childRegistration = child(parentValue)?.onChange { childValue ->
            onChange(childValue)
        }
    }
    return CSRegistration.CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}

//TODO: Test this !!!
inline fun <ParentValue, ChildValue> CSHasChangeValue<ParentValue>.actionNullableChild(
    crossinline child: (ParentValue) -> CSHasChangeValue<ChildValue>?,
    crossinline onChange: (ChildValue) -> Unit
): CSRegistration {
    var childRegistration: CSRegistration? = null
    val parentRegistration = action { parentValue ->
        childRegistration?.cancel()
        childRegistration = child(parentValue)?.action { childValue ->
            onChange(childValue)
        }
    }
    return CSRegistration.CSRegistration(isActive = true, onCancel = {
        parentRegistration.cancel()
        childRegistration?.cancel()
    })
}