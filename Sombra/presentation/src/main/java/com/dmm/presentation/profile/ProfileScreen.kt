package com.dmm.presentation.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import com.dmm.presentation.profile.component.EditProfileSheet
import com.dmm.presentation.profile.component.LevelProgress
import com.dmm.presentation.profile.component.PatchesSection
import com.dmm.presentation.profile.component.ProfileHeader
import com.dmm.presentation.profile.component.ProfileSkeleton
import com.dmm.presentation.profile.component.StatCardsRow

@Composable
fun ProfileScreen(onSignedOut: () -> Unit, viewModel: ProfileViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Como no Início: o esqueleto desaparece suavemente quando os dados chegam
    AnimatedContent(
        targetState = uiState,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        contentKey = { it == null },
        label = "profile",
    ) { state ->
        if (state == null) ProfileSkeleton()
        else ProfileContent(uiState = state, onSettingsClick = viewModel::onSettingsClick)
    }
    uiState?.editor?.let { editor ->
        EditProfileSheet(
            state = editor,
            onNameChange = viewModel::onEditName,
            onCityChange = viewModel::onEditCity,
            onSkinTypeChange = viewModel::onEditSkinType,
            onSave = viewModel::onSaveEditor,
            onSignOut = { viewModel.onSignOut(onDone = onSignedOut) },
            onDeleteAccount = viewModel::onDeleteAccountClick,
            onConfirmDeleteAccount = { viewModel.onConfirmDeleteAccount(onDone = onSignedOut) },
            onDismissDeleteAccount = viewModel::onDismissDeleteAccount,
            onDismiss = viewModel::onDismissEditor,
        )
    }
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
                name = uiState.name.ifBlank { stringResource(R.string.profile_no_name) },
                subtitle = profileSubtitle(uiState),
                level = uiState.level,
            )
        }
        LevelProgress(
            level = stringResource(R.string.profile_level, uiState.level, stringResource(uiState.levelName)),
            xp = stringResource(R.string.profile_xp, uiState.xp, uiState.xpGoal),
            progress = if (uiState.xpGoal > 0) uiState.xp.toFloat() / uiState.xpGoal else 1f,
            next = if (uiState.nextLevelName != null && uiState.nextLevelUnlock != null) {
                stringResource(
                    R.string.profile_next_level,
                    stringResource(uiState.nextLevelName),
                    stringResource(uiState.nextLevelUnlock),
                )
            } else {
                stringResource(R.string.profile_max_level)
            },
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

// "Pele tipo II · Coimbra", sem as partes que a pessoa não preencheu
@Composable
private fun profileSubtitle(uiState: ProfileUiState): String {
    val skin = uiState.skinType?.let { stringResource(R.string.profile_skin_type, it) }
        ?: stringResource(R.string.profile_skin_type_unknown)
    return listOf(skin, uiState.city).filter { it.isNotBlank() }.joinToString(" · ")
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ProfileContentPreview() {
    SombraTheme {
        ProfileContent(uiState = ProfileUiState())
    }
}
