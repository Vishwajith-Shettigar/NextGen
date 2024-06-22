package com.example.domain.nearby

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.constants.LOG_KEY
import com.example.domain.constants.USERS_COLLECTION
import com.example.utility.GeoUtils
import com.example.model.GeoPoint
import com.example.model.Privacy
import com.example.model.Profile
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.type.LatLng
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ln
import kotlinx.coroutines.*

const val NEAEBY_USERS_COLLECTION = "nearby_users"

@Singleton
class NearByController @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth
) {
  private val ioScope = CoroutineScope(Dispatchers.IO)
  val nearbyUsers = mutableListOf<Profile>()
  private var activeListeners = mutableListOf<ListenerRegistration>()

  // Todo: 1. For dynamic changing center parameter, to listen to new center, remove old listener, create new one.
  // Todo: 1. Completed-->Pending Testing.
//  fun listenToNearbyUsers(
//    center: GeoLocation,
//    radiusInMeter: Double,
//    updateNearbyUsers: (Profile, Boolean) -> Unit,
//  ) {
//    // Cancel any existing listeners.
//    activeListeners.forEach { it.remove() }
//    activeListeners.clear()
//    ioScope.launch {
//      val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInMeter)
//      val queries = mutableListOf<Query>()
//      for (bound in bounds) {
//        val query = firestore.collection(NEAEBY_USERS_COLLECTION)
//          .orderBy("geohash")
//          .startAt(bound.startHash)
//          .endAt(bound.endHash)
//        queries.add(query)
//      }
//      queries.forEach { query ->
//        val listener = query.addSnapshotListener { snapShot, e ->
//          if (e != null) {
//            Log.e(LOG_KEY, e.toString())
//            return@addSnapshotListener
//          }
//          snapShot?.documents?.forEach { document ->
//            val location = document.getGeoPoint("location") ?: return@forEach
//            val lat = location.latitude
//            val lng = location.longitude
//            val docLocation = GeoLocation(lat, lng)
//            val distanceMeter = GeoFireUtils.getDistanceBetween(docLocation, center)
//            val profile = getProfile(document)
//            if (distanceMeter <= radiusInMeter) {
//
//              Log.e(LOG_KEY, profile.userId + "--> reasult-id")
//              val iterator = nearbyUsers.iterator()
//
//              // Indicator whether new profile is already in list or not
//              var found = false
//
//              // If profile exists in list and if location also same,
//              // dont callback, otherwise replce old profile with new profile and callback
//              while (iterator.hasNext()) {
//                val existingProfile = iterator.next()
//                if (existingProfile.userId == profile.userId) {
//                  found = true
//                  if (existingProfile.location.latitude == profile.location.latitude &&
//                    existingProfile.location.longitude == profile.location.longitude
//                  ) {
//                    // Location matches so no need to update list or callback.
//                    break
//                  } else {
//                    // Location does not match, so replce old profile
//                    // with new profile and callback.
//                    iterator.remove()
//                    nearbyUsers.add(profile)
//                    updateNearbyUsers(profile, false)
//                    break
//                  }
//                }
//              }
//              // Add profile to list and callback.
//              if (!found) {
//                nearbyUsers.add(profile)
//                updateNearbyUsers(profile, false)
//              }
//            } else {
//              var flag: Boolean = false
//              nearbyUsers.mapIndexed { index, it ->
//                if (profile.userId == it.userId) {
//                  flag = true
//                  nearbyUsers.removeAt(index)
//                }
//              }
//              if (flag)
//                updateNearbyUsers(profile, true)
//
//              Log.e(LOG_KEY, "out of range" + document.getString("userName")!!)
//            }
//          }
//        }
//        activeListeners.add(listener)
//      }
//    }
//  }

//  fun listenToNearbyUsers(
//    center: GeoLocation,
//    radiusInMeter: Double,
//    updateNearbyUsers: (Profile, Boolean) -> Unit,
//  ) {
//    // Cancel any existing listeners.
//    activeListeners.forEach { it.remove() }
//    activeListeners.clear()
//    ioScope.launch {
//      val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInMeter)
//      val queries = mutableListOf<Query>()
//      for (bound in bounds) {
//        val query = firestore.collection(NEAEBY_USERS_COLLECTION)
//          .orderBy("geohash")
//          .startAt(bound.startHash)
//          .endAt(bound.endHash)
//        queries.add(query)
//      }
//      queries.forEach { query ->
//        val listener = query.addSnapshotListener { snapShot, e ->
//          if (e != null) {
//            Log.e(LOG_KEY, e.toString())
//            return@addSnapshotListener
//          }
//          snapShot?.documents?.forEach { document ->
//            val location = document.getGeoPoint("location") ?: return@forEach
//            val lat = location.latitude
//            val lng = location.longitude
//            val docLocation = GeoLocation(lat, lng)
//            val distanceMeter = GeoFireUtils.getDistanceBetween(docLocation, center)
//            val profile = getProfile(document)
//            if (distanceMeter <= radiusInMeter) {
//              val copyOfNearbyUsers = ArrayList(nearbyUsers)  // Create a copy
//              var found = false
//              copyOfNearbyUsers.forEach { existingProfile ->  // Iterate over copy
//                if (existingProfile.userId == profile.userId) {
//                  found = true
//                  if (existingProfile.location.latitude == profile.location.latitude &&
//                    existingProfile.location.longitude == profile.location.longitude
//                  ) {
//                    // Location matches so no need to update list or callback.
//                    return@forEach
//                  } else {
//                    // Location does not match, so replace old profile with new profile
//                    copyOfNearbyUsers.remove(existingProfile)
//                  }
//                }
//              }
//              if (!found) {
//                copyOfNearbyUsers.add(profile)
//              }
//              nearbyUsers.clear()  // Update original list
//              nearbyUsers.addAll(copyOfNearbyUsers)
//              updateNearbyUsers(profile, false)
//            } else {
//              nearbyUsers.removeAll { it.userId == profile.userId }
//              updateNearbyUsers(profile, true)
//              Log.e(LOG_KEY, "out of range" + document.getString("userName")!!)
//            }
//          }
//        }
//        activeListeners.add(listener)
//      }
//    }
//  }


  // Refined nearby listener yet.
  // Todo: Create new field online, if not remove from map, if yes add to map.

//  fun listenToNearbyUsers(
//    center: GeoLocation,
//    radiusInMeter: Double,
//    updateNearbyUsers: (Profile, Boolean) -> Unit,
//  ) {
//    // Cancel any existing listeners.
//    activeListeners.forEach { it.remove() }
//    activeListeners.clear()
//
//    ioScope.launch {
//      val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInMeter)
//      val queries = mutableListOf<Query>()
//      for (bound in bounds) {
//        val query = firestore.collection(com.example.domain.constants.NEAEBY_USERS_COLLECTION)
//          .whereEqualTo("disableLocation", false)
//          .orderBy("geohash")
//          .startAt(bound.startHash)
//          .endAt(bound.endHash)
//        queries.add(query)
//      }
//
//      queries.forEach { query ->
//        val listener = query.addSnapshotListener { snapshot, e ->
//          if (e != null) {
//            Log.e(LOG_KEY, e.toString())
//            return@addSnapshotListener
//          }
//
//          if (snapshot != null) {
//            val newNearbyUsers = ArrayList<Profile>()
//            val usersToRemove = ArrayList<Profile>()
//
//            snapshot.documents.forEach { document ->
//              val location = document.getGeoPoint("location") ?: return@forEach
//              val lat = location.latitude
//              val lng = location.longitude
//              val docLocation = GeoLocation(lat, lng)
//              val distanceMeter = GeoFireUtils.getDistanceBetween(docLocation, center)
//              val profile = getProfile(document)
//
//              Log.e(LOG_KEY,profile.privacy.disableLocation.toString())
//              if (distanceMeter <= radiusInMeter && !profile.privacy.disableLocation ) {
//                val existingProfile = nearbyUsers.find { it.userId == profile.userId }
//                if (existingProfile != null) {
//                  if (existingProfile.location.latitude == profile.location.latitude &&
//                    existingProfile.location.longitude == profile.location.longitude) {
//                    // Location matches so no need to update list or callback.
//                    newNearbyUsers.add(existingProfile)
//                  } else {
//                    // Location does not match, update profile
//                    newNearbyUsers.add(profile)
//                  }
//                } else {
//                  newNearbyUsers.add(profile)
//                }
//              } else {
//                usersToRemove.add(profile)
//              }
//            }
//
//            // Update the nearbyUsers list after iteration
//            nearbyUsers.clear()
//            nearbyUsers.addAll(newNearbyUsers)
//
//            // Notify about users out of range
//            usersToRemove.forEach { profile ->
//              nearbyUsers.removeAll { it.userId == profile.userId }
//              updateNearbyUsers(profile, true)
//              Log.e(LOG_KEY, "out of range: ${profile.userName}")
//            }
//
//            // Notify about users in range
//            newNearbyUsers.forEach { profile ->
//              updateNearbyUsers(profile, false)
//            }
//          }
//        }
//        activeListeners.add(listener)
//      }
//    }
//  }

  fun listenToNearbyUsers(
    userId: String,
    center: GeoLocation,
    radiusInMeter: Double,
    updateNearbyUsers: (Profile, Boolean) -> Unit,
  ) {
    // Cancel any existing listeners.
    activeListeners.forEach { it.remove() }
    activeListeners.clear()
Log.e(LOG_KEY,userId+"<____________________________")
    ioScope.launch {
      val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInMeter)
      val queries = mutableListOf<Query>()
      for (bound in bounds) {
        val query = firestore.collection(USERS_COLLECTION)
          .whereEqualTo("disableLocation", false).whereNotEqualTo("userId",userId)
          .orderBy("geoHash")
          .startAt(bound.startHash)
          .endAt(bound.endHash)
        queries.add(query)
      }

      queries.forEach { query ->
        val listener = query.addSnapshotListener { snapshot, e ->
          if (e != null) {
            Log.e(LOG_KEY, "Listen failed: ", e)
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
              val profile = getProfile(document)
Log.e(LOG_KEY,profile.userId)
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
              Log.e(LOG_KEY, "Out of range: ${profile.userName}")
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


   fun getProfile(document: DocumentSnapshot): Profile {
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

      // Todo :Here calculate ratings.
      this.rating= document.getDouble("rating")?.toFloat() ?: 0F

      //Todo : implement this
      this.rated=4.5F

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


  fun updateLocation(userId: String, location: Location, oldLocation:Location?) {

    if(auth.currentUser == null ) {
      return
    }

    if(oldLocation!=null) {
      val oldGeoPoint = GeoLocation(oldLocation.latitude, oldLocation.longitude)
      val newGeoPoint = GeoLocation(location.latitude, location.longitude)
      if (GeoFireUtils.getDistanceBetween(oldGeoPoint,newGeoPoint)<20)
        return
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
        Log.e(LOG_KEY, "Error updating Location: ${exception.message}")
      }
  }


//  // nearBysaveusers
//  // todo: merge with general user collection
//  fun saveUsersLocation(profile: Profile) {
//    val latitude = profile.location.latitude
//    val longitude = profile.location.longitude
//
//    val geoHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))
//
//    val userData = hashMapOf(
//      "firstName" to profile.firstName,
//      "lastName" to profile.lastName,
//      "location" to GeoPoint(latitude, longitude),
//      "userId" to profile.userId,
//      "userName" to profile.userName,
//      "geohash" to geoHash
//    )
//
//    // Add the user data to Firestore
//    firestore.collection(NEAEBY_USERS_COLLECTION).document(profile.userId)
//      .set(userData)
//      .addOnSuccessListener {
//        Log.e(LOG_KEY, "User data added successfully")
//      }
//      .addOnFailureListener { e ->
//        Log.e(LOG_KEY, "Error adding user data: $e")
//      }
//  }
}
