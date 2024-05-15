package com.example.nextgen.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nextgen.Application.MyApplication

abstract class BaseActivity : AppCompatActivity() {

  lateinit var activityComponent: ActivityComponent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent = (application as MyApplication).appComponent
      .activityComponent(ActivityModule(this))
    injectDependencies(activityComponent)
  }

  abstract fun injectDependencies(activityComponent: ActivityComponent)
}