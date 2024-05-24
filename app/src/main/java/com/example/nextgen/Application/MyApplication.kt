package com.example.nextgen.Application

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore

class MyApplication : Application() {

  lateinit var appComponent: AppComponent

  override fun onCreate() {
    super.onCreate()
    FirebaseFirestore.setLoggingEnabled(true)
    appComponent = DaggerAppComponent.builder()
      .appModule(AppModule(this))
      .build()
    appComponent.inject(this)
  }


}

