package com.example.domain.registration

import android.app.Activity
import android.content.Context
import android.util.EventLog
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.constants.LOG_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignUpLogInController @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth,
) {

  fun logIn(activity: AppCompatActivity,email: String, password: String,
            callback: (com.example.utility.Result) -> Unit){
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
    auth.signOut()
    if (auth.currentUser != null)
      return

    Log.e(LOG_KEY, email)
    auth.createUserWithEmailAndPassword(email, password)
      .addOnCompleteListener(activity) { task ->
        if (task.isSuccessful) {

//          Todo : Save user in firestore
          callback(com.example.utility.Result.Success())
        } else {
          callback(com.example.utility.Result.Failure(task.exception.toString()))
        }
      }
      .addOnFailureListener {
        callback(com.example.utility.Result.Failure(it.toString()))
      }
  }
}
