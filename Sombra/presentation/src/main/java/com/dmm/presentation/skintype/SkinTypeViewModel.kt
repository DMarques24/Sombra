package com.dmm.presentation.skintype

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SkinTypeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SkinTypeUiState())

    val uiState: StateFlow<SkinTypeUiState> = _uiState.asStateFlow()

    // Só guarda as escolhas, para os botões reagirem ao toque. Guardar o perfil vem depois.
    fun onSelectSkinType(skinType: SkinType) = _uiState.update { it.copy(selectedSkinType = skinType) }

    fun onTogglePlace(place: SunPlace) = _uiState.update {
        val places = if (place in it.selectedPlaces) it.selectedPlaces - place else it.selectedPlaces + place
        it.copy(selectedPlaces = places)
    }
}
