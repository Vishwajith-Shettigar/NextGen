package com.example.nextgen.editprofile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.domain.constants.LOG_KEY
import com.example.model.Profile
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentEditProfileBinding
import com.example.utility.getProto
import com.example.utility.putProto

class EditProfileFragment : Fragment() {

  lateinit var binding:FragmentEditProfileBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    // Inflate the layout for this fragment
    binding= FragmentEditProfileBinding.inflate(layoutInflater, container, false)
    val profile=arguments?.getProto(EDIT_PROFILE_FRAGMENT_ARGUMENTS_KEY,Profile.getDefaultInstance())
    Log.e(LOG_KEY,profile.toString())
    return binding.root
  }

  companion object {

    private val EDIT_PROFILE_FRAGMENT_ARGUMENTS_KEY = "EditProfileFragment.arguments"

    @JvmStatic
    fun newInstance(profile: Profile) =
      EditProfileFragment().apply {
        arguments = Bundle().apply {
          putProto(EDIT_PROFILE_FRAGMENT_ARGUMENTS_KEY, profile)
        }
      }
  }
}