package com.example.location

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.location.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

const val COARSE_FINE_REQUEST_CODE = 100

class MainActivity : AppCompatActivity() {
    private lateinit var mService: MyLocationService
    private var mBound: Boolean = false
    private lateinit var binding: ActivityMainBinding

    private val connection = object: ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MyLocationService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        // Bind to LocalService
        Intent(this, MyLocationService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }


    /** Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute)  */
    fun onButtonClick(v: View) {
        if (mBound) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_DENIED
                    ) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showFineRequestSnackbar()
                        } else {
                            requestPermissions(
                                arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ), COARSE_FINE_REQUEST_CODE
                            )
                        }
                    }
                    showLocation()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                    AlertDialog.Builder(this)
                        .setTitle("This functionality needs geo-localization")
                        .setMessage("This functionality can't work without turning on geo-localization. Do you wish to do so now?")
                        .setPositiveButton("Yes") { _, _ ->
                            requestPermissions(
                                arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ), COARSE_FINE_REQUEST_CODE
                            )
                        }
                        .show()
                }
                else -> requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), COARSE_FINE_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            COARSE_FINE_REQUEST_CODE -> {
                if(grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    showLocation()
                }
            }
        }
    }

    private fun showLocation() {
        val location = mService.location

        location?.let {
            binding.latitude.text = getString(R.string.latitude, it.latitude)
            binding.longitude.text = getString(R.string.longitude, it.longitude)
        }
    }

    private fun showFineRequestSnackbar() {
        val mySnackbar = Snackbar.make(binding.constraintLayout,
            R.string.fine_is_better, Snackbar.LENGTH_SHORT)
        mySnackbar.setAction(R.string.fine_info) {
            AlertDialog.Builder(this)
                .setTitle("Receive more accurate position information")
                .setMessage("Our app services can be made more accurate by turning on precise location. Do you wish to do so now?")
                .setPositiveButton("Yes") {
                        _,_->
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), COARSE_FINE_REQUEST_CODE)
                }.show()
        }
        mySnackbar.show()
    }
}
