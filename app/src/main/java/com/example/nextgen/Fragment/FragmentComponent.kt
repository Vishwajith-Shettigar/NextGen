package com.example.nextgen.Fragment

import com.example.nextgen.editprofile.EditProfileFragment
import com.example.nextgen.home.HomeFragment
import com.example.nextgen.message.MessageFragment
import com.example.nextgen.nearby.NearByFragment
import com.example.nextgen.notification.NotificationFragment
import com.example.nextgen.privacy.PrivacyFragment
import com.example.nextgen.profile.ProfileFragment
import com.example.nextgen.signup.SignInFragment
import com.example.nextgen.signup.SignupFragment
import com.example.nextgen.videocall.VideoCallFragment
import com.example.nextgen.viewprofile.ViewProfileFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [FragmentModule::class])
interface FragmentComponent {
  fun inject(fragment: HomeFragment)
  fun inject(fragment: ProfileFragment)
  fun inject(fragment: NearByFragment)
  fun inject(fragment: SignupFragment)
  fun inject(fragment: SignInFragment)
  fun inject(fragment: MessageFragment)
  fun inject(fragment: EditProfileFragment)
  fun inject(fragment: PrivacyFragment)
  fun inject(fragment: ViewProfileFragment)
  fun inject(fragment: VideoCallFragment)
  fun inject(fragment: NotificationFragment)
}
