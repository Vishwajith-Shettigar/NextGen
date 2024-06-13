package com.example.nextgen.nearby

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.domain.constants.CIRCLE_RADIUS
import com.example.domain.constants.LOG_KEY
import com.example.domain.constants.MAX_UPDATE
import com.example.domain.nearby.NearByController
import com.example.model.Profile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentNearByBinding
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import javax.inject.Inject

class NearByFragment : BaseFragment(), OnMapReadyCallback, UpdateMapListener {

  @Inject
  lateinit var nearByController: NearByController

  private lateinit var mMap: GoogleMap
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  private lateinit var nearByViewModel: NearByViewModel
  private lateinit var binding: FragmentNearByBinding

  private lateinit var locationUser: LatLng

  private val userMarkers: MutableMap<String, Marker> = mutableMapOf()
  private var usermarker: Marker? = null

  private var userCircle: Circle? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    binding = FragmentNearByBinding.inflate(inflater, container, false)
    nearByViewModel = NearByViewModel(viewLifecycleOwner, nearByController, this)

    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
    mapFragment?.getMapAsync(this)

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    checkLocationPermission()

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    fusedLocationClient.lastLocation.addOnSuccessListener {
      it?.let {
        locationUser = LatLng(it.latitude, it.longitude)
        nearByViewModel.location.value = locationUser
      }
    }

    val locationRequest = LocationRequest.create().apply {
      interval = 1000
      fastestInterval = 1000
      priority = LocationRequest.PRIORITY_HIGH_ACCURACY
      smallestDisplacement = 1f
    }

    val locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        val currentLocation = locationResult.lastLocation
        currentLocation?.let {
          val latLng = LatLng(it.latitude, it.longitude)
          Log.e(LOG_KEY,latLng.toString()+"  update location")

          moveUser(latLng)
          val lastGeoLocation = GeoLocation(locationUser.latitude, locationUser.longitude)
          val newGeoLocation = GeoLocation(it.latitude, it.longitude)
          val distance = GeoFireUtils.getDistanceBetween(newGeoLocation, lastGeoLocation)
          if (MAX_UPDATE <= distance && it.accuracy <= MAX_UPDATE) {
            Log.e(LOG_KEY,locationUser.toString()+"  old")

            Log.e(LOG_KEY,latLng.toString()+"  new")
            moveCamera(latLng)
            Toast.makeText(requireActivity(), "New radius", Toast.LENGTH_SHORT).show()
            locationUser = latLng
            nearByViewModel.location.value = latLng
          }
        }
      }
    }
    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
  }

  private fun checkLocationPermission() {
    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
      ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }
  }

  companion object {
    const val TAG = "NearByFragment"
    fun newInstance(): NearByFragment {
      return NearByFragment()
    }
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
      ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
      return
    }
    mMap.isMyLocationEnabled = true
    moveCamera(locationUser)
  }

  private fun moveUser(location: LatLng){
    usermarker?.remove()
    usermarker = mMap.addMarker(MarkerOptions().position(location).title("You are here")
      .icon(bitmapDescriptorFromVector(R.drawable.outline_person_ic)))
  }
  private fun moveCamera(location: LatLng) {
    userCircle?.remove()
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, getZoomLevel(CIRCLE_RADIUS.toDouble())))
    moveUser(location)
    userCircle = mMap.addCircle(
      CircleOptions()
        .center(location)
        .radius(CIRCLE_RADIUS.toDouble())
        .strokeColor(R.color.black)
        .fillColor(R.color.teal_200)
    )
  }

  private fun getZoomLevel(radius: Double): Float {
    val scale = radius / 500.0
    return (16 - Math.log(scale) / Math.log(2.0)).toFloat()
  }

  @Deprecated("Deprecated in Java")
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      onMapReady(mMap)
    }
  }

  private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
    val context = context ?: return null
    val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable?.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
  }

  override fun updateMap(profile: Profile, outOfBound: Boolean) {
    if (outOfBound) {
      userMarkers.remove(profile.userId)?.remove()
    } else {
      val otherUserLocation = LatLng(profile.location.latitude, profile.location.longitude)
      userMarkers.remove(profile.userId)?.remove()
      userMarkers[profile.userId] = mMap.addMarker(MarkerOptions().position(otherUserLocation).title(profile.userId))!!
    }
    Log.e(LOG_KEY, "${userMarkers.size} markers on map")
  }
}
