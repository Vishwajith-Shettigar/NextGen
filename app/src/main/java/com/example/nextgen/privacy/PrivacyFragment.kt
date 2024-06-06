package com.example.nextgen.privacy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.model.Profile
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentPrivacyBinding
import com.example.utility.getProto
import com.example.utility.putProto

class PrivacyFragment : Fragment() {

  lateinit var binding: FragmentPrivacyBinding

  lateinit var profile: Profile
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
