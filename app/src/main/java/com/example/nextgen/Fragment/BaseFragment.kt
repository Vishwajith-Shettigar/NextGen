package com.example.nextgen.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.nextgen.Activity.BaseActivity

abstract class BaseFragment : Fragment() {

  lateinit var fragmentComponent: FragmentComponent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val activityComponent = (requireActivity() as BaseActivity).activityComponent
    fragmentComponent = activityComponent.fragmentComponent(FragmentModule(this))
    injectDependencies(fragmentComponent)
  }

  abstract fun injectDependencies(fragmentComponent: FragmentComponent)
}