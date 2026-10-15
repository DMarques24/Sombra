package com.dmm.presentation.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.dmm.core.components.SombraDarkButton
import com.dmm.core.components.SombraSecondaryButton
import com.dmm.core.components.SombraTextField
import com.dmm.core.designsystem.SombraColors
import com.dmm.domain.profile.model.SkinType
import com.dmm.presentation.R
import com.dmm.presentation.profile.ProfileEditorState
import com.dmm.presentation.skintype.component.SkinTypeGrid

// Folha que sobe de baixo para mudar o nome, a cidade e o tipo de pele
// Apagar a conta não tem volta atrás: pede confirmação primeiro
@Composable
private fun DeleteAccountDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.profile_delete_title)) },
        text = { Text(stringResource(R.string.profile_delete_text)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.profile_delete_confirm), color = SombraColors.UvVeryHigh)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.profile_delete_cancel)) }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileSheet(
    state: ProfileEditorState,
    onNameChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onSkinTypeChange: (SkinType) -> Unit,
    onSave: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onConfirmDeleteAccount: () -> Unit,
    onDismissDeleteAccount: () -> Unit,
    onDismiss: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val privacyUrl = stringResource(R.string.privacy_policy_url)
    if (state.isConfirmingDelete) {
        DeleteAccountDialog(onConfirm = onConfirmDeleteAccount, onDismiss = onDismissDeleteAccount)
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SombraColors.Surface,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.profile_edit_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = SombraColors.Text,
            )
            if (state.email.isNotBlank()) {
                Text(
                    text = state.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SombraColors.TextMuted,
                )
            }
            SombraTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = stringResource(R.string.register_name),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
            )
            SombraTextField(
                value = state.city,
                onValueChange = onCityChange,
                label = stringResource(R.string.register_city),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done,
                ),
            )
            Text(
                text = stringResource(R.string.profile_edit_skin_type),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SombraColors.Text,
            )
            SkinTypeGrid(selected = state.skinType, onSelect = onSkinTypeChange)
            SombraDarkButton(text = stringResource(R.string.profile_edit_save), onClick = onSave)
            SombraSecondaryButton(text = stringResource(R.string.profile_sign_out), onClick = onSignOut)
            state.error?.let { error ->
                Text(
                    text = stringResource(error),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SombraColors.OnRiskHighContainer,
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { uriHandler.openUri(privacyUrl) }) {
                    Text(stringResource(R.string.profile_privacy_policy), color = SombraColors.TextMuted)
                }
                TextButton(onClick = onDeleteAccount, enabled = !state.isDeleting) {
                    Text(
                        text = stringResource(if (state.isDeleting) R.string.profile_deleting else R.string.profile_delete_account),
                        color = SombraColors.UvVeryHigh,
                    )
                }
            }
        }
    }
}
