package com.xyx.landmark.ui.map

import android.location.Location
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.xyx.landmark.R
import com.xyx.landmark.vo.User
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment() {

    private lateinit var map: GoogleMap
    private var lastLocation: Location? = null

    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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
                isMyLocationEnabled = true
                setOnMyLocationChangeListener { location ->
                    if (!location.equal(lastLocation)) {
                        lastLocation = location
                        fab.isEnabled = true
                        val latLng = LatLng(location.latitude, location.longitude)
                        animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                        setOnMarkerClickListener { false }
                    }
                }
            }
            viewModel.apply {
                allNotes.observe(viewLifecycleOwner, Observer { notes -> updateMarkers(notes) })
                searchNotes.observe(viewLifecycleOwner, Observer { notes -> updateMarkers(notes) })
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

    private fun updateMarkers(notes: List<User.Note>) {
        map.clear()
        notes.forEach {
            map.addMarker(
                MarkerOptions().position(LatLng(it.loc!!.lat!!, it.loc.lng!!))
                    .title(it.name)
                    .snippet(it.content)
                    .icon(getIcon(it.uid))
            )
        }
    }

    private fun getIcon(uid: String?): BitmapDescriptor {
        return BitmapDescriptorFactory.defaultMarker(
            if (FirebaseAuth.getInstance().currentUser?.uid == uid)
                BitmapDescriptorFactory.HUE_GREEN
            else
                BitmapDescriptorFactory.HUE_VIOLET
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_map, menu)
        val searchView = menu.findItem(R.id.menu_map_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                if (!query.isNullOrEmpty()) {
                    viewModel.search(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        searchView.setOnCloseListener {
            viewModel.allNotes.value?.run { updateMarkers(this) } // load all
            false
        }
    }

}

fun Location.equal(l: Location?): Boolean {
    return l?.run { this@equal.latitude == latitude && this@equal.longitude == longitude }
        ?: run { false }
}