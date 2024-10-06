package com.example.nextgen.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.domain.nearby.NearByController
import com.example.domain.profile.ProfileController
import com.example.nextgen.Application.MyApplication
import com.example.nextgen.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class LocationService
  : Service() {

  @Inject
  lateinit var nearByController: NearByController

  @Inject
  lateinit var profileController: ProfileController

  lateinit var userId: String

  private lateinit var fusedLocationClient: FusedLocationProviderClient
  private lateinit var locationCallback: LocationCallback

  private var oldLocation: Location? = null

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate() {
    super.onCreate()
    (applicationContext as MyApplication).appComponent.inject(this)
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    userId = profileController.getUserId().toString()
    startForeground(1, createNotification())
    startLocationUpdates()
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createNotification(): Notification {
    val notificationChannelId = "LOCATION_SERVICE_CHANNEL"
    val channel = NotificationChannel(
      notificationChannelId,
      "Location Service",
      NotificationManager.IMPORTANCE_DEFAULT
    )
    val manager = getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)

    val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
      .setContentTitle("Location Service")
      .setContentText("Updating location in the background")
      .setSmallIcon(R.drawable.notifications_24)
    return notificationBuilder.build()
  }

  private fun startLocationUpdates() {
    CoroutineScope(Dispatchers.IO).launch {
      val locationRequest = LocationRequest.create().apply {
        interval = 3
        fastestInterval = 3
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
      }

      locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
          locationResult.lastLocation?.let {
            nearByController.updateLocation(userId, it, oldLocation)
            oldLocation = it
          }
        }
      }

      if (ActivityCompat.checkSelfPermission(
          baseContext,
          Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
          baseContext,
          Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return@launch
      }
      fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
      )
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    fusedLocationClient.removeLocationUpdates(locationCallback)
    stopForeground(true)
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }
}
