package com.example.android.politicalpreparedness

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.network.models.Address
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class LocationAppServices(private val fragment: Fragment) {

    companion object {
        private const val FOREGROUND_AND_BACKGROUND_PERMISSION_REQUEST_CODE = 201
        private const val FOREGROUND_PERMISSION_REQUEST_CODE = 202
        const val TURN_DEVICE_LOCATION_ON_REQUEST_CODE = 203
    }


    /**
     * LOCATION PERMISSION FLAG
     */

    /*  Proceed to find location if permission is granted;
        Request instead the permission if it is not already granted
     */
    fun checkLocationPermission(): Boolean {
        return if (isPermissionGranted()) true
        else {
            var permissionArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            val resultCode =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissionArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    FOREGROUND_AND_BACKGROUND_PERMISSION_REQUEST_CODE
                } else FOREGROUND_PERMISSION_REQUEST_CODE
            fragment.requestPermissions(permissionArray, resultCode)
            false
        }
    }

    /*  Check if the foreground and background permission have been granted
     */
    private fun isPermissionGranted(): Boolean {
        val foregroundPermissionGranted = (PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(
                    fragment.requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ))
        val backgroundPermissionGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    fragment.requireContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            } else true

        return (foregroundPermissionGranted && backgroundPermissionGranted)
    }

    /*  Check the result of the permission grant request
        Show an alert dialog if the user decides not to enable the required permission
     */
    fun onRequestPermissionsResult(grantResults: IntArray): Boolean {
        var outcome = true
        for (grant in grantResults) {
            if (grant == PackageManager.PERMISSION_DENIED) {
                outcome = false
                break
            }
        }

        return if (!outcome) {
            // Build a material alert dialog that addresses the user to the settings app
            MaterialAlertDialogBuilder(fragment.requireContext())
                .setTitle(fragment.requireContext().resources.getString(R.string.permission_alert_dialog_title))
                .setMessage(fragment.requireContext().resources.getString(R.string.permission_alert_dialog_message))
                .setCancelable(false)
                .setNegativeButton(fragment.requireContext().resources.getString(R.string.permission_alert_dialog_negative_button)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(fragment.requireContext().resources.getString(R.string.permission_alert_dialog_positive_button)) { dialog, _ ->
                    fragment.requireContext().startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    dialog.dismiss()
                }
                .create()
                .show()
            false
        } else checkLocationPermission()
    }


    /**
     * ACTIVE DEVICE LOCATION FLAG
     */

    /* Check if the device location is enabled and active
     */
    fun enableDeviceLocation(): Task<LocationSettingsResponse> {
        // Build location settings request
        val locationSettingsRequest = LocationSettingsRequest.Builder().addLocationRequest(
            LocationRequest.create().apply { priority = LocationRequest.PRIORITY_LOW_POWER }
        ).build()

        // Define settings client for location services
        val settingsClient = LocationServices.getSettingsClient(fragment.requireContext())

        // Check if the location settings are enabled
        return settingsClient.checkLocationSettings(locationSettingsRequest)
    }

    /*  Try to provide a solution for the failure in the current location retrieval
        If the failure is automatically resolvable, ask directly the user to activate the location
        Otherwise, warn the user that the app does not work correctly without enabling the location
     */
    fun solveOnDeviceLocationInactive(exception: Exception): Boolean {
        if (exception is ResolvableApiException) {
            try {       // If Android identifies a PendingIntent to be used for the resolution
                fragment.startIntentSenderForResult(
                    exception.resolution.intentSender,
                    TURN_DEVICE_LOCATION_ON_REQUEST_CODE,
                    null, 0, 0, 0, null
                )
                return true
            } catch (sendException: IntentSender.SendIntentException) {
                Toast.makeText(
                    fragment.requireContext(),
                    fragment.requireContext().resources.getString(
                        R.string.device_location_toast_error_message,
                        sendException.message
                    ),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        } else {
            MaterialAlertDialogBuilder(fragment.requireContext())
                .setTitle(fragment.requireContext().resources.getString(R.string.device_location_alert_dialog_title))
                .setMessage(fragment.requireContext().resources.getString(R.string.device_location_alert_dialog_message))
                .setPositiveButton(fragment.requireContext().resources.getString(android.R.string.ok)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
            return false
        }
    }


    /**
     * CURRENT LOCATION FLAG
     */

    /*  Retrieve the current location of the user
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): Task<Location> {
        return LocationServices.getFusedLocationProviderClient(fragment.requireContext())
            .getCurrentLocation(LocationRequest.PRIORITY_LOW_POWER, null)
    }

    /*  Ask the user to retry from a different position when current location has not been
        retrieved correctly
     */
    fun onCurrentLocationError() {
        Toast.makeText(
            fragment.requireContext(),
            fragment.requireContext().resources.getString(R.string.current_location_toast_error_message),
            Toast.LENGTH_SHORT
        ).show()
    }


    /**
     * GEOCODE LOCATION FLAG
     */

    /*  Decode the lat-long location into a readable address
     */
    fun geocodeLocation(location: Location): Address {
        val geocoder = Geocoder(fragment.requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }
}