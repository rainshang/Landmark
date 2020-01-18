package com.xyx.landmark.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.xyx.landmark.R

class MapFragment : SupportMapFragment() {

    private val REQUEST_CODE_PERMISSION_LOCATION = 7

    private lateinit var map: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getMapAsync {
            map = it
            map.uiSettings.apply {
                isZoomControlsEnabled = true
                isMyLocationButtonEnabled = true
            }
            if (checkPermission()) map.isMyLocationEnabled = true else requestPermission()

            val latLng = LatLng(-34.0, 151.0)
//            map.addMarker(
//                MarkerOptions().position(latLng)
//                    .icon(
//                        BitmapDescriptorFactory.fromResource(
//                            R.drawable.marker_atm_commbank
//                        )
//                    )
//            )
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    private fun checkPermission(): Boolean {
        return context?.run {
            (hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    && hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
        } ?: false
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), REQUEST_CODE_PERMISSION_LOCATION
        )
    }

    private fun hasPermission(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION_LOCATION
            && checkPermission()
        ) {
            map.isMyLocationEnabled = true
        } else {
            activity?.finish()
            Toast.makeText(
                context?.applicationContext,
                getString(R.string.tip_location_permission_err, getString(R.string.app_name)),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}