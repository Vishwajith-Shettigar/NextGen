package com.example.nextgen.Application

import android.content.Context
import androidx.room.Room
import com.example.data.dao.UserDao
import com.example.data.db.AppDatabase
import com.example.data.repository.UserRepo
import com.firebase.geofire.GeoFire
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
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
  fun provideFirebaseFirestore(): FirebaseFirestore {
    return FirebaseFirestore.getInstance()
  }

  @Provides
  @Singleton
  fun provideFirebaseAuth(): FirebaseAuth {
    return Firebase.auth
  }

  @Provides
  @Singleton
  fun provideFirebaseDatabase(): FirebaseDatabase {
    return FirebaseDatabase.getInstance()
  }

  @Provides
  @Singleton
  fun provideFirebaseStorage(): FirebaseStorage {
    return FirebaseStorage.getInstance()
  }

  @Provides
  @Singleton
  fun provideRoomDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
      context,
      AppDatabase::class.java,
      "app_database"
    ).build()
  }

  @Singleton
  @Provides
  fun provideUserDao(database: AppDatabase): UserDao {
    return database.userDao()

  }

  @Singleton
  @Provides
  fun provideUserRepository(userDao: UserDao): UserRepo {
    return UserRepo(userDao)
  }

}
