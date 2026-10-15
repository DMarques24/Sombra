package com.dmm.presentation.timer

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Testes de UI do Protetor (TimerContent, sem ViewModel), na JVM com Robolectric
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TimerScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `Idle mostra o UV e o botão Apliquei chama onApply`() {
        var applies = 0
        composeRule.setContent { SombraTheme { TimerContent(TimerUiState.Idle(uvNow = 8), onApply = { applies++ }) } }

        composeRule.onNodeWithText(context.getString(R.string.timer_uv_now_title, 8)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.timer_apply_button)).performClick()
        assertEquals(1, applies)
    }

    @Test
    fun `escolher outro SPF chama onSelectSpf com o número`() {
        var selected: Int? = null
        composeRule.setContent { SombraTheme { TimerContent(TimerUiState.Idle(), onSelectSpf = { selected = it }) } }

        composeRule.onNodeWithText("30").performScrollTo().performClick()
        assertEquals(30, selected)
        composeRule.onNodeWithText("50+").performScrollTo().performClick()
        assertEquals(60, selected)                              // "50+" fica guardado como 60
    }

    @Test
    fun `Running mostra o tempo que falta e os botões Reapliquei e Já saí do sol`() {
        var reapplies = 0
        var leaves = 0
        composeRule.setContent {
            SombraTheme { TimerContent(TimerUiState.Running(remaining = "1:12:40"), onReapply = { reapplies++ }, onLeftSun = { leaves++ }) }
        }

        composeRule.onNodeWithText("1:12:40").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.timer_reapply_button)).performClick()
        composeRule.onNodeWithText(context.getString(R.string.timer_left_sun_button)).performClick()
        assertEquals(1, reapplies)
        assertEquals(1, leaves)
    }

    @Test
    fun `Finished mostra Hora de reaplicar e o XP do botão`() {
        composeRule.setContent { SombraTheme { TimerContent(TimerUiState.Finished(xpReward = 20)) } }

        composeRule.onNodeWithText(context.getString(R.string.timer_title_finished)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.timer_reapply_xp_button, 20)).assertIsDisplayed()
    }
}
