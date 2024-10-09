package com.example.domain.nearby

import android.location.Location
import com.example.domain.constants.USERS_COLLECTION
import com.example.model.Privacy
import com.example.model.Profile
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton
import com.example.model.GeoPoint

const val NEAEBY_USERS_COLLECTION = "nearby_users"

@Singleton
class NearByController @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth,
) {
  private val ioScope = CoroutineScope(Dispatchers.IO)
  val nearbyUsers = mutableListOf<Profile>()
  private var activeListeners = mutableListOf<ListenerRegistration>()

  fun listenToNearbyUsers(
    userId: String,
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
        val query = firestore.collection(USERS_COLLECTION)
          .whereEqualTo("disableLocation", false).whereNotEqualTo("userId", userId)
          .orderBy("geoHash")
          .startAt(bound.startHash)
          .endAt(bound.endHash)
        queries.add(query)
      }

      queries.forEach { query ->
        val listener = query.addSnapshotListener { snapshot, e ->
          if (e != null) {
            return@addSnapshotListener
          }

          if (snapshot != null) {
            val newNearbyUsers = ArrayList<Profile>()
            val usersToRemove = ArrayList<Profile>()

            snapshot.documentChanges.forEach { documentChange ->

              val document = documentChange.document
              val location = document.getGeoPoint("location") ?: return@forEach
              val lat = location.latitude
              val lng = location.longitude
              val docLocation = GeoLocation(lat, lng)
              val distanceMeter = GeoFireUtils.getDistanceBetween(docLocation, center)
              val profile = getProfile(userId, document)
              if (distanceMeter <= radiusInMeter && !profile.privacy.disableLocation) {
                when (documentChange.type) {
                  DocumentChange.Type.ADDED,
                  DocumentChange.Type.MODIFIED,
                  -> {
                    val existingProfile = nearbyUsers.find { it.userId == profile.userId }
                    if (existingProfile != null) {
                      if (existingProfile.location.latitude == profile.location.latitude &&
                        existingProfile.location.longitude == profile.location.longitude
                      ) {
                        // Location matches, no need to update list or callback.
                        newNearbyUsers.add(existingProfile)
                      } else {
                        // Location does not match, update profile
                        newNearbyUsers.add(profile)
                        updateNearbyUsers(profile, false) // Notify update
                      }
                    } else {
                      newNearbyUsers.add(profile)
                      updateNearbyUsers(profile, false) // Notify new addition
                    }
                  }
                  DocumentChange.Type.REMOVED -> {
                    usersToRemove.add(profile)
                  }
                }
              } else {
                usersToRemove.add(profile)
              }
            }

            // Notify about users out of range
            usersToRemove.forEach { profile ->
              nearbyUsers.removeAll { it.userId == profile.userId }
              updateNearbyUsers(profile, true)
            }

            // Update the nearbyUsers list after iteration
            nearbyUsers.clear()
            nearbyUsers.addAll(newNearbyUsers)
          }
        }
        activeListeners.add(listener)
      }
    }
  }

  fun getProfile(userId: String, document: DocumentSnapshot): Profile {
    return Profile.newBuilder().apply {
      this.userId = document.getString("userId")
      this.userName = document.getString("username")
      document.getString("firstName")?.let {
        this.firstName = it
      }
      document.getString("lastName")?.let {
        this.lastName = it
      }
      document.getString("imageUrl")?.let {
        this.imageUrl = it
      }

      document.getString("bio")?.let {
        this.bio = it
      }

      this.rated = 0F

      val ratings = document.get("ratings") as? Map<*, *>

      var totalRating = 0.0f
      var numberOfRatings = 0

      if (ratings != null) {
        for ((key, value) in ratings) {
          if (value is Map<*, *>) {
            var rating: Double? = null
            value.get("rating")?.let {
              rating = it as Double
            }
            if (rating != null) {
              if (key == userId)
                rated = rating!!.toFloat()
              totalRating += rating!!.toFloat()
              numberOfRatings++
            }
          }
        }
      }
      this.rating = if (numberOfRatings > 0) {
        val averageRating = totalRating / numberOfRatings
        String.format("%.1f", averageRating).toFloat()
      } else {
        0.0f
      }

      this.location = GeoPoint.newBuilder().apply {
        this.latitude = document.getGeoPoint("location")!!.latitude
        this.longitude = document.getGeoPoint("location")!!.longitude
      }.build()
      this.privacy = Privacy.newBuilder().apply {
        this.disableLocation = document.getBoolean("disableLocation") ?: false
        this.disableProfilePicture = document.getBoolean("disableProfilePicture") ?: false
        this.disableChat = document.getBoolean("disableChat") ?: false
      }.build()
    }.build()
  }

  fun updateLocation(userId: String, location: Location, oldLocation: Location?) {
    CoroutineScope(Dispatchers.IO).launch {
      if (auth.currentUser == null) {
        return@launch
      }

      if (oldLocation != null) {
        val oldGeoPoint = GeoLocation(oldLocation.latitude, oldLocation.longitude)
        val newGeoPoint = GeoLocation(location.latitude, location.longitude)
        if (GeoFireUtils.getDistanceBetween(oldGeoPoint, newGeoPoint) < 10)
          return@launch
      }

      val latitude = location.latitude
      val longitude = location.longitude

      val geoHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))

      val doc = firestore.collection(USERS_COLLECTION).document(userId)

      doc.update("geoHash", geoHash)
        .addOnSuccessListener {

        }
        .addOnFailureListener { exception ->
        }

      doc.update("location", GeoPoint(latitude, longitude))
        .addOnSuccessListener {
        }
        .addOnFailureListener { exception ->
        }
   }
  }
}
