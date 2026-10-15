package com.dmm.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.core.components.LocationPermissionStatus
import com.dmm.domain.common.DataError
import com.dmm.domain.common.DomainResult
import com.dmm.domain.location.model.ResolvedLocation
import com.dmm.domain.location.usecase.ResolveLocationUseCase
import com.dmm.domain.profile.repository.UserRepository
import com.dmm.domain.sunscreen.usecase.ApplySunscreenUseCase
import com.dmm.domain.sunscreen.usecase.ObserveTimerStatusUseCase
import com.dmm.domain.sunscreen.usecase.ReapplySunscreenUseCase
import com.dmm.domain.weather.model.TodayWeather
import com.dmm.domain.weather.usecase.CalculateSunRiskUseCase
import com.dmm.domain.weather.usecase.GetTodayWeatherUseCase
import com.dmm.presentation.common.roman
import com.dmm.presentation.home.mapper.toHomeWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeTimerStatus: ObserveTimerStatusUseCase,
    userRepository: UserRepository,
    private val getTodayWeather: GetTodayWeatherUseCase,
    private val calculateSunRisk: CalculateSunRiskUseCase,
    private val resolveLocation: ResolveLocationUseCase,
    private val applySunscreen: ApplySunscreenUseCase,
    private val reapplySunscreen: ReapplySunscreenUseCase,
    private val clock: Clock,
) : ViewModel() {

    // O tempo carregado (ou a carregar, ou o erro). O timer e o perfil juntam-se a isto em baixo.
    private val weather = MutableStateFlow<WeatherState>(WeatherState.Loading)
    private val isRefreshing = MutableStateFlow(false)
    private var loadJob: Job? = null

    // null até a pessoa responder ao pedido de permissão: até lá o ecrã fica no esqueleto
    private var locationPrompt: LocationPrompt? = null

    // O pedido do sistema só aparece uma vez; depois disso é o cartão no ecrã que volta a pedir
    private var hasAskedForLocation = false

    // O timer atualiza o ecrã a cada segundo; o tempo só muda quando se volta a pedir à API
    val uiState: StateFlow<HomeUiState> =
        combine(weather, observeTimerStatus(), userRepository.observeProfile(), isRefreshing) { weather, status, profile, refreshing ->
            when (weather) {
                WeatherState.Loading -> HomeUiState.Loading
                is WeatherState.Failed -> HomeUiState.Error(weather.error)
                is WeatherState.Loaded -> {
                    val today = weather.today
                    val risk = calculateSunRisk(profile.skinTypeOrDefault, today.current.uvIndex, today.hourly)
                    // A hora no sítio do tempo (as horas da previsão são as de lá)
                    val nowHour = LocalDateTime.ofInstant(clock.instant(), today.zone).hour
                    val uvNow = today.current.toHomeWeather().uvIndex
                    HomeUiState.Content(
                        weather = today.current.toHomeWeather().copy(
                            uvUntilHour = risk.protectHours?.endHour?.takeIf { nowHour < it && today.current.uvIndex >= 3 },
                        ),
                        risk = SunRiskUiState(
                            burnMinutes = risk.burnMinutes,
                            skinType = profile.skinTypeOrDefault.roman,
                            shadeHours = risk.shadeHours?.takeIf { nowHour < it.endHour },
                            protectHours = risk.protectHours?.takeIf { nowHour < it.endHour },
                            recommendedSpf = risk.recommendedSpf,
                        ),
                        timerCard = status.toCard(uvNow = uvNow),
                        updatedAt = today.fetchedAt.atZone(clock.zone).format(TIME_FORMAT),
                        locationName = weather.location.cityName,
                        isOffline = today.isFromCache,
                        hoursSinceUpdate = Duration.between(today.fetchedAt, clock.instant()).toHours().toInt(),
                        isRefreshing = refreshing,
                        locationPrompt = weather.prompt,
                    )
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading)

    // Ecrã de erro: volta ao esqueleto e tenta carregar tudo outra vez
    fun onRetry() = load(showSkeleton = true)

    // "Atualizar" no aviso de sem internet: mantém os dados no ecrã enquanto tenta
    fun onRefresh() = load(showSkeleton = false)

    fun onTimerAction() {
        val content = uiState.value as? HomeUiState.Content ?: return
        val uvNow = content.weather.uvIndex
        viewModelScope.launch {
            when (content.timerCard) {
                // O mesmo SPF que o ecrã do Protetor recomenda
                TimerCardUiState.Idle ->
                    applySunscreen(spf = content.risk.recommendedSpf, inWater = false, uvIndex = uvNow)
                is TimerCardUiState.Running,
                is TimerCardUiState.Finished -> reapplySunscreen(uvIndex = uvNow)
            }
        }
    }

    // true só na primeira vez: o ecrã usa isto para decidir se mostra o pedido do sistema
    fun shouldAskForLocation(): Boolean {
        if (hasAskedForLocation) return false
        hasAskedForLocation = true
        return true
    }

    // Chamado ao abrir o ecrã, ao voltar à app e depois de cada resposta ao pedido de permissão
    fun onLocationPermissionResult(status: LocationPermissionStatus) {
        val prompt = when (status) {
            LocationPermissionStatus.Granted -> LocationPrompt.None
            LocationPermissionStatus.Denied -> LocationPrompt.AskAgain
            LocationPermissionStatus.PermanentlyDenied -> LocationPrompt.OpenSettings
        }
        val current = weather.value
        // Nada mudou e os dados ainda são frescos: voltar ao ecrã não pede o tempo outra vez
        if (prompt == locationPrompt && current is WeatherState.Loaded && !current.isStale()) return
        locationPrompt = prompt
        load(showSkeleton = current !is WeatherState.Loaded)
    }

    private fun load(showSkeleton: Boolean) {
        val prompt = locationPrompt ?: return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (showSkeleton) weather.value = WeatherState.Loading else isRefreshing.value = true
            try {
                val location = resolveLocation(useGps = prompt == LocationPrompt.None)
                val coordinates = location.coordinates
                when (val result = getTodayWeather(coordinates.latitude, coordinates.longitude)) {
                    is DomainResult.Success ->
                        weather.value = WeatherState.Loaded(location, result.data, prompt, loadedAt = clock.instant())
                    // A atualizar e falhou: fica com os dados que já estavam no ecrã
                    is DomainResult.Failure ->
                        if (showSkeleton || weather.value !is WeatherState.Loaded) {
                            weather.value = WeatherState.Failed(result.error)
                        }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Rede de segurança: os erros da API já chegam como DomainResult.Failure
                weather.value = WeatherState.Failed(DataError.Unknown)
            } finally {
                isRefreshing.value = false
            }
        }
    }

    private fun WeatherState.Loaded.isStale() =
        Duration.between(loadedAt, clock.instant()) > STALE_AFTER || today.isFromCache

    private sealed interface WeatherState {
        data object Loading : WeatherState
        data class Failed(val error: DataError) : WeatherState
        data class Loaded(
            val location: ResolvedLocation,
            val today: TodayWeather,
            val prompt: LocationPrompt,
            val loadedAt: Instant,
        ) : WeatherState
    }

    private companion object {
        val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val STALE_AFTER: Duration = Duration.ofMinutes(30)
    }
}
