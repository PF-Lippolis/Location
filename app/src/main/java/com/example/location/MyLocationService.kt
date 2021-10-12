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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class MyLocationService : Service() {
    // Binder given to clients
    private val binder = LocalBinder()

    lateinit var fusedLocationClient: FusedLocationProviderClient
    private val _locationLiveData = MutableLiveData<Location>()
    val locationProvider: LiveData<Location>
        get() {
            return _locationLiveData
        }

    /** method for clients  */
    fun updateLocation() {
            Log.d("MyLocationService", "Entering updateLocation")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    _locationLiveData.postValue(it)
                }
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
