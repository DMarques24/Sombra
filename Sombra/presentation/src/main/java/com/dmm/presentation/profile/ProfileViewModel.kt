package com.dmm.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.domain.auth.model.AuthResult
import com.dmm.domain.auth.usecase.DeleteAccountUseCase
import com.dmm.domain.auth.usecase.SignOutUseCase
import com.dmm.presentation.common.messageRes
import com.dmm.domain.profile.model.SkinType
import com.dmm.domain.profile.model.UserProfile
import com.dmm.domain.profile.repository.UserRepository
import com.dmm.domain.progress.model.Patch
import com.dmm.domain.progress.model.UserProgress
import com.dmm.domain.progress.usecase.ObserveProgressUseCase
import com.dmm.presentation.common.icon
import com.dmm.presentation.common.roman
import com.dmm.presentation.common.title
import com.dmm.presentation.common.unlock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeProgress: ObserveProgressUseCase,
    private val userRepository: UserRepository,
    private val signOut: SignOutUseCase,
    private val deleteAccount: DeleteAccountUseCase,
) : ViewModel() {

    private val editor = MutableStateFlow<ProfileEditorState?>(null)

    // Evento de uma só vez: saiu (ou apagou a conta), o ecrã vai para o Login
    private val _signedOut = Channel<Unit>(Channel.BUFFERED)
    val signedOut: Flow<Unit> = _signedOut.receiveAsFlow()

    // null enquanto lê o perfil e o histórico guardados: o ecrã mostra o esqueleto
    val uiState: StateFlow<ProfileUiState?> =
        combine(userRepository.observeProfile(), observeProgress(), editor) { profile, progress, editor ->
            toUiState(profile, progress, editor)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun onSettingsClick() {
        viewModelScope.launch {
            val profile = userRepository.observeProfile().first()
            editor.value = ProfileEditorState(profile.email, profile.name, profile.city, profile.skinType)
        }
    }

    fun onEditName(name: String) = editor.update { it?.copy(name = name) }

    fun onEditCity(city: String) = editor.update { it?.copy(city = city) }

    fun onEditSkinType(skinType: SkinType) = editor.update { it?.copy(skinType = skinType) }

    fun onDismissEditor() {
        editor.value = null
    }

    fun onSaveEditor() {
        val edited = editor.value ?: return
        viewModelScope.launch {
            userRepository.update {
                it.copy(name = edited.name.trim(), city = edited.city.trim(), skinType = edited.skinType)
            }
            editor.value = null
        }
    }

    // Termina a sessão no Supabase e apaga os dados da conta do telemóvel
    fun onSignOut() {
        viewModelScope.launch {
            editor.value = null
            signOut()
            _signedOut.send(Unit)
        }
    }

    fun onDeleteAccountClick() = editor.update { it?.copy(isConfirmingDelete = true, error = null) }

    fun onDismissDeleteAccount() = editor.update { it?.copy(isConfirmingDelete = false) }

    // Apaga a conta no Supabase (perfil e histórico incluídos)
    fun onConfirmDeleteAccount() {
        if (editor.value?.isDeleting == true) return
        editor.update { it?.copy(isDeleting = true) }
        viewModelScope.launch {
            when (val result = deleteAccount()) {
                is AuthResult.Success -> {
                    editor.value = null
                    _signedOut.send(Unit)
                }
                is AuthResult.Failure -> editor.update {
                    it?.copy(isDeleting = false, isConfirmingDelete = false, error = result.error.messageRes)
                }
            }
        }
    }

    private fun toUiState(profile: UserProfile, progress: UserProgress, editor: ProfileEditorState?) = ProfileUiState(
        name = profile.name,
        skinType = profile.skinType?.roman,
        city = profile.city,
        level = progress.level.number,
        levelName = progress.level.title,
        nextLevelName = progress.nextLevel?.title,
        nextLevelUnlock = progress.nextLevel?.unlock,
        xp = progress.xp,
        xpGoal = progress.nextLevel?.minXp ?: progress.xp,
        streakDays = progress.streakDays,
        highUvDaysProtected = progress.highUvDaysProtected,
        highUvDays = progress.highUvDays,
        onTimeReapplications = progress.onTimeReapplications,
        patches = Patch.entries.map { PatchUi(it.title, it.icon, unlocked = it in progress.unlockedPatches) },
        editor = editor,
    )
}
