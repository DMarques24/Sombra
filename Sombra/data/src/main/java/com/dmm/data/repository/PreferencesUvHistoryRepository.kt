package com.dmm.data.repository

import com.dmm.data.local.LocalStore
import com.dmm.domain.progress.repository.UvHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesUvHistoryRepository @Inject constructor(
    private val store: LocalStore,
) : UvHistoryRepository {

    private val days = MutableStateFlow(
        store.read(KEY, DaysSerializer).orEmpty().mapKeys { LocalDate.parse(it.key) }
    )

    override fun observeDailyMaxUv(): Flow<Map<LocalDate, Double>> = days.asStateFlow()

    override suspend fun record(date: LocalDate, maxUv: Double) {
        val updated = days.updateAndGet { current ->
            val max = maxOf(maxUv, current[date] ?: 0.0)
            // Só interessam os últimos dias: o mapa não cresce para sempre
            (current + (date to max)).filterKeys { !it.isBefore(date.minusDays(KEEP_DAYS)) }
        }
        store.write(KEY, DaysSerializer, updated.mapKeys { it.key.toString() })
    }

    override suspend fun clear() {
        days.value = emptyMap()
        store.write(KEY, DaysSerializer, null)
    }

    private companion object {
        const val KEY = "uv_history"
        const val KEEP_DAYS = 60L
        val DaysSerializer = MapSerializer(String.serializer(), Double.serializer())
    }
}
