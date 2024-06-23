package com.example.nextgen.Activity

import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.Fragment.FragmentModule
import com.example.nextgen.editprofile.EditProfileActivity
import com.example.nextgen.home.HomeActivity
import com.example.nextgen.message.MessageActivity
import com.example.nextgen.privacy.PrivacyActivity
import com.example.nextgen.signup.SignupSigninActivity
import com.example.nextgen.videocall.VideoCallActivity
import com.example.nextgen.viewprofile.ViewProfileActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
  fun inject(activity: HomeActivity)
  fun inject(activity: SignupSigninActivity)
  fun inject(activity: MessageActivity)
  fun inject(activity: EditProfileActivity)
  fun inject(activity: PrivacyActivity)
  fun inject(activity: ViewProfileActivity)
  fun inject(activity: VideoCallActivity)
  fun fragmentComponent(fragmentModule: FragmentModule): FragmentComponent
}