package com.dmm.data.repository

import com.dmm.domain.sunscreen.model.SunscreenSession
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // uma só instância na app, partilhada pelas duas ViewModels
class InMemorySunscreenRepository @Inject constructor(
    private val clock: Clock,
) : SunscreenRepository {
    private val session = MutableStateFlow<SunscreenSession?>(null)
    override fun observeActiveSession() = session.asStateFlow()

    override suspend fun start(spf: Int, inWater: Boolean) {
        session.value = SunscreenSession(clock.instant(), spf, inWater)
    }

    override suspend fun reapply() {
        session.update { it?.copy(appliedAt = clock.instant()) }
    }

    override suspend fun stop() {
        session.value = null
    }
}