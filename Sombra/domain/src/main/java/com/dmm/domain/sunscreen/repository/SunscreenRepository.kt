package com.dmm.domain.sunscreen.repository

import com.dmm.domain.sunscreen.model.SunscreenSession
import kotlinx.coroutines.flow.Flow

interface SunscreenRepository {
    fun observeActiveSession(): Flow<SunscreenSession?>
    suspend fun start(spf: Int, inWater: Boolean)
    suspend fun reapply()
    suspend fun stop()
}