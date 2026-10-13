package com.dmm.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R
import com.dmm.presentation.profile.component.LevelProgress
import com.dmm.presentation.profile.component.PatchesSection
import com.dmm.presentation.profile.component.ProfileHeader
import com.dmm.presentation.profile.component.StatCardsRow

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // TODO: abrir as definições quando o ecrã existir
    ProfileContent(uiState = uiState)
}

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SombraColors.Surface)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.align(Alignment.End),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = SombraColors.SurfaceSoft,
                    contentColor = SombraColors.Text,
                ),
            ) {
                Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.profile_settings))
            }
            ProfileHeader(
                name = uiState.name,
                subtitle = stringResource(R.string.profile_subtitle, uiState.skinType, uiState.city),
                level = uiState.level,
            )
        }
        LevelProgress(
            level = stringResource(R.string.profile_level, uiState.level, stringResource(uiState.levelName)),
            xp = stringResource(R.string.profile_xp, uiState.xp, uiState.xpGoal),
            progress = uiState.xp.toFloat() / uiState.xpGoal,
            next = stringResource(
                R.string.profile_next_level,
                stringResource(uiState.nextLevelName),
                stringResource(uiState.nextLevelUnlock),
            ),
        )
        StatCardsRow(
            stats = listOf(
                uiState.streakDays.toString() to stringResource(R.string.profile_stat_streak),
                "${uiState.highUvDaysProtected}/${uiState.highUvDays}" to stringResource(R.string.profile_stat_high_uv),
                uiState.onTimeReapplications.toString() to stringResource(R.string.profile_stat_on_time),
            ),
        )
        PatchesSection(
            title = stringResource(R.string.profile_patches_title),
            count = stringResource(R.string.profile_patches_count, uiState.unlockedPatches, uiState.totalPatches),
            patches = uiState.patches,
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ProfileContentPreview() {
    SombraTheme {
        ProfileContent(uiState = ProfileUiState())
    }
}
