package com.dmm.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.core.components.SombraDarkButton
import com.dmm.core.components.SombraSecondaryButton
import com.dmm.core.components.SombraTextField
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R
import com.dmm.presentation.login.component.LoginSkyHeader
import com.dmm.presentation.login.component.OrDivider
import com.dmm.presentation.login.component.SignUpPrompt

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // TODO: ligar Entrar, Apple, Google, Esqueci-me e Criar conta quando houver lógica
    LoginContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onForgotPassword: () -> Unit = {},
    onLogin: () -> Unit = {},
    onAppleLogin: () -> Unit = {},
    onGoogleLogin: () -> Unit = {},
    onCreateAccount: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SombraColors.Surface)
            .navigationBarsPadding()
            .imePadding(),                      // o teclado empurra o conteúdo para cima
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            LoginSkyHeader()
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.login_title),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = SombraColors.Text,
                    )
                    Text(
                        text = stringResource(R.string.login_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = SombraColors.TextMuted,
                    )
                }
                SombraTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    label = stringResource(R.string.login_email),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                )
                SombraTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    label = stringResource(R.string.login_password),
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    trailing = {
                        TextButton(onClick = onForgotPassword) {
                            Text(
                                text = stringResource(R.string.login_forgot_password),
                                style = MaterialTheme.typography.bodyMedium,
                                color = SombraColors.TextMuted,
                            )
                        }
                    },
                )
                SombraDarkButton(text = stringResource(R.string.login_submit), onClick = onLogin)
                OrDivider(text = stringResource(R.string.login_or))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SombraSecondaryButton(
                        text = stringResource(R.string.login_apple),
                        onClick = onAppleLogin,
                        icon = painterResource(R.drawable.ic_apple),
                        modifier = Modifier.weight(1f),
                    )
                    SombraSecondaryButton(
                        text = stringResource(R.string.login_google),
                        onClick = onGoogleLogin,
                        icon = painterResource(R.drawable.ic_google),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        SignUpPrompt(
            question = stringResource(R.string.login_no_account),
            action = stringResource(R.string.login_create_account),
            onActionClick = onCreateAccount,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .wrapContentWidth(Alignment.CenterHorizontally),
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun LoginContentPreview() {
    SombraTheme {
        LoginContent(
            uiState = LoginUiState(email = "diogo@exemplo.pt", password = "palavrapasse"),
            onEmailChange = {},
            onPasswordChange = {},
        )
    }
}
