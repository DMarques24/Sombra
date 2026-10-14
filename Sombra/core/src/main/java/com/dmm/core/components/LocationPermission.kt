package com.dmm.core.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

enum class LocationPermissionStatus { Granted, Denied, PermanentlyDenied }

private const val PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION

fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, PERMISSION) == PackageManager.PERMISSION_GRANTED

@Composable
fun rememberLocationPermissionRequest(
    onResult: (LocationPermissionStatus) -> Unit,
): () -> Unit {
    // null só em previews ou fora de uma Activity: aí não há como perguntar outra vez
    val activity = LocalActivity.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        val status = when {
            granted -> LocationPermissionStatus.Granted
            // Depois de uma recusa, se o Android já não deixa explicar, é "nunca mais perguntar"
            activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION) ->
                LocationPermissionStatus.PermanentlyDenied

            else -> LocationPermissionStatus.Denied
        }
        onResult(status)
    }

    return { launcher.launch(PERMISSION) }
}

/** Abre as definições da app, para a pessoa ativar a localização à mão. */
fun Context.openAppSettings() {
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
    )
}
