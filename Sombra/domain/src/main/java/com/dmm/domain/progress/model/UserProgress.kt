package com.dmm.domain.progress.model

data class UserProgress(
    val xp: Int,
    val level: Level,
    val streakDays: Int,
    val highUvDaysProtected: Int,       // nos últimos 30 dias
    val highUvDays: Int,                // nos últimos 30 dias
    val onTimeReapplications: Int,
    val unlockedPatches: Set<Patch>,
) {
    val nextLevel: Level? get() = level.next
}
