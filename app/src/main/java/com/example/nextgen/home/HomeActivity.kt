package com.example.nextgen.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.ActivityScope
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import javax.inject.Inject

@ActivityScope
class HomeActivity : BaseActivity(){
  @Inject
 lateinit var activity:AppCompatActivity




  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    activity.supportFragmentManager.beginTransaction().add(R.id.home_fragment_placeholder,HomeFragment.newInstance())
      .commitNow()



    }


  override fun injectDependencies(activityComponent: ActivityComponent) {
    activityComponent.inject(this)
  }

}