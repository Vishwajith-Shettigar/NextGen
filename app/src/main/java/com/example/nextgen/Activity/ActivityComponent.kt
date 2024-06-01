package com.example.nextgen.Activity

import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.Fragment.FragmentModule
import com.example.nextgen.home.HomeActivity
import com.example.nextgen.message.MessageActivity
import com.example.nextgen.signup.SignupSigninActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
  fun inject(activity: HomeActivity)
  fun inject(activity: SignupSigninActivity)
  fun inject(activity: MessageActivity)

  fun fragmentComponent(fragmentModule: FragmentModule): FragmentComponent
}