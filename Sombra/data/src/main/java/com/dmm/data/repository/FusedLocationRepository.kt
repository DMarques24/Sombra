package com.dmm.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.core.content.ContextCompat
import com.dmm.data.di.IoDispatcher
import com.dmm.domain.location.model.Coordinates
import com.dmm.domain.location.repository.LocationRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

// Localização pelo Fused Location Provider dos Google Play Services
class FusedLocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : LocationRepository {

    private val client = LocationServices.getFusedLocationProviderClient(context)
    private val geocoder = Geocoder(context, Locale.getDefault())

    @SuppressLint("MissingPermission")   // a permissão é verificada logo na primeira linha
    override suspend fun getCurrentLocation(): Coordinates? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) return null

        return try {
            // Sem sinal, o GPS pode demorar muito: ao fim de uns segundos usa a última posição conhecida
            val cancellation = CancellationTokenSource()
            val location = withTimeoutOrNull(LOCATION_TIMEOUT_MILLIS) {
                client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellation.token).await()
            } ?: run {
                cancellation.cancel()
                client.lastLocation.await()
            }
            location?.let { Coordinates(it.latitude, it.longitude) }
        } catch (e: CancellationException) {
            throw e                            // cancelamento normal: deixa passar
        } catch (e: Exception) {
            null                               // GPS desligado, Play Services em falta, etc.
        }
    }

    override suspend fun getCityName(coordinates: Coordinates): String? =
        geocode { listener ->
            if (listener != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1, listener)
                null
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
            }
        }?.let { it.locality ?: it.subAdminArea ?: it.adminArea }

    override suspend fun getCoordinates(cityName: String): Coordinates? =
        geocode { listener ->
            if (listener != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(cityName, 1, listener)
                null
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocationName(cityName, 1)
            }
        }?.let { Coordinates(it.latitude, it.longitude) }

    // O Geocoder tem duas APIs: com listener (Android 13+) e a antiga, que bloqueia a thread.
    // request recebe o listener quando há e devolve o resultado só na API antiga.
    private suspend fun geocode(
        request: (Geocoder.GeocodeListener?) -> List<Address>?,
    ): Address? {
        if (!Geocoder.isPresent()) return null
        return try {
            // Alguns telemóveis nunca respondem ao Geocoder: o nome da cidade não pode prender o ecrã
            withTimeoutOrNull(GEOCODER_TIMEOUT_MILLIS) { geocodeNow(request) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null                               // sem rede, serviço de geocoding indisponível, etc.
        }
    }

    private suspend fun geocodeNow(request: (Geocoder.GeocodeListener?) -> List<Address>?): Address? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { cont ->
                request(object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) = cont.resume(addresses.firstOrNull())
                    override fun onError(errorMessage: String?) = cont.resume(null)
                })
            }
        } else {
            withContext(ioDispatcher) { request(null)?.firstOrNull() }
        }

    private companion object {
        const val LOCATION_TIMEOUT_MILLIS = 8_000L
        const val GEOCODER_TIMEOUT_MILLIS = 5_000L
    }
}
