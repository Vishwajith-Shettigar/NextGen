package com.example.nextgen.Application

import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.ActivityModule
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {
  fun inject(application: MyApplication)
  fun activityComponent(activityModule: ActivityModule): ActivityComponent
}