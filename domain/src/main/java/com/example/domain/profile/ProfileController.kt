package com.example.domain.profile

import com.example.domain.nearby.NEAEBY_USERS_COLLECTION
import com.example.model.Profile
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileController @Inject constructor(
  private val firestore: FirebaseFirestore,
) {

  fun saveUsers(profile: Profile){
    val latitude = profile.location.latitude
    val longitude =profile.location.longitude

    val geoHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))

    val userData = hashMapOf(
      "firstName" to profile.firstName,
      "lastName" to profile.lastName,
      "location" to GeoPoint(latitude, longitude),
      "userId" to profile.userId,
      "userName" to profile.userName,
      "geohash" to geoHash
    )

// Add the user data to Firestore
    firestore.collection(NEAEBY_USERS_COLLECTION).document(profile.userId)
      .set(userData)
      .addOnSuccessListener {
        println("User data added successfully")
      }
      .addOnFailureListener { e ->
        println("Error adding user data: $e")
      }
  }

}