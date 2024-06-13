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
import android.graphics.*
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
import com.example.utility.getProto
import com.example.utility.putProto
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

  private lateinit var profile: Profile

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
    profile = arguments?.getProto(NEARBYFRAGMENT_ARGUMENTS_KEY, Profile.getDefaultInstance())
      ?: Profile.getDefaultInstance()
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
          Log.e(LOG_KEY, latLng.toString() + "  update location")

          moveUser(latLng)
          val lastGeoLocation = GeoLocation(locationUser.latitude, locationUser.longitude)
          val newGeoLocation = GeoLocation(it.latitude, it.longitude)
          val distance = GeoFireUtils.getDistanceBetween(newGeoLocation, lastGeoLocation)
          if (MAX_UPDATE <= distance && it.accuracy <= MAX_UPDATE) {
            Log.e(LOG_KEY, locationUser.toString() + "  old")

            Log.e(LOG_KEY, latLng.toString() + "  new")
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
  }

  companion object {

    const val NEARBYFRAGMENT_ARGUMENTS_KEY = "NearByFragment.arguments"

    const val TAG = "NearByFragment"
    fun newInstance(profile: Profile): NearByFragment {
      val nearByFragment = NearByFragment()
      nearByFragment.arguments?.apply {
        this.putProto(NEARBYFRAGMENT_ARGUMENTS_KEY, profile)
      }
      return nearByFragment
    }
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
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
    mMap.isMyLocationEnabled = true
    moveCamera(locationUser)
    mMap.setOnMarkerClickListener { marker ->
      val profile = marker.tag as Profile
      Log.e(LOG_KEY, profile.toString())
      true
    }
  }

  private fun moveUser(location: LatLng) {


    CoroutineScope(Dispatchers.IO).launch {
      // Load profile picture asynchronously using loadBitmapFromUrl
      val bitmap = loadBitmapFromUrl(profile.imageUrl)
      val resizedBitmap = bitmap?.let { resizeBitmap(it, 50, 50) }
      val roundedBitmap = resizedBitmap?.let { getRoundedBitmap(it) }

      withContext(Dispatchers.Main) {
        usermarker?.remove()
        val bitmapDescriptor = roundedBitmap?.let { BitmapDescriptorFactory.fromBitmap(it) }
          ?: BitmapDescriptorFactory.defaultMarker()
        usermarker = mMap.addMarker(
          MarkerOptions().position(location).title("You are here")
            .icon(bitmapDescriptor)
        )
        usermarker?.tag = profile
      }
    }
  }

  private fun moveCamera(location: LatLng) {
    userCircle?.remove()
    mMap.moveCamera(
      CameraUpdateFactory.newLatLngZoom(
        location,
        getZoomLevel(CIRCLE_RADIUS.toDouble())
      )
    )
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
  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray,
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      onMapReady(mMap)
    }
  }

  private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
    val context = context ?: return null
    val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
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
    CoroutineScope(Dispatchers.IO).launch {
      if (outOfBound) {
        // Remove marker if out of bound
        userMarkers.remove(profile.userId)?.remove()
      } else {


        // Load profile picture asynchronously using loadBitmapFromUrl
        val bitmap = loadBitmapFromUrl(profile.imageUrl)
        val resizedBitmap = bitmap?.let { resizeBitmap(it, 50, 50) }
        val roundedBitmap = resizedBitmap?.let { getRoundedBitmap(it) }

        withContext(Dispatchers.Main) {

          val otherUserLocation = LatLng(profile.location.latitude, profile.location.longitude)
          userMarkers.remove(profile.userId)?.remove()

          val bitmapDescriptor = roundedBitmap?.let { BitmapDescriptorFactory.fromBitmap(it) }
            ?: BitmapDescriptorFactory.defaultMarker()

          val newMarker = mMap.addMarker(
            MarkerOptions()
              .position(otherUserLocation)
              .icon(bitmapDescriptor)
              .title(profile.userId)
          )!!
          newMarker.tag = profile // Attach profile object as tag to the marker
          userMarkers[profile.userId] = newMarker
        }
      }
      Log.e(LOG_KEY, "${userMarkers.size} markers on map")
    }
  }

  // Function to load bitmap from URL using Picasso
  suspend fun loadBitmapFromUrl(url: String?): Bitmap? {
    if (url.isNullOrEmpty()) return null

    return withContext(Dispatchers.IO) {
      try {
        Log.e(LOG_KEY, url)
        // Asynchronously load bitmap from URL using Picasso
        val request: RequestCreator = Picasso.get().load(url)
        request.get()
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }
  }

  // Function to create a circular bitmap from a given bitmap
  // Function to create a circular bitmap with border from a given bitmap
  private fun getRoundedBitmap(bitmap: Bitmap): Bitmap {
    val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val paint = Paint()
    val rect = Rect(0, 0, bitmap.width, bitmap.height)

    val halfBorderWidth = 5 / 2f

    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.style = Paint.Style.FILL
    canvas.drawCircle(
      bitmap.width / 2f,
      bitmap.height / 2f,
      (bitmap.width / 2f),
      paint
    )

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)
    paint.xfermode = null
    paint.style = Paint.Style.STROKE
    paint.color = Color.RED
    paint.strokeWidth = 5F
    canvas.drawCircle(
      (bitmap.width / 2f),
      (bitmap.height / 2f),
      (bitmap.width / 2f) - halfBorderWidth,
      paint
    )
    return output
  }


  // Function to resize a bitmap to the specified width and height
  private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(bitmap, width, height, false)
  }

}
