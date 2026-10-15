package com.dmm.domain.auth.usecase

import com.dmm.domain.profile.repository.UserRepository
import com.dmm.domain.progress.repository.UvHistoryRepository
import com.dmm.domain.sunscreen.repository.ReminderScheduler
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import javax.inject.Inject

// Tira do telemóvel tudo o que é da conta: a próxima pessoa a entrar não vê nada de quem saiu.
// (O que está no Supabase fica; volta quando a mesma conta entrar outra vez.)
class ClearLocalDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sunscreenRepository: SunscreenRepository,
    private val uvHistoryRepository: UvHistoryRepository,
    private val reminderScheduler: ReminderScheduler,
) {
    suspend operator fun invoke() {
        reminderScheduler.cancel()
        userRepository.clear()
        sunscreenRepository.clear()
        uvHistoryRepository.clear()
    }
}
