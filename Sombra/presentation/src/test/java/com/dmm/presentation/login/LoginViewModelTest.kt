package com.dmm.presentation.login

import app.cash.turbine.test
import com.dmm.domain.auth.model.AuthError
import com.dmm.domain.auth.model.AuthResult
import com.dmm.domain.auth.usecase.SignInUseCase
import com.dmm.domain.profile.model.UserProfile
import com.dmm.presentation.R
import com.dmm.presentation.testing.FakeAuthRepository
import com.dmm.presentation.testing.FakeSunscreenRepository
import com.dmm.presentation.testing.FakeUserRepository
import com.dmm.presentation.testing.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import java.time.Clock

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth = FakeAuthRepository()
    private val users = FakeUserRepository(UserProfile(onboardingCompleted = true))

    private fun viewModel() = LoginViewModel(
        signIn = SignInUseCase(auth, users, FakeSunscreenRepository(Clock.systemUTC())),
        authRepository = auth,
        userRepository = users,
    )

    @Test
    fun `entrar com sucesso envia um único evento com o estado do registo`() = runTest {
        val vm = viewModel()
        vm.onEmailChange("ana@exemplo.pt")
        vm.onPasswordChange("segredo123")

        vm.events.test {
            vm.onLogin()
            assertEquals(LoginEvent.SignedIn(onboardingCompleted = true), awaitItem())
            expectNoEvents()                                    // o evento não se repete
        }
        assertFalse(vm.uiState.value.isLoading)
        assertEquals("", vm.uiState.value.password)             // a palavra-passe não fica no estado
    }

    @Test
    fun `credenciais erradas mostram a mensagem e não navegam`() = runTest {
        auth.signInResult = AuthResult.Failure(AuthError.InvalidCredentials)
        val vm = viewModel()
        vm.onEmailChange("ana@exemplo.pt")
        vm.onPasswordChange("errada")

        vm.events.test {
            vm.onLogin()
            expectNoEvents()
        }
        assertEquals(R.string.auth_error_invalid_credentials, vm.uiState.value.message)
    }

    @Test
    fun `campos vazios não fazem pedido`() = runTest {
        val vm = viewModel()

        vm.onLogin()

        assertEquals(0, auth.signInCalls)
        assertEquals(R.string.login_missing_fields, vm.uiState.value.message)
    }
}
