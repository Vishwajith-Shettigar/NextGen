package com.example.nextgen.Application

import android.content.Context
import com.example.domain.post.PostController
import com.google.firebase.firestore.FirebaseFirestore
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

}
