package com.dmm.domain.weather.model

// Quanto tempo a pele aguenta ao sol agora e quando é melhor evitar o sol hoje
data class SunRisk(
    val burnMinutes: Int?,              // null quando o UV é tão baixo que não há risco de queimar
    val shadeHours: HourRange?,         // UV 6 ou mais: procurar sombra
    val protectHours: HourRange?,       // UV 3 ou mais: usar protetor
    val recommendedSpf: Int,
)

// Intervalo de horas inteiras: 12..16 quer dizer "das 12h às 16h"
data class HourRange(val startHour: Int, val endHour: Int)
