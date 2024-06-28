package com.example.nextgen.Application

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.example.nextgen.service.LocationService
import com.google.firebase.firestore.FirebaseFirestore

class MyApplication : Application(), Application.ActivityLifecycleCallbacks {

  lateinit var appComponent: AppComponent
  private var activityReferences = 0
  private var isActivityChangingConfigurations = false

  override fun onCreate() {
    super.onCreate()
    FirebaseFirestore.setLoggingEnabled(true)
    appComponent = DaggerAppComponent.builder()
      .appModule(AppModule(this))
      .build()
    appComponent.inject(this)

    registerActivityLifecycleCallbacks(this)
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    // No-op
  }

  override fun onActivityStarted(activity: Activity) {
    if (activityReferences == 0 && !isActivityChangingConfigurations) {
      // App enters foreground
    }
    activityReferences++
  }

  override fun onActivityResumed(activity: Activity) {
    // No-op
  }

  override fun onActivityPaused(activity: Activity) {
    // No-op
  }

  override fun onActivityStopped(activity: Activity) {
    activityReferences--
    isActivityChangingConfigurations = activity.isChangingConfigurations
    if (activityReferences == 0 && !isActivityChangingConfigurations) {
      // App enters background
      stopService(Intent(this, LocationService::class.java))
    }
  }

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    // No-op
  }

  override fun onActivityDestroyed(activity: Activity) {
    // No-op
  }
}
