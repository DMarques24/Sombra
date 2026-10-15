package com.dmm.presentation.timer

import app.cash.turbine.test
import com.dmm.domain.common.DataError
import com.dmm.domain.location.usecase.ResolveLocationUseCase
import com.dmm.domain.profile.model.SkinType
import com.dmm.domain.profile.model.UserProfile
import com.dmm.domain.sunscreen.model.SunscreenEvent
import com.dmm.domain.sunscreen.usecase.ApplySunscreenUseCase
import com.dmm.domain.sunscreen.usecase.LeaveSunUseCase
import com.dmm.domain.sunscreen.usecase.ObserveTimerStatusUseCase
import com.dmm.domain.sunscreen.usecase.ReapplySunscreenUseCase
import com.dmm.domain.weather.usecase.CalculateSunRiskUseCase
import com.dmm.domain.weather.usecase.GetTodayWeatherUseCase
import com.dmm.presentation.testing.FakeLocationRepository
import com.dmm.presentation.testing.FakeReminderScheduler
import com.dmm.presentation.testing.FakeSunscreenRepository
import com.dmm.presentation.testing.FakeUserRepository
import com.dmm.presentation.testing.FakeUvHistoryRepository
import com.dmm.presentation.testing.FakeWeatherRepository
import com.dmm.presentation.testing.MainDispatcherRule
import com.dmm.presentation.testing.MutableClock
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

// O temporizador guarda a hora de aplicação; o relógio fixo/avançável testa cada transição
class TimerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = MutableClock(Instant.parse("2026-07-20T12:00:00Z"))
    private val weather = FakeWeatherRepository(FakeWeatherRepository.success(LocalDate.of(2026, 7, 20), clock.instant()))
    private val users = FakeUserRepository(UserProfile(skinType = SkinType.II))
    private val sunscreen = FakeSunscreenRepository(clock)
    private val reminders = FakeReminderScheduler()

    private fun viewModel() = TimerViewModel(
        observeTimerStatus = ObserveTimerStatusUseCase(sunscreen, clock),
        userRepository = users,
        getTodayWeather = GetTodayWeatherUseCase(weather, FakeUvHistoryRepository(), clock),
        resolveLocation = ResolveLocationUseCase(FakeLocationRepository(), users),
        calculateSunRisk = CalculateSunRiskUseCase(),
        applySunscreen = ApplySunscreenUseCase(sunscreen, reminders, clock),
        reapplySunscreen = ReapplySunscreenUseCase(sunscreen, reminders, clock),
        leaveSun = LeaveSunUseCase(sunscreen, reminders, clock),
        clock = clock,
    )

    // O temporizador recalcula a cada segundo: avançar o relógio e o tempo virtual dá o novo estado
    private fun tick(duration: Duration) {
        clock.advance(duration)
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(1_001)
    }

    @Test
    fun `sem temporizador mostra Idle com o UV de agora e o SPF recomendado`() = runTest {
        viewModel().uiState.test {
            val idle = awaitItem() as TimerUiState.Idle
            assertEquals(8, idle.uvNow)
            assertEquals(50, idle.recommendedSpf)
            assertEquals(50, idle.selectedSpf)
            assertEquals("2:00", idle.duration)
        }
    }

    @Test
    fun `sem rede fica Idle sem UV em vez de ficar preso no Loading`() = runTest {
        weather.result = FakeWeatherRepository.failure(DataError.NoConnection)
        viewModel().uiState.test {
            val idle = awaitItem() as TimerUiState.Idle
            assertNull(idle.uvNow)
        }
    }

    @Test
    fun `na água o temporizador dura 40 minutos`() = runTest {
        val vm = viewModel()
        vm.uiState.test {
            skipItems(1)
            vm.onWaterChange(true)
            assertEquals("0:40", (awaitItem() as TimerUiState.Idle).duration)
        }
    }

    @Test
    fun `Idle - Running - Finished - Reapliquei - Já saí do sol`() = runTest {
        val vm = viewModel()
        vm.uiState.test {
            skipItems(1)

            vm.onSelectSpf(30)
            skipItems(1)
            vm.onApply()
            val running = awaitItem() as TimerUiState.Running
            assertEquals(30, running.spf)
            assertEquals("12:00", running.appliedAt)
            assertEquals("14:00", running.notifyAt)
            assertEquals("2:00:00", running.remaining)
            assertEquals(1f, running.progress)

            // 2h06 depois: acabou há 6 minutos
            tick(Duration.ofMinutes(126))
            val finished = expectMostRecentItem() as TimerUiState.Finished
            assertEquals(6, finished.finishedMinutesAgo)
            assertEquals(126, finished.elapsedMinutes)
            assertEquals(24, finished.autoStopMinutes)

            // Reapliquei: volta a Running e conta como reaplicação a tempo
            vm.onReapply()
            assertTrue(awaitItem() is TimerUiState.Running)
            assertEquals(SunscreenEvent.Type.ReappliedOnTime, sunscreen.history.value.last().type)

            // Já saí do sol: volta a Idle e o aviso é cancelado
            vm.onLeftSun()
            assertTrue(awaitItem() is TimerUiState.Idle)
            assertNull(reminders.scheduledAt)
        }
    }

    @Test
    fun `sem resposta 30 minutos depois de acabar, o temporizador termina sozinho`() = runTest {
        val vm = viewModel()
        vm.uiState.test {
            skipItems(1)
            vm.onApply()
            skipItems(1)

            tick(Duration.ofHours(2).plusMinutes(31))

            assertTrue(expectMostRecentItem() is TimerUiState.Idle)
        }
    }
}
