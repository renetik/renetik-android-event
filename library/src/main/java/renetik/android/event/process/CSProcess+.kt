package renetik.android.event.process

@Deprecated("In favor of coroutines", ReplaceWith("Coroutines"))
fun <Data : Any, Process : CSProcess<Data>>
        Process.connect(process: CSProcess<Data>) = apply {
    onSuccess { process.success(it.data!!) }.onFailed { process.failed(it) }
}