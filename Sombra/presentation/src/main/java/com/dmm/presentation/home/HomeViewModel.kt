package com.dmm.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.core.components.LocationPermissionStatus
import com.dmm.domain.location.model.Coordinates
import com.dmm.domain.location.repository.LocationRepository
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import com.dmm.domain.common.DataError
import com.dmm.domain.common.DomainResult
import com.dmm.domain.sunscreen.usecase.ObserveTimerStatusUseCase
import com.dmm.domain.weather.usecase.GetCurrentWeatherUseCase
import com.dmm.presentation.home.mapper.toHomeWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeTimerStatus: ObserveTimerStatusUseCase,
    getCurrentWeather: GetCurrentWeatherUseCase,
    private val sunscreenRepository: SunscreenRepository,
    private val locationRepository: LocationRepository,
) : ViewModel() {

    // Cada vez que muda, o flow de baixo recomeça do zero (é assim que o "Tenta outra vez" funciona)
    private val retryTrigger = MutableStateFlow(0)

    // Onde ir buscar o tempo. Fica a null até a pessoa responder ao pedido de permissão,
    // e enquanto for null o ecrã fica no esqueleto
    private val location = MutableStateFlow<LocationState?>(null)

    // O pedido do sistema só aparece uma vez; depois disso é o cartão no ecrã que volta a pedir
    private var hasAskedForLocation = false

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> =
        combine(retryTrigger, location.filterNotNull()) { _, location -> location }
            .flatMapLatest { location ->
                val coordinates = location.coordinates
                // Pede o tempo uma vez; depois o timer continua a atualizar o ecrã a cada segundo
                flow { emit(getCurrentWeather(coordinates.latitude, coordinates.longitude)) }
                    .flatMapLatest { result ->
                        when (result) {
                            is DomainResult.Failure -> flowOf(HomeUiState.Error(result.error))
                            is DomainResult.Success -> {
                                val weather = result.data.toHomeWeather()
                                observeTimerStatus().map<_, HomeUiState> { status ->
                                    HomeUiState.Content(
                                        weather = weather,
                                        timerCard = status.toCard(uvNow = weather.uvIndex),
                                        isDefaultLocation = coordinates == Coordinates.Default,
                                        locationPrompt = location.prompt,
                                    )
                                }
                            }
                        }
                    }
                    .onStart { emit(HomeUiState.Loading) }
                    // Rede de segurança: os erros da API já chegam como DomainResult.Failure
                    .catch { emit(HomeUiState.Error(DataError.Unknown)) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading)

    // Ecrã de erro: volta ao esqueleto e tenta carregar tudo outra vez
    fun onRetry() {
        retryTrigger.value++
    }

    fun onRefresh() {
        retryTrigger.value++
    }

    fun onTimerAction() {
        val content = uiState.value as? HomeUiState.Content ?: return
        viewModelScope.launch {
            when (content.timerCard) {
                TimerCardUiState.Idle -> sunscreenRepository.start(spf = 50, inWater = false)
                is TimerCardUiState.Running,
                is TimerCardUiState.Finished -> sunscreenRepository.reapply()
            }
        }
    }

    // true só na primeira vez: o ecrã usa isto para decidir se mostra o pedido do sistema
    fun shouldAskForLocation(): Boolean {
        if (hasAskedForLocation) return false
        hasAskedForLocation = true
        return true
    }

    fun onLocationPermissionResult(status: LocationPermissionStatus) {
        // Já temos a localização do GPS: voltar ao ecrã não precisa de a pedir outra vez
        if (status == LocationPermissionStatus.Granted && location.value?.prompt == LocationPrompt.None) return

        viewModelScope.launch {
            val coordinates = when (status) {
                LocationPermissionStatus.Granted -> locationRepository.getCurrentLocation()
                LocationPermissionStatus.Denied,
                LocationPermissionStatus.PermanentlyDenied -> null
            } ?: Coordinates.Default

            location.value = LocationState(
                coordinates = coordinates,
                prompt = when (status) {
                    LocationPermissionStatus.Granted -> LocationPrompt.None
                    LocationPermissionStatus.Denied -> LocationPrompt.AskAgain
                    LocationPermissionStatus.PermanentlyDenied -> LocationPrompt.OpenSettings
                },
            )
        }
    }

    private data class LocationState(
        val coordinates: Coordinates,
        val prompt: LocationPrompt,
    )
}
