package com.dmm.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.core.components.LocationPermissionStatus
import com.dmm.core.components.SombraDarkButton
import com.dmm.core.components.hasLocationPermission
import com.dmm.core.components.rememberLocationPermissionRequest
import com.dmm.core.components.SombraLinkPrompt
import com.dmm.core.components.SombraStepProgressBar
import com.dmm.core.components.SombraTextField
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R
import com.dmm.presentation.register.component.PasswordStrengthLabel

@Composable
fun RegisterScreen(
    onContinue: () -> Unit,
    onLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val requestLocation = rememberLocationPermissionRequest { status ->
        if (status == LocationPermissionStatus.Granted) viewModel.onUseLocation() else viewModel.onLocationDenied()
    }
    RegisterContent(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onCityChange = viewModel::onCityChange,
        onUseLocation = {
            if (context.hasLocationPermission()) viewModel.onUseLocation() else requestLocation()
        },
        // a navegação é decidida no SombraNavHost; o ViewModel só avisa quando o perfil está guardado
        onContinue = { viewModel.onContinue(onDone = onContinue) },
        onLogin = onLogin,
    )
}

@Composable
fun RegisterContent(
    uiState: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onUseLocation: () -> Unit = {},
    onContinue: () -> Unit = {},
    onLogin: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SombraColors.Surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()                       // o teclado empurra o conteúdo para cima
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
                    text = stringResource(R.string.register_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = SombraColors.Text,
                )
                Text(
                    text = stringResource(R.string.register_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = SombraColors.TextMuted,
                )
            }
            SombraTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = stringResource(R.string.register_name),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
            )
            SombraTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.register_email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            )
            SombraTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.register_password),
                isPassword = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                trailing = uiState.passwordStrength?.let { strength -> { PasswordStrengthLabel(strength) } },
            )
            SombraTextField(
                value = uiState.city,
                onValueChange = onCityChange,
                label = stringResource(R.string.register_city),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done,
                ),
                // o ✓ do teclado continua logo (com o teclado aberto, o botão fica tapado)
                keyboardActions = KeyboardActions(onDone = { onContinue() }),
                trailing = {
                    if (uiState.isLocating) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(12.dp).size(24.dp),
                            color = SombraColors.TextMuted,
                            strokeWidth = 2.dp,
                        )
                    } else IconButton(onClick = onUseLocation) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = stringResource(R.string.register_use_location),
                            tint = SombraColors.TextMuted,
                        )
                    }
                },
            )
        }
        uiState.message?.let { message ->
            Text(
                text = stringResource(message),
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (uiState.messageIsError) SombraColors.OnRiskHighContainer else SombraColors.SkyBlueDeep,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SombraLinkPrompt(
                question = stringResource(R.string.register_have_account),
                action = stringResource(R.string.register_login),
                onActionClick = onLogin,
                modifier = Modifier.weight(1f),
            )
            // O SombraDarkButton ocupa a largura toda por defeito; aqui só deve ter a largura do texto
            SombraDarkButton(
                text = stringResource(if (uiState.isLoading) R.string.register_loading else R.string.register_continue),
                onClick = onContinue,
                modifier = Modifier.width(IntrinsicSize.Max),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun RegisterContentPreview() {
    SombraTheme {
        RegisterContent(
            uiState = RegisterUiState(
                name = "Diogo Marques",
                email = "diogo@exemplo.pt",
                password = "palavrapasse",
                city = "Coimbra, PT",
                passwordStrength = PasswordStrength.STRONG,
            ),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onCityChange = {},
        )
    }
}
