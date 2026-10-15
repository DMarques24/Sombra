package com.dmm.presentation.home

import app.cash.turbine.test
import com.dmm.core.components.LocationPermissionStatus
import com.dmm.domain.common.DataError
import com.dmm.domain.location.usecase.ResolveLocationUseCase
import com.dmm.domain.profile.model.SkinType
import com.dmm.domain.profile.model.UserProfile
import com.dmm.domain.sunscreen.usecase.ApplySunscreenUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val today = LocalDate.of(2026, 7, 20)
    private val clock = MutableClock(Instant.parse("2026-07-20T12:00:00Z"))
    private val weather = FakeWeatherRepository(FakeWeatherRepository.success(today, clock.instant()))
    private val location = FakeLocationRepository()
    private val users = FakeUserRepository(UserProfile(name = "Ana", city = "Coimbra", skinType = SkinType.II))
    private val sunscreen = FakeSunscreenRepository(clock)
    private val reminders = FakeReminderScheduler()

    private fun viewModel() = HomeViewModel(
        observeTimerStatus = ObserveTimerStatusUseCase(sunscreen, clock),
        userRepository = users,
        getTodayWeather = GetTodayWeatherUseCase(weather, FakeUvHistoryRepository(), clock),
        calculateSunRisk = CalculateSunRiskUseCase(),
        resolveLocation = ResolveLocationUseCase(location, users),
        applySunscreen = ApplySunscreenUseCase(sunscreen, reminders, clock),
        reapplySunscreen = ReapplySunscreenUseCase(sunscreen, reminders, clock),
        clock = clock,
    )

    @Test
    fun `fica em Loading até a pessoa responder ao pedido de localização`() = runTest {
        viewModel().uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            expectNoEvents()
            assertEquals(0, weather.calls)
        }
    }

    @Test
    fun `com permissão e dados passa de Loading a Content`() = runTest {
        val vm = viewModel()
        vm.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            vm.onLocationPermissionResult(LocationPermissionStatus.Granted)

            val content = awaitItem() as HomeUiState.Content
            assertEquals(27, content.weather.temperature)
            assertEquals(8, content.weather.uvIndex)
            assertEquals("Coimbra", content.locationName)
            assertFalse(content.isOffline)
            assertEquals(LocationPrompt.None, content.locationPrompt)
            assertEquals(21, content.risk.burnMinutes)          // pele II com UV 8
            assertEquals(TimerCardUiState.Idle, content.timerCard)
        }
    }

    @Test
    fun `sem dados e com o pedido a falhar mostra Error, e o retry recupera`() = runTest {
        weather.result = FakeWeatherRepository.failure(DataError.Server)
        val vm = viewModel()
        vm.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            vm.onLocationPermissionResult(LocationPermissionStatus.Granted)
            assertEquals(HomeUiState.Error(DataError.Server), awaitItem())

            weather.result = FakeWeatherRepository.success(today, clock.instant())
            weather.delayMillis = 1_000
            vm.onRetry()
            assertEquals(HomeUiState.Loading, awaitItem())          // o erro dá lugar ao esqueleto
            assertTrue(awaitItem() is HomeUiState.Content)
        }
    }

    @Test
    fun `dados da cache (sem rede) ficam em Content com isOffline e as horas desde a atualização`() = runTest {
        val fetchedAt = clock.instant().minus(Duration.ofHours(3))
        weather.result = FakeWeatherRepository.success(today, fetchedAt, isFromCache = true)
        val vm = viewModel()
        vm.uiState.test {
            skipItems(1)
            vm.onLocationPermissionResult(LocationPermissionStatus.Granted)

            val content = awaitItem() as HomeUiState.Content
            assertTrue(content.isOffline)
            assertEquals(3, content.hoursSinceUpdate)
        }
    }

    @Test
    fun `atualizar sem rede mantém os dados no ecrã em vez de mostrar o erro`() = runTest {
        val vm = viewModel()
        vm.uiState.test {
            skipItems(1)
            vm.onLocationPermissionResult(LocationPermissionStatus.Granted)
            val before = awaitItem() as HomeUiState.Content

            weather.result = FakeWeatherRepository.failure(DataError.NoConnection)
            weather.delayMillis = 1_000
            vm.onRefresh()

            // Enquanto atualiza: os mesmos dados, com o indicador na pílula (nunca o ecrã de erro)
            val refreshing = awaitItem() as HomeUiState.Content
            assertTrue(refreshing.isRefreshing)
            assertEquals(before.weather, refreshing.weather)
            // Falhou: volta exatamente ao que estava
            assertEquals(before, awaitItem())
        }
    }

    @Test
    fun `sem permissão usa a cidade do perfil ou Coimbra e pede para voltar a perguntar`() = runTest {
        val vm = viewModel()
        vm.uiState.test {
            skipItems(1)
            vm.onLocationPermissionResult(LocationPermissionStatus.Denied)

            val content = awaitItem() as HomeUiState.Content
            assertEquals(LocationPrompt.AskAgain, content.locationPrompt)
            assertEquals("Coimbra", content.locationName)       // o fake não encontra a cidade: fica a predefinida
        }
    }

    @Test
    fun `aplicar protetor no cartão começa o temporizador com o SPF recomendado e agenda o aviso`() = runTest {
        val vm = viewModel()
        vm.uiState.test {
            skipItems(1)
            vm.onLocationPermissionResult(LocationPermissionStatus.Granted)
            skipItems(1)

            vm.onTimerAction()

            val running = (awaitItem() as HomeUiState.Content).timerCard as TimerCardUiState.Running
            assertEquals(50, running.spf)                       // pele II com UV alto
            assertEquals(2, running.remainingHours)
            assertEquals(clock.instant().plus(Duration.ofHours(2)), reminders.scheduledAt)
        }
    }
}
