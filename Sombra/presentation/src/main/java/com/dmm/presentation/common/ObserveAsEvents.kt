package com.dmm.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Recebe eventos que só podem acontecer uma vez (ex.: "entrou, navega para o Início").
 *
 * A ViewModel põe-nos num Channel: se o ecrã rodar a meio de um pedido, o evento fica à espera
 * e é entregue ao ecrã novo. Com um lambda passado à ViewModel, o evento ia para o ecrã antigo
 * (já destruído) e perdia-se. Só recolhe com o ecrã visível, e no Main.immediate para nenhum
 * evento se perder entre frames.
 */
@Composable
fun <T> ObserveAsEvents(events: Flow<T>, onEvent: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(events, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                events.collect { currentOnEvent(it) }
            }
        }
    }
}
