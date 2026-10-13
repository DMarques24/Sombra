package com.dmm.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.Instant


data class SunscreenSession(
    val appliedAt: Instant,
    val spf: Int,
    val inWater: Boolean,
){
    val duration: Duration @RequiresApi(Build.VERSION_CODES.O)
    get() = if (inWater) Duration.ofMinutes(40) else Duration.ofHours(2)
    val endsAt: Instant get() = appliedAt.plus(duration)
}