package com.example.location

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class MyLocationService : Service() {
    // Binder given to clients
    private val binder = LocalBinder()

    lateinit var fusedLocationClient: FusedLocationProviderClient

    /** method for clients  */
    val locationTask: Task<Location>?
        get() {
            Log.d("MyLocationService", "Entering getLocation")
            return if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //Can't handle permission requests from Service, so we just return null
                null
            } else {
                fusedLocationClient.lastLocation
            }
        }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): MyLocationService = this@MyLocationService
    }

    override fun onBind(intent: Intent): IBinder {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        return binder
    }
}
