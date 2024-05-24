package com.example.domain.nearby

import android.util.Log
import com.example.domain.constants.LOG_KEY
import com.example.domain.nearby.utility.GeoUtils
import com.example.model.GeoPoint
import com.example.model.Profile
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ln

const val NEAEBY_USERS_COLLECTION = "nearby_users"

@Singleton
class NearByController @Inject constructor(
  private val firestore: FirebaseFirestore,
) {


  fun listenToNearbyUsers(
    center: GeoLocation,
    radiusInMeter: Double,
    updateNearbyUsers: (List<Profile>) -> Unit,
  ) {

    Log.e(LOG_KEY,"helloo")
    val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInMeter)
    val queries = mutableListOf<Query>()


    for (bound in bounds) {
      val query = firestore.collection(NEAEBY_USERS_COLLECTION)
        .orderBy("geohash")
        .startAt(bound.startHash)
        .endAt(bound.endHash)

      queries.add(query)

    }
    Log.e(LOG_KEY,bounds.size.toString())

    queries.forEach { query ->

      query.addSnapshotListener { snapShot, e ->

        if (e != null) {
          Log.e(LOG_KEY, e.toString())
          return@addSnapshotListener
        }
        val nearbyUsers: MutableList<Profile> = mutableListOf()
        Log.e(LOG_KEY,snapShot?.size().toString())

        snapShot?.documents?.forEach { document ->

          val location = document.getGeoPoint("location")
          if (location == null) {
            return@forEach
          }

          val lat = location.latitude
          val lng = location.longitude
          val docLocation = GeoLocation(lat, lng)
          val distanceMeter = GeoFireUtils.getDistanceBetween(docLocation, center)
          if (distanceMeter <= radiusInMeter) {
            Log.e(LOG_KEY, document.getString("userName")!!)
            val profile = Profile.newBuilder().apply {
              this.userId = document.getString("userId")
              this.userName = document.getString("userName")
              this.firstName = document.getString("firstName")
              this.lastName = document.getString("lastName")
              this.location = GeoPoint.newBuilder().apply {
                this.latitude = lat
                this.longitude = lng
              }.build()
            }.build()

            nearbyUsers.add(profile)
          }else
          Log.e(LOG_KEY, "out of range"+document.getString("userName")!!)


        }


      }

    }


  }

}