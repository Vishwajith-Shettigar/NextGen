package com.example.nextgen.privacy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.domain.constants.LOG_KEY
import com.example.domain.profile.ProfileController
import com.example.model.Profile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentPrivacyBinding
import com.example.utility.getProto
import com.example.utility.putProto
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrivacyFragment : BaseFragment() {

  lateinit var binding: FragmentPrivacyBinding

  lateinit var profile: Profile

  @Inject
  lateinit var profileController: ProfileController

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    // Inflate the layout for this fragment
    binding = FragmentPrivacyBinding.inflate(inflater, container, false)
    profile = arguments?.getProto(PRIVACY_FRAGMENT_ARGUMENTS_KEY, Profile.getDefaultInstance())!!
    return binding.root
  }

  companion object {

    private val PRIVACY_FRAGMENT_ARGUMENTS_KEY = "PrivacyFragment.arguments"

    @JvmStatic
    fun newInstance(profile: Profile) =
      PrivacyFragment().apply {
        arguments = Bundle().apply {
          putProto(PRIVACY_FRAGMENT_ARGUMENTS_KEY, profile)
        }
      }
  }
}
