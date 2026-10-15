package com.dmm.presentation.skintype

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.domain.profile.model.SkinType
import com.dmm.domain.profile.model.SunPlace
import com.dmm.domain.profile.model.UserProfile
import com.dmm.domain.profile.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun onSelectSkinType(skinType: SkinType) = _uiState.update { it.copy(selectedSkinType = skinType) }

    fun onTogglePlace(place: SunPlace) = _uiState.update {
        val places = if (place in it.selectedPlaces) it.selectedPlaces - place else it.selectedPlaces + place
        it.copy(selectedPlaces = places)
    }

    // "Seguinte": guarda as escolhas e acaba o registo
    fun onNext(onDone: () -> Unit) = finish(onDone) { state ->
        copy(skinType = state.selectedSkinType, sunPlaces = state.selectedPlaces)
    }

    // "Saltar": acaba o registo sem tipo de pele (as contas usam o tipo II até a pessoa escolher no Perfil)
    fun onSkip(onDone: () -> Unit) = finish(onDone) { this }

    private fun finish(
        onDone: () -> Unit,
        transform: UserProfile.(SkinTypeUiState) -> UserProfile,
    ) {
        val state = _uiState.value
        viewModelScope.launch {
            userRepository.update { it.transform(state).copy(onboardingCompleted = true) }
            onDone()
        }
    }
}
