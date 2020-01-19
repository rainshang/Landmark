package com.xyx.landmark.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.xyx.landmark.R
import com.xyx.landmark.vo.User
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment() {

    private val REQUEST_CODE_PERMISSION_LOCATION = 7

    private lateinit var map: GoogleMap
    private var lastLocation: Location? = null
//    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync {
            it.apply {
                map = this
                uiSettings.isMyLocationButtonEnabled = true
                if (checkPermission()) isMyLocationEnabled = true else requestPermission()
                setOnMyLocationChangeListener { location ->
                    if (!location.equal(lastLocation)) {
                        lastLocation = location
                        fab.isEnabled = true
                        val latLng = LatLng(location.latitude, location.longitude)
                        animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

//                        val marker = addMarker(
//                            MarkerOptions().position(latLng)
//                                .title("Test")
//                        )
//                        setOnMarkerClickListener {
//                            Snackbar.make(fab, it.title, Snackbar.LENGTH_SHORT).show()
//                            true
//                        }
                    }
                }
            }
        }
        fab.apply {
            isEnabled = lastLocation != null
            setOnClickListener {
                lastLocation?.run {
                    findNavController().navigate(
                        MapFragmentDirections.actionMapFragmentToNoteFragment(
                            User.Note.Loc(
                                latitude,
                                longitude
                            )
                        )
                    )
                }
            }
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

fun Location.equal(l: Location?): Boolean {
    return l?.run { this@equal.latitude == latitude && this@equal.longitude == longitude }
        ?: run { false }
}