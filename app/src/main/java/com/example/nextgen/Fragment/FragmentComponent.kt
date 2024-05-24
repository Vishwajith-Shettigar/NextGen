package com.example.nextgen.Fragment

import com.example.nextgen.home.HomeFragment
import com.example.nextgen.nearby.NearByFragment
import com.example.nextgen.profile.ProfileFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [FragmentModule::class])
interface FragmentComponent {
  fun inject(fragment: HomeFragment)
  fun inject(fragment: ProfileFragment)
  fun inject(fragment: NearByFragment)



}
