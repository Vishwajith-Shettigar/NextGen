package com.example.nextgen.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R

class SignupActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

  override fun injectDependencies(activityComponent: ActivityComponent) {
    activityComponent.inject(this)
  }
}