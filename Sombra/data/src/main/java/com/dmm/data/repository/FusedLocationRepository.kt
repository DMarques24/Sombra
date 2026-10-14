package com.dmm.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.dmm.domain.location.model.Coordinates
import com.dmm.domain.location.repository.LocationRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Localização pelo Fused Location Provider dos Google Play Services
class FusedLocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationRepository {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")   // a permissão é verificada logo na primeira linha
    override suspend fun getCurrentLocation(): Coordinates? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) return null

        return try {
            val location = client
                .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .await()
            location?.let { Coordinates(it.latitude, it.longitude) }
        } catch (e: CancellationException) {
            throw e                            // cancelamento normal: deixa passar
        } catch (e: Exception) {
            null                               // GPS desligado, Play Services em falta, etc.
        }
    }
}
