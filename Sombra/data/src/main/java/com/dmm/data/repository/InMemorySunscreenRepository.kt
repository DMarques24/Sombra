package com.dmm.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.dmm.domain.model.SunscreenSession
import com.dmm.domain.repository.SunscreenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // uma só instância na app, partilhada pelas duas ViewModels
class InMemorySunscreenRepository @RequiresApi(Build.VERSION_CODES.O)
@Inject constructor(
    private val clock: Clock,
) : SunscreenRepository {
    private val session = MutableStateFlow<SunscreenSession?>(null)
    override fun observeActiveSession() = session.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun start(spf: Int, inWater: Boolean) {
        session.value = SunscreenSession(clock.instant(), spf, inWater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun reapply() {
        session.update { it?.copy(appliedAt = clock.instant()) }
    }

    override suspend fun stop() {
        session.value = null
    }
}