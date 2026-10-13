package com.dmm.domain.weather.model

import kotlin.math.roundToInt

enum class UvLevel {
    Low, Moderate, High, VeryHigh, Extreme;

    companion object {
        fun from(uvIndex: Double): UvLevel = when (uvIndex.roundToInt()) {
            in Int.MIN_VALUE..2 -> Low
            in 3..5 -> Moderate
            in 6..7 -> High
            in 8..10 -> VeryHigh
            else -> Extreme
        }
    }
}