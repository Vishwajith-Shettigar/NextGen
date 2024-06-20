package com.example.domain.registration

import android.app.Activity
import android.content.Context
import android.util.EventLog
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.constants.LOG_KEY
import com.example.domain.constants.USERS_COLLECTION
import com.example.domain.profile.ProfileController
import com.example.model.Privacy
import com.example.model.Profile
import com.example.utility.Result
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Singleton
class SignUpLogInController @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth,
  private val profileController: ProfileController,
) {
  fun logIn(
    activity: AppCompatActivity, email: String, password: String,
    callback: (com.example.utility.Result<*>) -> Unit,
  ) {
    CoroutineScope(Dispatchers.IO).launch {
      auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(activity) { task ->
          if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Log.d(LOG_KEY, "signInWithEmail:success")
            CoroutineScope(Dispatchers.IO).launch {
              val doc =
                firestore.collection(USERS_COLLECTION).document(auth.uid.toString()).get().await()

              val privacy = Privacy.newBuilder()
                .apply {
                  this.disableProfilePicture = doc.getBoolean("disableProfilePicture") ?: false
                  this.disableChat = doc.getBoolean("disableChat") ?: false
                  this.disableLocation = doc.getBoolean("disableLocation") ?: false
                }.build()
              val profile = Profile.newBuilder().apply {
                this.userId = doc.getString("userId")
                this.userName = doc.getString("username")
                this.firstName = doc?.getString("firstName")
                this.lastName = doc?.getString("lastName")
                this.imageUrl = doc?.getString("imageUrl")
                this.bio = doc?.getString("bio")
                this.rating = 0F
                this.privacy = privacy
              }.build()

              profileController.setLocalUserProfile(profile = profile) {
                callback(com.example.utility.Result.Success("Successful"))
              }


            }

          } else {
            // If sign in fails, display a message to the user.
            Log.w(LOG_KEY, "signInWithEmail:failure", task.exception)
            callback(com.example.utility.Result.Failure(task.exception?.message.toString()))
          }
        }
        .addOnFailureListener {
          Log.e(LOG_KEY, "signInWithEmail:failure" + it.toString())

        }
    }
  }

  // Todo: Do hashing and salting with password
  fun registerUser(
    activity: Activity, username: String, email: String, password: String,
    callback: (com.example.utility.Result<*>) -> Unit,
  ) {
    CoroutineScope(Dispatchers.IO).launch {
      auth.signOut()
      if (auth.currentUser != null)
        return@launch

      Log.e(LOG_KEY, email)
      auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(activity) { task ->
          if (task.isSuccessful) {

            CoroutineScope(Dispatchers.IO).launch {

              saveUser(username, email, password) { result ->
                when (result) {
                  is com.example.utility.Result.Success -> {

                    val privacy = Privacy.newBuilder().apply {
                      this.disableProfilePicture = false
                      this.disableLocation = false
                      this.disableChat = false
                    }.build()

                    val profile = Profile.newBuilder().apply {
                      this.userId = result.data
                      this.userName = username
                      this.firstName = ""
                      this.lastName = ""
                      this.imageUrl = ""
                      this.bio = ""
                      this.rating = 0F
                      this.privacy = privacy
                    }.build()

                    Log.e(LOG_KEY, profile.toString())
                    CoroutineScope(Dispatchers.IO).launch {
                      profileController.setLocalUserProfile(profile = profile) {}
                    }
                    callback(com.example.utility.Result.Success("Saved"))
                  }
                  is com.example.utility.Result.Failure -> {
                    if (auth.currentUser != null) {
                      deleteUserFromAuth(email, password)
                    }
                    auth.signOut()
                    callback(com.example.utility.Result.Failure(result.message.toString()))

                  }
                  else -> {}
                }
              }
            }
          } else {
            callback(com.example.utility.Result.Failure(task.exception.toString()))
          }
        }
        .addOnFailureListener {
          callback(com.example.utility.Result.Failure(it.toString()))
        }
    }
  }

  private fun deleteUserFromAuth(email: String, password: String) {
    val user = auth.currentUser
    if (user != null) {
      // Reauthenticate the user
      val credential = EmailAuthProvider.getCredential(email, password)
      user.reauthenticate(credential)
        .addOnCompleteListener { reauthTask ->
          if (reauthTask.isSuccessful) {
            // User reauthenticated, now delete the user
            user.delete()
              .addOnCompleteListener { deleteTask ->
                if (deleteTask.isSuccessful) {

                } else {
                }
              }
          } else {
          }
        }
    } else {
    }
  }

  // Todo: Do hashing and salting with password
  fun saveUser(
    username: String,
    email: String,
    password: String,
    callback: (com.example.utility.Result<String>) -> Unit,
  ) {
    val user = hashMapOf(
      "userId" to auth.currentUser!!.uid,
      "username" to username,
      "email" to email,
      "password" to password,
      "name" to null,
      "imageUrl" to null,
      "status" to "online",
      "firstName" to null,
      "lastName" to null,
      "rating" to 0,
      "disableProfilePicture" to false,
      "disableLocation" to false,
      "disableChat" to false,
      "chats" to mutableMapOf<String, String>(),
      "ratings" to mutableMapOf<String, String>(),
      "location" to GeoPoint(0.0, 0.0),
      "geoHash" to GeoFireUtils.getGeoHashForLocation(GeoLocation(0.0, 0.0))
    )
    firestore.collection(USERS_COLLECTION).document(auth.currentUser!!.uid)
      .set(user).addOnSuccessListener {
        callback(com.example.utility.Result.Success(auth.currentUser!!.uid.toString()))
      }
      .addOnFailureListener {
        callback(com.example.utility.Result.Failure(it.message.toString()))
      }
  }
}
