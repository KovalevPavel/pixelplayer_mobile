package kov_p.pixelplayer.core_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    body: suspend CoroutineScope.() -> Unit,
    onFailure: (Throwable) -> Unit = {},
    finally: () -> Unit = {},
) {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onFailure(throwable)
        finally()
    }

    viewModelScope.launch(
        context = exceptionHandler + context,
    ) {
        body(this)
        finally()
    }
}
