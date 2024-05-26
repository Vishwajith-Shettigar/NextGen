package com.example.domain.nearby

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.constants.LOG_KEY
import com.example.utility.GeoUtils
import com.example.model.GeoPoint
import com.example.model.Profile
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ln
import kotlinx.coroutines.*

const val NEAEBY_USERS_COLLECTION = "nearby_users"

@Singleton
class NearByController @Inject constructor(
  private val firestore: FirebaseFirestore,
) {
  private val _nearbyUsers = MutableLiveData<List<Profile>>()
  val nearbyUsers: LiveData<List<Profile>> get() = _nearbyUsers
  private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  fun listenToNearbyUsers(
    center: GeoLocation,
    radiusInMeter: Double,
    updateNearbyUsers: (List<Profile>) -> Unit,
  ) {
    ioScope.launch {
      val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInMeter)
      val queries = mutableListOf<Query>()

      for (bound in bounds) {
        val query = firestore.collection(NEAEBY_USERS_COLLECTION)
          .orderBy("geohash")
          .startAt(bound.startHash)
          .endAt(bound.endHash)
        queries.add(query)
      }

      Log.e(LOG_KEY, bounds.size.toString())

      val nearbyUsers = mutableListOf<Profile>()
      queries.forEach { query ->

        query.addSnapshotListener { snapShot, e ->
          if (e != null) {
            Log.e(LOG_KEY, e.toString())
            return@addSnapshotListener
          }

          Log.e(LOG_KEY, snapShot?.size().toString())

          snapShot?.documents?.forEach { document ->

            val location = document.getGeoPoint("location") ?: return@forEach

            val lat = location.latitude
            val lng = location.longitude
            val docLocation = GeoLocation(lat, lng)
            val distanceMeter = GeoFireUtils.getDistanceBetween(docLocation, center)
            if (distanceMeter <= radiusInMeter) {
              val profile = getProfile(document)
              nearbyUsers.add(profile)
            } else
              Log.e(LOG_KEY, "out of range" + document.getString("userName")!!)
          }
        }
      }
      withContext(Dispatchers.Main) {
        _nearbyUsers.value = nearbyUsers
      }
    }
  }

  private fun getProfile(document: DocumentSnapshot): Profile {
    return Profile.newBuilder().apply {
      this.userId = document.getString("userId")
      this.userName = document.getString("userName")
      this.firstName = document.getString("firstName")
      this.lastName = document.getString("lastName")
      this.location = GeoPoint.newBuilder().apply {
        this.latitude = document.getGeoPoint("location")!!.latitude
        this.longitude = document.getGeoPoint("location")!!.longitude
      }.build()
    }.build()
  }
}
