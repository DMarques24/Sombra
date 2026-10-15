package com.dmm.presentation.home

import android.content.Context
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
    ) = HomeUiState.Content(
        weather = HomeWeatherUiState(27, 28, WeatherCondition.Clear, isDay = true, uvIndex = 8, uvLevel = UvLevel.VeryHigh),
        risk = SunRiskUiState(21, "II", HourRange(12, 16), HourRange(10, 18), recommendedSpf = 50),
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
