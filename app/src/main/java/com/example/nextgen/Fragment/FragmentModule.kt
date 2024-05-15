package com.example.nextgen.Fragment

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides

@Module
class FragmentModule(private val fragment: Fragment) {

  @Provides
  @FragmentScope
  fun provideFragment(): Fragment = fragment
}
