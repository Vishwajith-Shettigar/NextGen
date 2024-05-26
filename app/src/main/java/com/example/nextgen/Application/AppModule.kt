package com.example.nextgen.Application

import android.content.Context
import com.firebase.geofire.GeoFire
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {
  @Provides
  @Singleton
  fun provideContext(): Context {
    return context
  }

  @Provides
  @Singleton
  fun provideFirebaseFirestore():FirebaseFirestore{
    return FirebaseFirestore.getInstance()
  }

  @Provides
  @Singleton
  fun provideFirebaseAuth():FirebaseAuth{
    return Firebase.auth
  }

}
