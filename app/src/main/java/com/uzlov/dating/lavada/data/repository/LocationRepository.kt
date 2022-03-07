package com.uzlov.dating.lavada.data.repository

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.RuntimeExecutionException
import com.uzlov.dating.lavada.di.modules.LocationModule
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LocationRepository @Inject constructor(
    @Named(LocationModule.REQUEST_GPS) val requestGps: LocationRequest,
    @Named(LocationModule.REQUEST_NETWORK) val requestNetwork: LocationRequest,
    private val locationManager: LocationManager,
    val locationClient: FusedLocationProviderClient,
) {


    @SuppressLint("MissingPermission")
    suspend fun requestLocation(): Location {
        return suspendCoroutine { continuation ->
            val callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    continuation.resumeWith(Result.success(locationResult.lastLocation))
                    locationClient.removeLocationUpdates(this)
                }
            }
            when {
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                    locationClient.requestLocationUpdates(requestGps,
                        callback,
                        Looper.getMainLooper())

                }
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                    locationClient.requestLocationUpdates(requestNetwork,
                        callback,
                        Looper.getMainLooper())
                }
                else -> {
                    continuation.resumeWithException(RuntimeException("Failed load your location!"))
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLocation(): Location? {
        return suspendCoroutine { continuation ->
            if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
                continuation.resumeWith(Result.failure(java.lang.RuntimeException("Location is disable")))
            } else {
                locationClient.lastLocation
                    .addOnCompleteListener { locTask ->
                        val result = locTask.result
                        try {
                            continuation.resumeWith(Result.success(result))
                        } catch (e: RuntimeExecutionException) {
                            continuation.resumeWithException(e)
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
            }
        }
    }
}