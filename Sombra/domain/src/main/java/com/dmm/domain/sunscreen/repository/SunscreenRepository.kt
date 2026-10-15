package com.dmm.domain.sunscreen.repository

import com.dmm.domain.sunscreen.model.SunscreenEvent
import com.dmm.domain.sunscreen.model.SunscreenSession
import kotlinx.coroutines.flow.Flow

interface SunscreenRepository {
    fun observeActiveSession(): Flow<SunscreenSession?>
    suspend fun start(spf: Int, inWater: Boolean): SunscreenSession
    suspend fun reapply(): SunscreenSession?
    suspend fun stop()

    fun observeHistory(): Flow<List<SunscreenEvent>>
    suspend fun record(event: SunscreenEvent)

    // Junta o histórico do telemóvel com o da conta no Supabase (depois de entrar)
    suspend fun syncFromRemote()

    // Apaga o temporizador e o histórico do telemóvel (ao terminar sessão ou apagar a conta)
    suspend fun clear()
}
