package com.dmm.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel //não teres de criar a ViewModel à mão nem de lhe passar as dependências.
class MainViewModel @Inject constructor() : ViewModel(){ // Diz ao Hilt qual é o construtor que deve usar para criar esta classe.


    /**
     * MutableStateFlow(true)
     * Uma "caixa" que guarda um valor e avisa quem está a observar sempre que ele muda.
     *
     * - "State": tem sempre um valor atual (lês com .value)
     * - "Flow": quem observa recebe automaticamente cada valor novo
     * - "Mutable": pode ser alterado (_isLoading.value = false)
     *
     * Começa a `true` porque, no momento em que a app abre, ainda está a carregar:
     * ainda não sabemos se há sessão iniciada. O splash fica visível enquanto for true.
     *
     * É `private` e tem o underscore (_) por convenção: só a ViewModel o pode alterar.
     */
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    init {
        viewModelScope.launch {
            delay(2000)   //delay para simular verificação de autenticação
            _isLoading.value = false
        }
    }
}