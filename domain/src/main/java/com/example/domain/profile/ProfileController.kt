package com.example.domain.profile

import android.net.ConnectivityManager.NetworkCallback
import android.util.Log
import com.example.domain.constants.LOG_KEY
import com.example.domain.constants.USERS_COLLECTION
import com.example.domain.nearby.NEAEBY_USERS_COLLECTION
import com.example.model.Profile
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Singleton
class ProfileController @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val firebaseAuth: FirebaseAuth
) {

  fun getUserId():String?
  {
    return firebaseAuth.currentUser?.uid
  }
  fun saveUsers(profile: Profile) {
    val latitude = profile.location.latitude
    val longitude = profile.location.longitude

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

  suspend fun getUserProfile(userId: String): com.example.utility.Result<DocumentSnapshot> {
    return try {
      val document = firestore.collection(USERS_COLLECTION).document(userId).get().await()
      com.example.utility.Result.Success(document)
    } catch (e: Exception) {
      com.example.utility.Result.Failure(e.message ?: "Failed to retrieve user profile")
    }
  }
}


