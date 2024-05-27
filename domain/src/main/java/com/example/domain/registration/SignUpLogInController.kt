package com.example.domain.registration

import android.app.Activity
import android.content.Context
import android.util.EventLog
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.constants.LOG_KEY
import com.example.utility.Result
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class SignUpLogInController @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth,
) {

  fun logIn(
    activity: AppCompatActivity, email: String, password: String,
    callback: (com.example.utility.Result) -> Unit,
  ) {
    auth.signInWithEmailAndPassword(email, password)
      .addOnCompleteListener(activity) { task ->
        if (task.isSuccessful) {
          // Sign in success, update UI with the signed-in user's information
          Log.d(LOG_KEY, "signInWithEmail:success")
          callback(com.example.utility.Result.Success())

        } else {
          // If sign in fails, display a message to the user.
          Log.w(LOG_KEY, "signInWithEmail:failure", task.exception)
          callback(com.example.utility.Result.Failure(task.exception?.message.toString()))


        }
      }
  }

  fun registerUser(
    activity: Activity, username: String, email: String, password: String,
    callback: (com.example.utility.Result) -> Unit,
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
                    callback(com.example.utility.Result.Success())
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

  suspend fun saveUser(
    username: String,
    email: String,
    password: String,
    callback: (com.example.utility.Result) -> Unit,
  ) {
    val user = hashMapOf(
      "userId" to auth.currentUser!!.uid,
      "username" to username,
      "email" to email,
      "password" to password
    )
    firestore.collection("users").document(auth.currentUser!!.uid)
      .set(user).addOnSuccessListener {
        callback(com.example.utility.Result.Success())
      }
      .addOnFailureListener {
        callback(com.example.utility.Result.Failure(it.message.toString()))

      }
  }
}
