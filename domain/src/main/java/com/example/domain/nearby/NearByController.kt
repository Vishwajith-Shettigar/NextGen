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
  private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  val nearbyUsers = mutableListOf<Profile>()
  private var activeListeners = mutableListOf<ListenerRegistration>()

  // Todo: 1. For dynamic changing center parameter, to listen to new center, remove old listener, create new one.
  // Todo: 1. Completed-->Pending Testing.
  fun listenToNearbyUsers(
    center: GeoLocation,
    radiusInMeter: Double,
    updateNearbyUsers: (Profile, Boolean) -> Unit,
  ) {
    // Cancel any existing listeners.
    activeListeners.forEach { it.remove() }
    activeListeners.clear()
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
      queries.forEach { query ->
        val listener = query.addSnapshotListener { snapShot, e ->
          if (e != null) {
            Log.e(LOG_KEY, e.toString())
            return@addSnapshotListener
          }
          snapShot?.documents?.forEach { document ->
            val location = document.getGeoPoint("location") ?: return@forEach
            val lat = location.latitude
            val lng = location.longitude
            val docLocation = GeoLocation(lat, lng)
            val distanceMeter = GeoFireUtils.getDistanceBetween(docLocation, center)
            val profile = getProfile(document)
            if (distanceMeter <= radiusInMeter) {

              Log.e(LOG_KEY, profile.userId + "--> reasult-id")
              val iterator = nearbyUsers.iterator()

              // Indicator whether new profile is already in list or not
              var found = false

              // If profile exists in list and if location also same,
              // dont callback, otherwise replce old profile with new profile and callback
              while (iterator.hasNext()) {
                val existingProfile = iterator.next()
                if (existingProfile.userId == profile.userId) {
                  found = true
                  if (existingProfile.location.latitude == profile.location.latitude &&
                    existingProfile.location.longitude == profile.location.longitude
                  ) {
                    // Location matches so no need to update list or callback.
                    break
                  } else {
                    // Location does not match, so replce old profile
                    // with new profile and callback.
                    iterator.remove()
                    nearbyUsers.add(profile)
                    updateNearbyUsers(profile,false)
                    break
                  }
                }
              }
              // Add profile to list and callback.
              if (!found) {
                nearbyUsers.add(profile)
                updateNearbyUsers(profile,false)
              }
            } else {
              var flag:Boolean=false
              nearbyUsers.mapIndexed { index, it ->
                if(profile.userId==it.userId){
                  flag=true
                  nearbyUsers.removeAt(index)
                }
              }
              if (flag)
                updateNearbyUsers(profile,true)

              Log.e(LOG_KEY, "out of range" + document.getString("userName")!!)
            }
          }
        }
        activeListeners.add(listener)
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
