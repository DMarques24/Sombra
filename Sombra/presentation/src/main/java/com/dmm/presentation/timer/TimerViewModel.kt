package com.dmm.presentation.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.domain.common.DomainResult
import com.dmm.domain.location.usecase.ResolveLocationUseCase
import com.dmm.domain.profile.repository.UserRepository
import com.dmm.domain.sunscreen.model.SunscreenRules
import com.dmm.domain.sunscreen.model.SunscreenSession
import com.dmm.domain.sunscreen.model.TimerStatus
import com.dmm.domain.sunscreen.usecase.ApplySunscreenUseCase
import com.dmm.domain.sunscreen.usecase.LeaveSunUseCase
import com.dmm.domain.sunscreen.usecase.ObserveTimerStatusUseCase
import com.dmm.domain.sunscreen.usecase.ReapplySunscreenUseCase
import com.dmm.domain.weather.model.TodayWeather
import com.dmm.domain.weather.usecase.CalculateSunRiskUseCase
import com.dmm.domain.weather.usecase.GetTodayWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class TimerViewModel @Inject constructor(
    observeTimerStatus: ObserveTimerStatusUseCase,
    userRepository: UserRepository,
    private val getTodayWeather: GetTodayWeatherUseCase,
    private val resolveLocation: ResolveLocationUseCase,
    private val calculateSunRisk: CalculateSunRiskUseCase,
    private val applySunscreen: ApplySunscreenUseCase,
    private val reapplySunscreen: ReapplySunscreenUseCase,
    private val leaveSun: LeaveSunUseCase,
    private val clock: Clock,
) : ViewModel() {

    // O UV de agora, para o aviso e para guardar no histórico.
    // null enquanto o pedido não acaba; depois, today fica null se falhou (sem rede).
    private val weather = MutableStateFlow<LoadedWeather?>(null)

    // O que a pessoa escolheu antes de aplicar. selectedSpf null = ainda não tocou: usa o recomendado.
    private val selection = MutableStateFlow(Selection())

    val uiState: StateFlow<TimerUiState> =
        combine(observeTimerStatus(), weather, selection, userRepository.observeProfile()) { status, loaded, selection, profile ->
            // Fica no esqueleto até o pedido do tempo acabar (bem ou mal), para o UV não aparecer depois
            if (loaded == null) return@combine TimerUiState.Loading
            val weather = loaded.today
            val uvNow = weather?.current?.uvIndex?.roundToInt()
            when (status) {
                TimerStatus.Idle -> {
                    val recommended = calculateSunRisk(
                        skinType = profile.skinTypeOrDefault,
                        uvNow = weather?.current?.uvIndex ?: 0.0,
                        hourlyToday = weather?.hourly.orEmpty(),
                    ).recommendedSpf
                    TimerUiState.Idle(
                        uvNow = uvNow,
                        recommendedSpf = recommended,
                        duration = SunscreenSession(clock.instant(), 0, selection.inWater).duration.toClock(),
                        selectedSpf = selection.spf ?: recommended,
                        goingToWater = selection.inWater,
                    )
                }
                is TimerStatus.Running -> TimerUiState.Running(
                    spf = status.session.spf,
                    appliedAt = status.session.appliedAt.toTime(),
                    remaining = status.remaining.toClock(withSeconds = true),
                    progress = status.progress,
                    notifyAt = status.session.endsAt.toTime(),
                    uvNow = uvNow,
                    inWater = status.session.inWater,
                )
                is TimerStatus.Finished -> TimerUiState.Finished(
                    appliedAt = status.session.appliedAt.toTime(),
                    elapsedMinutes = Duration.between(status.session.appliedAt, clock.instant()).toMinutes().toInt(),
                    finishedMinutesAgo = status.overdue.toMinutes().toInt(),
                    uvNow = uvNow,
                    // Arredonda para cima: com 23 min e 10 s ainda faltam "24 min"
                    autoStopMinutes = ((SunscreenRules.AUTO_STOP.minus(status.overdue).seconds + 59) / 60).toInt(),
                    xpReward = SunscreenRules.XP_REAPPLY_ON_TIME,
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimerUiState.Loading)

    init {
        viewModelScope.launch {
            val coordinates = resolveLocation().coordinates
            val result = getTodayWeather(coordinates.latitude, coordinates.longitude)
            weather.value = LoadedWeather(today = (result as? DomainResult.Success)?.data)
        }
    }

    fun onSelectSpf(spf: Int) = selection.update { it.copy(spf = spf) }

    fun onWaterChange(inWater: Boolean) = selection.update { it.copy(inWater = inWater) }

    fun onApply() {
        val idle = uiState.value as? TimerUiState.Idle ?: return
        viewModelScope.launch {
            applySunscreen(spf = idle.selectedSpf, inWater = idle.goingToWater, uvIndex = idle.uvNow ?: 0)
            selection.value = Selection()
        }
    }

    fun onReapply() {
        viewModelScope.launch { reapplySunscreen(uvIndex = currentUv()) }
    }

    fun onLeftSun() {
        viewModelScope.launch { leaveSun(uvIndex = currentUv()) }
    }

    private fun currentUv(): Int = weather.value?.today?.current?.uvIndex?.roundToInt() ?: 0

    private fun Instant.toTime(): String = atZone(clock.zone).format(TIME_FORMAT)

    private data class LoadedWeather(val today: TodayWeather?)

    private data class Selection(val spf: Int? = null, val inWater: Boolean = false)

    private companion object {
        val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}

// 2:00 ou, com segundos, 1:12:40 (sem toMinutesPart, que só existe a partir do Android 12)
private fun Duration.toClock(withSeconds: Boolean = false): String {
    val hours = toHours()
    val minutes = toMinutes() % 60
    return if (withSeconds) "%d:%02d:%02d".format(hours, minutes, seconds % 60)
    else "%d:%02d".format(hours, minutes)
}
