package com.dmm.domain.progress.repository

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

// O UV máximo de cada dia em que a app foi aberta. Serve para saber quantos dias de UV alto houve.
interface UvHistoryRepository {
    fun observeDailyMaxUv(): Flow<Map<LocalDate, Double>>
    suspend fun record(date: LocalDate, maxUv: Double)
    suspend fun clear()
}
