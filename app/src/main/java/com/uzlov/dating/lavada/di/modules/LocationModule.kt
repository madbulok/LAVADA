package com.uzlov.dating.lavada.di.modules

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class LocationModule {

    @Provides
    fun provideLocationManager(context: Context) : LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    fun provideLocationClient(context: Context) : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Named(REQUEST_GPS)
    fun locationRequestGPS(): LocationRequest =
        LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(1)
            .setExpirationDuration(1000)

    @Provides
    @Named(REQUEST_NETWORK)
   fun locationRequestNETWORK() : LocationRequest =
        LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_LOW_POWER)
            .setNumUpdates(1)
            .setExpirationDuration(1000)

    companion object {
        const val REQUEST_GPS = "gps"
        const val REQUEST_NETWORK = "network"
    }
}