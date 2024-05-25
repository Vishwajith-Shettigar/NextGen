package com.example.nextgen.Activity

import com.example.nextgen.Application.AppModule
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.Fragment.FragmentModule
import com.example.nextgen.home.HomeActivity
import com.example.nextgen.signup.SignupActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
  fun inject(activity: HomeActivity)
  fun inject(activity: SignupActivity)

  fun fragmentComponent(fragmentModule: FragmentModule): FragmentComponent
}