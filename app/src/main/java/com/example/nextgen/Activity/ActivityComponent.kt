package com.example.nextgen.Activity

import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.Fragment.FragmentModule
import com.example.nextgen.home.HomeActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
  fun inject(activity: HomeActivity)

  fun fragmentComponent(fragmentModule: FragmentModule): FragmentComponent
}