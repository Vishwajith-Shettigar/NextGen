package com.example.nextgen.splashScreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nextgen.R
import com.example.nextgen.home.HomeActivity
import com.example.nextgen.signup.SignupSigninActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        firebaseAuth = FirebaseAuth.getInstance()


        lifecycleScope.launch {
            delay(3000) // 3-second delay
            redirectUserBasedOnSignInStatus()
        }
    }

    private fun redirectUserBasedOnSignInStatus() {
        if (firebaseAuth.currentUser != null) {
            startActivity(HomeActivity.createHomeActivity(this))
        } else {
            startActivity(Intent(this, SignupSigninActivity::class.java))
        }
        finish()
    }
}
