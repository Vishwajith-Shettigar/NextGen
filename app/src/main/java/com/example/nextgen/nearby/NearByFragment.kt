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
import android.location.LocationRequest
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.example.domain.constants.CIRCLE_RADIUS
import com.example.domain.constants.LOG_KEY
import com.example.domain.constants.MAX_UPDATE
import com.example.domain.nearby.NearByController
import com.example.model.Profile
import com.example.utility.GeoUtils
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentNearByBinding
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [NearByFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NearByFragment : BaseFragment(), OnMapReadyCallback, UpdateMapListener {


  @Inject
  lateinit var nearByController: NearByController

  @Inject
  lateinit var fragment: Fragment

  private lateinit var mMap: GoogleMap
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  private lateinit var nearByViewModel: NearByViewModel
  private lateinit var binding: FragmentNearByBinding

  lateinit var locationUser: LatLng

  private val userMarkers: MutableMap<String, Marker> = mutableMapOf<String, Marker>()

  private var userCicle: Circle? = null

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
    if (ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED &&
      ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        requireActivity(),
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        1
      )
    }
    // Inflate the layout for this fragment
    binding = FragmentNearByBinding.inflate(inflater, container, false)
    nearByViewModel =
      NearByViewModel(viewLifecycleOwner, nearByController, (fragment as UpdateMapListener))

    // Initialize the FusedLocationProviderClient to get the user's location
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


    fusedLocationClient.lastLocation.addOnSuccessListener {
      locationUser = LatLng(it.latitude, it.longitude)
      nearByViewModel.location.value = locationUser

    }

    val locationRequest = com.google.android.gms.location.LocationRequest().apply {
      interval = 1000 // Set desired update interval (e.g., 10 seconds)
      fastestInterval = 1000  // Optional: Set minimum interval for faster updates (e.g., 5 seconds)
      priority =
        com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY  // Specify desired location accuracy
    }


    val locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)
        val currentLocation = locationResult.lastLocation
        if (currentLocation != null) {
          Log.e(
            LOG_KEY,
            "location updated -->" + currentLocation.latitude + currentLocation.longitude
          )
          val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)

          //Todo : Update user location in nearby collection

          val lastGeoLocation = GeoLocation(locationUser.latitude, locationUser.longitude)
          val newGeoLocation = GeoLocation(currentLocation.latitude, currentLocation.longitude)
          val distance = GeoFireUtils.getDistanceBetween(newGeoLocation, lastGeoLocation)
          Log.e(LOG_KEY, distance.toString() + "disnace")
          if (MAX_UPDATE <= distance) {
            moveCamera()
            Log.e(
              LOG_KEY,
              "new radius -->" + currentLocation.latitude + currentLocation.longitude
            )

            Toast.makeText(requireActivity(), "New radius", Toast.LENGTH_SHORT).show()

            locationUser = latLng
            nearByViewModel.location.value = latLng
          }

        }
      }
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // Get the SupportMapFragment and request notification when the map is ready to be used.
    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
    mapFragment?.getMapAsync(this)


    // TODO: Listen to user movement. Update user location but only call controller if user moves for x meters.

  }

  companion object {

    const val TAG = "NearByFragment"

    fun newInstance(): NearByFragment {
      return NearByFragment()
    }
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap

    // Check if the location permissions are granted, if not, request them
    if (ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED &&
      ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        requireActivity(),
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        1
      )
      return
    }
    // Enable the My Location layer on the map
    mMap.isMyLocationEnabled = true
//    mMap.setOnMarkerClickListener { marker ->
//      Log.e(LOG_KEY,marker.toString())
//      true
//    }

//     Move the camera to the user's current location with a zoom level to show 100 meters radius
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationUser, getZoomLevel(100.0)))

    // Add a circle with a 100-meter radius around the user's current location
  userCicle=  mMap.addCircle(
      CircleOptions()
        .center(locationUser)
        .radius(CIRCLE_RADIUS.toDouble())
        .strokeColor(R.color.black) // Transparent Blue Border
        .fillColor(R.color.teal_200)
    ) // Transparent Blue Fill
  }


  fun moveCamera() {

    userCicle?.remove()
    //  Move the camera to the user's current location with a zoom level to show 100 meters radius
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationUser, getZoomLevel(100.0)))

    // Add a circle with a 100-meter radius around the user's current location
    userCicle = mMap.addCircle(
      CircleOptions()
        .center(locationUser)
        .radius(CIRCLE_RADIUS.toDouble())
        .strokeColor(R.color.black) // Transparent Blue Border
        .fillColor(R.color.teal_200)
    ) // Transparent Blue Fill
  }

  // Get the user's last known location
//    fusedLocationClient.lastLocation
//      .addOnSuccessListener { location: Location? ->
//        location?.let {
//          // Create a LatLng object with the user's current location
//          val currentLocation = LatLng(it.latitude, it.longitude)
//
//          nearByController.listenToNearbyUsers(GeoLocation(it.latitude,it.longitude),100.0){
//              Log.e(LOG_KEY,it.userId + " result ppp")
//          }
//          // Add a marker at the user's current location
//          mMap.addMarker(MarkerOptions().position(currentLocation).title("You are here")
//            .icon(bitmapDescriptorFromVector(R.drawable.nearby_24)))
//           // Replace with your drawable resource
//
//
//          // Move the camera to the user's current location with a zoom level to show 100 meters radius
//          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, getZoomLevel(100.0)))
//
//          // Add a circle with a 100-meter radius around the user's current location
//          mMap.addCircle(
//            CircleOptions()
//              .center(currentLocation)
//              .radius(100.0)
//              .strokeColor(R.color.black) // Transparent Blue Border
//              .fillColor(R.color.teal_200)
//          ) // Transparent Blue Fill
//
//          // Define the other user's location (50 meters to the north)
//          var otherUserLocation = LatLng(13.689798, 74.659269)
//
//          // Add a marker at the other user's location
//          mMap.addMarker(MarkerOptions().position(otherUserLocation).title("Other User")
//            .icon(bitmapDescriptorFromVector(R.drawable.home_24)))
//           otherUserLocation = LatLng(13.689794, 74.659269)
//
//          // Add a marker at the other user's location
//          mMap.addMarker(MarkerOptions().position(otherUserLocation).title(" User 3")
//            .icon(bitmapDescriptorFromVector(R.drawable.notifications_24)))
//
//
//
//        }
//      }

  // Calculate the appropriate zoom level for the specified radius in meters
  private fun getZoomLevel(radius: Double): Float {
    val scale = radius / 500.0
    return (16 - Math.log(scale) / Math.log(2.0)).toFloat()
  }

  // Handle the result of the permission request
  @Deprecated("Deprecated in Java")
  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray,
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == 1) {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        onMapReady(mMap)
      }
    }
  }

  // Function to convert a vector drawable to a BitmapDescriptor
  private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
    val vectorDrawable: Drawable? = ContextCompat.getDrawable(requireContext(), vectorResId)
    vectorDrawable?.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
      vectorDrawable!!.intrinsicWidth,
      vectorDrawable.intrinsicHeight,
      Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
  }

  override fun updateMap(profile: Profile, outOfBound: Boolean) {

    if (outOfBound) {
      Log.e(LOG_KEY, profile.userId + " out")
      val existingMarker = userMarkers.remove(profile.userId)
      existingMarker?.remove()
      return
    }
    val otherUserLocation = LatLng(profile.location.latitude, profile.location.longitude)

    val existingMarker = userMarkers.remove(profile.userId)
    existingMarker?.remove() // Remove marker from the map if it exists

    // Add a marker at the other user's location
    val newMarker =
      mMap.addMarker(MarkerOptions().position(otherUserLocation).title(profile.userId))
    userMarkers[profile.userId] = newMarker!!

    Log.e(LOG_KEY, userMarkers.size.toString() + ",--- size")
  }

}
