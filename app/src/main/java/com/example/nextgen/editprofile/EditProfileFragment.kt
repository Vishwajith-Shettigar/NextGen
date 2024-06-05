package com.example.nextgen.editprofile

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
import com.example.nextgen.databinding.FragmentEditProfileBinding
import com.example.nextgen.profile.ProfileViewModel
import com.example.utility.getProto
import com.example.utility.putProto
import javax.inject.Inject

class EditProfileFragment : BaseFragment() {

  lateinit var binding: FragmentEditProfileBinding

  @Inject
  lateinit var profileController: ProfileController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

  }

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    // Inflate the layout for this fragment
    binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
    val profile =
      arguments?.getProto(EDIT_PROFILE_FRAGMENT_ARGUMENTS_KEY, Profile.getDefaultInstance())!!
    Log.e(LOG_KEY, profile.toString())
    val editProfileViewModel = EditProfileViewModel(profile, profileController)
    binding.viewModel = editProfileViewModel
    binding.lifecycleOwner = this

    binding.buttonSave.setOnClickListener {
      val newProfile = profile.toBuilder()
      newProfile.userName = binding.editTextUsername.text.toString()
      newProfile.firstName = binding.editTextFirstName.text.toString()
      newProfile.lastName = binding.editTextLastName.text.toString()
      newProfile.bio = binding.editTextBio.text.toString()
      val modifiedProfile = newProfile.build()
//      editProfileViewModel.updateUserProfile(modifiedProfile)
      Log.e(LOG_KEY, modifiedProfile.toString())


    }

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