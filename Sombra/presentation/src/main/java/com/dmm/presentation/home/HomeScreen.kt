package com.dmm.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.core.components.SombraTopBar
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R
import com.dmm.presentation.home.component.TempDetailsComponent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import com.dmm.presentation.home.component.InfoComponent
import com.dmm.presentation.home.component.TimerCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    onOpenTimer: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        onTimerAction = viewModel::onTimerAction,
        onOpenTimer = onOpenTimer,
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onTimerAction: () -> Unit,
    onOpenTimer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SombraColors.HomeGround)
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(SombraColors.SkySunny))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp + 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SombraTopBar(
                title = "Coimbra",
                subtitle = "Hoje, 13:20",
                onNotificationsClick = { /* TODO: abrir notificações */ },
            )
            Image(
                painter = painterResource(R.drawable.weather_sol),
                contentDescription = "Céu limpo",
                modifier = Modifier.size(230.dp),
            )
            Text(
                text = "27°",
                modifier = Modifier.offset(y = (-24).dp),
                style = TextStyle(
                    fontSize = 96.sp,
                    lineHeight = 96.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp,
                ),
                color = SombraColors.OnSky,
            )
            Text(
                text = "Céu limpo · máx 29° mín 16°",
                modifier = Modifier.offset(y = (-16).dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SombraColors.OnSky,
            )
            Spacer(Modifier.height(4.dp))
            TempDetailsComponent(
                uvNumber = "UV 8",
                description = "Muito alto até às 16h",
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-64).dp)
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(28.dp))
                .background(SombraColors.Surface, RoundedCornerShape(28.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            InfoComponent(
                title = "Queimas em ~15 min sem protetor",
                description = "Pele tipo II. Procura sombra entre as 12h e as 16h.",
                backgroundColor = SombraColors.RiskHighContainer,
                titleColor = SombraColors.OnRiskHighContainer,
                descriptionColor = SombraColors.OnRiskHighContainer,
            )
            TimerCard(
                state = uiState.timerCard,
                onAction = onTimerAction,
                onOpen = onOpenTimer,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    SombraTheme {
        HomeContent(
            uiState = HomeUiState(
                timerCard = TimerCardUiState.Running(
                    remainingHours = 1, remainingMinutes = 12,
                    spf = 50, appliedAt = "12:08", progress = 0.6f,
                ),
            ),
            onTimerAction = {},
            onOpenTimer = {},
        )
    }
}
