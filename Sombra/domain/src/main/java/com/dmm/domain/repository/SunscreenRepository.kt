package com.dmm.domain.repository

import com.dmm.domain.model.SunscreenSession
import kotlinx.coroutines.flow.Flow

interface SunscreenRepository {
    fun observeActiveSession(): Flow<SunscreenSession?>
    suspend fun start(spf: Int, inWater: Boolean)
    suspend fun reapply()
    suspend fun stop()
}