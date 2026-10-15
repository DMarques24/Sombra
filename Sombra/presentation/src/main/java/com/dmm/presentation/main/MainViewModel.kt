package com.dmm.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.core.navigation.HomeRoute
import com.dmm.core.navigation.LoginRoute
import com.dmm.core.navigation.SkinTypeRoute
import com.dmm.domain.auth.repository.AuthRepository
import com.dmm.domain.profile.repository.UserRepository
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel //não teres de criar a ViewModel à mão nem de lhe passar as dependências.
class MainViewModel @Inject constructor( // Diz ao Hilt qual é o construtor que deve usar para criar esta classe.
    authRepository: AuthRepository,
    userRepository: UserRepository,
    sunscreenRepository: SunscreenRepository,
) : ViewModel() {

    /**
     * Por onde a app começa:
     * - sem sessão no Supabase -> Login
     * - com sessão mas sem o registo acabado -> Tipo de pele
     * - com sessão e registo acabado -> Início
     * Fica a null enquanto lê a sessão guardada; o splash fica visível até lá.
     */
    private val _startDestination = MutableStateFlow<Any?>(null)
    val startDestination: StateFlow<Any?> = _startDestination.asStateFlow()

    val isLoading: StateFlow<Boolean> = startDestination
        .map { it == null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    init {
        viewModelScope.launch {
            val user = authRepository.observeUser().first()
            _startDestination.value = when {
                user == null -> LoginRoute
                userRepository.observeProfile().first().onboardingCompleted -> HomeRoute
                else -> SkinTypeRoute
            }
            // Já com o ecrã aberto: traz alterações feitas noutro telemóvel (sem atrasar o arranque)
            if (user != null) {
                userRepository.syncFromRemote()
                sunscreenRepository.syncFromRemote()
            }
        }
    }
}
