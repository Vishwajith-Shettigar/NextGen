package com.example.nextgen.Application

import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.ActivityModule
import com.example.nextgen.service.LocationService
import com.example.nextgen.service.VideoCallService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
  fun inject(application: MyApplication)
  fun inject(service: LocationService)
  fun inject(service: VideoCallService)


  fun activityComponent(activityModule: ActivityModule): ActivityComponent
}