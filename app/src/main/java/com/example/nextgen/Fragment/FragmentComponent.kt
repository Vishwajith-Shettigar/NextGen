package com.example.nextgen.Fragment

import com.example.nextgen.home.HomeFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [FragmentModule::class])
interface FragmentComponent {
  fun inject(fragment: HomeFragment)

}
