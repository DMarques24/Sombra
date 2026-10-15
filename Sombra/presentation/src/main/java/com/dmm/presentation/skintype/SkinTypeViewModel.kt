package com.dmm.presentation.skintype

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.domain.profile.model.SkinType
import com.dmm.domain.profile.model.SunPlace
import com.dmm.domain.profile.model.UserProfile
import com.dmm.domain.profile.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkinTypeViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SkinTypeUiState())

    val uiState: StateFlow<SkinTypeUiState> = _uiState.asStateFlow()

    // Evento de uma só vez: o registo acabou, o ecrã segue para o Início
    private val _finished = Channel<Unit>(Channel.BUFFERED)
    val finished: Flow<Unit> = _finished.receiveAsFlow()

    fun onSelectSkinType(skinType: SkinType) = _uiState.update { it.copy(selectedSkinType = skinType) }

    fun onTogglePlace(place: SunPlace) = _uiState.update {
        val places = if (place in it.selectedPlaces) it.selectedPlaces - place else it.selectedPlaces + place
        it.copy(selectedPlaces = places)
    }

    // "Seguinte": guarda as escolhas e acaba o registo
    fun onNext() = finish { state ->
        copy(skinType = state.selectedSkinType, sunPlaces = state.selectedPlaces)
    }

    // "Saltar": acaba o registo sem tipo de pele (as contas usam o tipo II até a pessoa escolher no Perfil)
    fun onSkip() = finish { this }

    private fun finish(
        transform: UserProfile.(SkinTypeUiState) -> UserProfile,
    ) {
        val state = _uiState.value
        viewModelScope.launch {
            userRepository.update { it.transform(state).copy(onboardingCompleted = true) }
            _finished.send(Unit)
        }
    }
}
