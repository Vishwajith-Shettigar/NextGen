package com.example.nextgen.Activity

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: AppCompatActivity) {

  @Provides
  @ActivityScope
  fun provideActivity(): AppCompatActivity = activity
}
