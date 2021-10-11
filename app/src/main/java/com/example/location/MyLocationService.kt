package com.example.location

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder

class MyLocationService : Service() {
    // Binder given to clients
    private val binder = LocalBinder()

    /** method for clients  */
    val location: Location?
        get() {
            return null
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
        return binder
    }
}
