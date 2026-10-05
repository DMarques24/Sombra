package com.dmm.presentation.skintype

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.core.components.SombraStepProgressBar
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R
import com.dmm.presentation.skintype.component.OnboardingFooter
import com.dmm.presentation.skintype.component.SkinTypeGrid
import com.dmm.presentation.skintype.component.SunPlaceChips

@Composable
fun SkinTypeScreen(onNext: () -> Unit,viewModel: SkinTypeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // TODO: ligar Saltar e Seguinte quando houver navegação/lógica
    SkinTypeContent(
        uiState = uiState,
        onSelectSkinType = viewModel::onSelectSkinType,
        onTogglePlace = viewModel::onTogglePlace,
        onNext = onNext,
    )
}

@Composable
fun SkinTypeContent(
    uiState: SkinTypeUiState,
    onSelectSkinType: (SkinType) -> Unit,
    onTogglePlace: (SunPlace) -> Unit,
    modifier: Modifier = Modifier,
    onSkip: () -> Unit = {},
    onNext: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SombraColors.Surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SombraStepProgressBar(
                step = uiState.step,
                totalSteps = uiState.totalSteps,
                description = stringResource(R.string.register_step, uiState.step, uiState.totalSteps),
            )
            Column {
                Text(
                    text = stringResource(R.string.skin_type_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = SombraColors.Text,
                )
                Text(
                    text = stringResource(R.string.skin_type_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = SombraColors.TextMuted,
                )
            }
            SkinTypeGrid(selected = uiState.selectedSkinType, onSelect = onSelectSkinType)
            SunPlaceChips(
                title = stringResource(R.string.skin_type_places_title),
                selected = uiState.selectedPlaces,
                onToggle = onTogglePlace,
            )
        }
        OnboardingFooter(
            skipText = stringResource(R.string.skin_type_skip),
            summary = pluralStringResource(
                R.plurals.skin_type_selected_count,
                uiState.selectedPlaces.size,
                uiState.selectedPlaces.size,
            ),
            nextDescription = stringResource(R.string.skin_type_next),
            onSkip = onSkip,
            onNext = onNext,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun SkinTypeContentPreview() {
    SombraTheme {
        SkinTypeContent(
            uiState = SkinTypeUiState(
                selectedSkinType = SkinType.II,
                selectedPlaces = setOf(SunPlace.BEACH, SunPlace.RUNNING),
            ),
            onSelectSkinType = {},
            onTogglePlace = {},
        )
    }
}
