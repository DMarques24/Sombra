package com.dmm.presentation.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.dmm.core.designsystem.SombraTheme
import com.dmm.domain.common.DataError
import com.dmm.domain.weather.model.HourRange
import com.dmm.domain.weather.model.UvLevel
import com.dmm.domain.weather.model.WeatherCondition
import com.dmm.presentation.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Testes de UI do Início (composables sem ViewModel), na JVM com Robolectric
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun content(
        timerCard: TimerCardUiState = TimerCardUiState.Idle,
        isOffline: Boolean = false,
        burnMinutes: Int? = 21,
        uvIndex: Int = 8,
    ) = HomeUiState.Content(
        weather = HomeWeatherUiState(27, 28, WeatherCondition.Clear, isDay = true, uvIndex = uvIndex, uvLevel = UvLevel.from(uvIndex.toDouble())),
        risk = SunRiskUiState(burnMinutes, "II", HourRange(12, 16), HourRange(10, 18), recommendedSpf = 50),
        timerCard = timerCard,
        updatedAt = "13:20",
        locationName = "Coimbra",
        isOffline = isOffline,
        hoursSinceUpdate = 2,
    )

    @Test
    fun `Content mostra a cidade, a temperatura e o aviso de risco`() {
        composeRule.setContent { SombraTheme { HomeContent(content(), onTimerAction = {}, onRefresh = {}, onOpenTimer = {}) } }

        composeRule.onNodeWithText("Coimbra").assertIsDisplayed()
        composeRule.onNodeWithText("27°").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.home_risk_burn_title, 21)).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `120 minutos ou mais mostra 120+ e UV abaixo de 1 mostra sem risco`() {
        var state by mutableStateOf(content(burnMinutes = 167, uvIndex = 2))
        composeRule.setContent { SombraTheme { HomeContent(state, onTimerAction = {}, onRefresh = {}, onOpenTimer = {}) } }

        composeRule.onNodeWithText(context.getString(R.string.home_risk_burn_title_cap, 120)).performScrollTo().assertIsDisplayed()

        state = content(burnMinutes = null, uvIndex = 0)
        composeRule.onNodeWithText(context.getString(R.string.home_risk_low_title)).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `sem internet mostra a pílula e o Atualizar chama o refresh`() {
        var refreshes = 0
        composeRule.setContent {
            SombraTheme { HomeContent(content(isOffline = true), onTimerAction = {}, onRefresh = { refreshes++ }, onOpenTimer = {}) }
        }

        composeRule.onNodeWithText(context.getString(R.string.home_offline, 2)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.home_offline_refresh)).performClick()
        assertEquals(1, refreshes)
    }

    @Test
    fun `o botão do cartão do temporizador chama a ação`() {
        var actions = 0
        composeRule.setContent {
            SombraTheme { HomeContent(content(), onTimerAction = { actions++ }, onRefresh = {}, onOpenTimer = {}) }
        }

        composeRule.onNodeWithText(context.getString(R.string.timer_card_apply_button)).performScrollTo().performClick()
        assertEquals(1, actions)
    }

    @Test
    fun `Error mostra a mensagem e o Tenta outra vez chama o retry`() {
        var retries = 0
        composeRule.setContent { SombraTheme { HomeError(DataError.NoConnection, onRetry = { retries++ }) } }

        composeRule.onNodeWithText(context.getString(R.string.error_no_connection_title)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.action_retry)).performClick()
        assertEquals(1, retries)
    }
}
