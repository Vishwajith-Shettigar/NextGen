package com.example.nextgen.splashScreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import com.example.nextgen.home.HomeActivity
import com.example.nextgen.signup.SignupSigninActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashScreenActivity : BaseActivity() {

    @Inject
    lateinit var firebaseAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        lifecycleScope.launch {
            delay(3000) // 3-second delay
            redirectUserBasedOnSignInStatus()
        }
    }

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
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
