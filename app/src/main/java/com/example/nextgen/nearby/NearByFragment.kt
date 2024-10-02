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
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.domain.chat.ChatController
import com.example.domain.constants.CIRCLE_RADIUS
import com.example.domain.constants.LOG_KEY
import com.example.domain.constants.MAX_UPDATE
import com.example.domain.nearby.NearByController
import com.example.domain.profile.ProfileController
import com.example.model.Chat
import com.example.model.LastMessageInfo
import com.example.model.Profile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentNearByBinding
import com.example.nextgen.databinding.NearbyProfileDialogBinding
import com.example.nextgen.home.ChatSummaryClickListener
import com.example.nextgen.viewprofile.RouteToViewProfile
import com.example.utility.Result
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
import kotlinx.coroutines.*

class NearByFragment : BaseFragment(), OnMapReadyCallback, UpdateMapListener {

  @Inject
  lateinit var nearByController: NearByController

  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var chatController: ChatController

  @Inject
  lateinit var profileController: ProfileController

  private lateinit var mMap: GoogleMap
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  private lateinit var nearByViewModel: NearByViewModel
  private lateinit var binding: FragmentNearByBinding

  private var locationUser: LatLng? = null

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
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    binding = FragmentNearByBinding.inflate(inflater, container, false)
    profile = arguments?.getProto(NEARBYFRAGMENT_ARGUMENTS_KEY, Profile.getDefaultInstance())
      ?: Profile.getDefaultInstance()

    nearByViewModel = NearByViewModel(profile.userId, viewLifecycleOwner, nearByController, this)


    return binding.root
  }

  @SuppressLint("MissingPermission")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    fusedLocationClient.lastLocation.addOnSuccessListener {
      it?.let {
        locationUser = LatLng(it.latitude, it.longitude)
        nearByViewModel.location.value = locationUser
      }
    }

    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
    mapFragment?.getMapAsync(this@NearByFragment)

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
          moveUser(latLng)
          var distance = 0.0
          if (locationUser != null) {
            val lastGeoLocation = GeoLocation(locationUser!!.latitude, locationUser!!.longitude)

            val newGeoLocation = GeoLocation(it.latitude, it.longitude)
            distance = GeoFireUtils.getDistanceBetween(newGeoLocation, lastGeoLocation)
          }
          if (MAX_UPDATE <= distance && it.accuracy <= MAX_UPDATE) {
            moveCamera(latLng)
            locationUser = latLng
            nearByViewModel.location.value = latLng
          }
        }
      }
    }
    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
  }
  private fun addMarker(latLng: LatLng, imageUrl: String?) {
    // Create MarkerOptions
    val markerOptions = MarkerOptions()
      .position(latLng)
      .title("Marker Title") // Optional
      .snippet("Marker Snippet") // Optional

    // Launch a coroutine to load the bitmap
    CoroutineScope(Dispatchers.Main).launch {
      // Load bitmap asynchronously from URL
      val bitmapDescriptor = if (!imageUrl.isNullOrEmpty()) {
        val bitmap = loadBitmapFromUrl(imageUrl) a

        // Use loaded bitmap if available; otherwise, use default image
        if (bitmap != null) {
          BitmapDescriptorFactory.fromBitmap(bitmap)
        } else {
          BitmapDescriptorFactory.fromResource(R.drawable.person_24) // Default image
        }
      } else {
        BitmapDescriptorFactory.fromResource(R.drawable.person_24) // Default image
      }

      // Set the icon for the marker
      markerOptions.icon(bitmapDescriptor)

      // Add the marker to the map
      mMap.addMarker(markerOptions)
    }
  }
  fun checkLocationPermission() {

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
      val nearByFragment = NearByFragment().apply {
        arguments = Bundle().apply {
          this.putProto(NEARBYFRAGMENT_ARGUMENTS_KEY, profile)
        }
      }
      return nearByFragment
    }
  }


  @SuppressLint("SuspiciousIndentation")
  override fun onMapReady(googleMap: GoogleMap) {


    try {
      mMap = googleMap

      checkLocationPermission()

      mMap.isMyLocationEnabled = true
      if (locationUser != null) {
        moveCamera(locationUser!!)

        // Call addMarker here
        val imageUrl= profile.imageUrl
        if(imageUrl.isNullOrEmpty())
          addMarker(locationUser!!, imageUrl) // Add marker at the user's location
      }

      mMap.setOnMarkerClickListener { marker ->
        val userProfile = marker.tag as Profile
        if (userProfile.userId!=profile.userId)
        showNearByProfileDialog(userProfile)
        true
      }

    } catch (e: Exception) {
        Toast.makeText(
          activity,
          "Please give access to location to use this feature",
          Toast.LENGTH_LONG
        ).show()
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
        withContext(Dispatchers.Main) {
          userMarkers.remove(profile.userId)?.remove()
        }
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
    }
  }

  // Function to load bitmap from URL using Picasso
  suspend fun loadBitmapFromUrl(url: String?): Bitmap? {
    if (url.isNullOrEmpty()) return null

    return withContext(Dispatchers.IO) {
      try {
        // Asynchronously load bitmap from URL using Picasso
        val request: RequestCreator = Picasso.get().load(url)
        request.get()
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }
  }

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

  fun showNearByProfileDialog(viewProfile: Profile) {

    CoroutineScope(Dispatchers.IO).launch {
      val isChatExists = chatController.isChatExists(profile.userId, viewProfile.userId)

      withContext(Dispatchers.Main) {
        val dialog = Dialog(requireContext())
        val layoutbinding = NearbyProfileDialogBinding.inflate(layoutInflater)
        dialog.setContentView(layoutbinding.root)
        dialog.window?.setLayout(600, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (viewProfile.imageUrl.isNullOrBlank() || viewProfile.privacy.disableProfilePicture)
          Picasso.get().load(R.drawable.profile_placeholder).into(layoutbinding.profilePicture)
        else {
          Picasso.get().load(viewProfile.imageUrl).into(layoutbinding.profilePicture)
        }

        layoutbinding.username.text = viewProfile.userName

        if (viewProfile.privacy.disableChat) {
          layoutbinding.chatIcon.isEnabled = false
          layoutbinding.chatIcon.alpha = 0.2F
        }

        layoutbinding.parentLayout.setOnClickListener {
          (activity as RouteToViewProfile).routeToViewProfile(viewProfile)
        }

        layoutbinding.chatIcon.setOnClickListener {
          if (isChatExists.isNullOrBlank()) {
            layoutbinding.messageParent.visibility = View.VISIBLE
          } else {
            val chat = Chat.newBuilder().apply {
              this.chatId = isChatExists
              this.userId = viewProfile.userId
              this.imageUrl = viewProfile.imageUrl ?: ""
              this.userName = viewProfile.userName ?: ""
            }.build()
            (activity as ChatSummaryClickListener).onChatSummaryClicked(chat)
          }
        }

        layoutbinding.buttonNo.setOnClickListener {
          layoutbinding.messageParent.visibility = View.GONE
        }

        layoutbinding.buttonYes.setOnClickListener {
          if (isChatExists.isNullOrBlank()) {
            chatController.initiateChat(profile.userId, viewProfile.userId) {
              if (it is Result.Success) {
                sendMesssage(viewProfile, it.data)
              } else {
                Toast.makeText(requireContext(), "Something went wrong !", Toast.LENGTH_SHORT)
                  .show()
              }
            }
          }
        }

        dialog.show()
      }
    }
  }

  fun sendMesssage(viewProfile: Profile, chatId: String) {
    CoroutineScope(Dispatchers.IO).launch {
      chatController.sendMessage(chatId, profile.userId, viewProfile.userId, "Hi!") {
        if (it is Result.Success) {
          val chat = Chat.newBuilder().apply {
            this.chatId = chatId
            this.userId = viewProfile.userId
            this.imageUrl = viewProfile.imageUrl ?: ""
            this.userName = viewProfile.userName ?: ""
          }.build()
          (activity as ChatSummaryClickListener).onChatSummaryClicked(chat)
        } else {
          Toast.makeText(requireContext(), "Something went wrong !", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }
}
