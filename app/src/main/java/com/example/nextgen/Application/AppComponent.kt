package com.example.nextgen.Application

import com.example.domain.post.PostController
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.ActivityModule
import com.example.nextgen.service.LocationService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
  fun inject(application: MyApplication)
  fun inject(locationService: LocationService)

  fun activityComponent(activityModule: ActivityModule): ActivityComponent
}