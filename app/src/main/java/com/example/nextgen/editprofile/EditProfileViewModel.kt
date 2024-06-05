package com.example.nextgen.editprofile

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import com.example.domain.profile.ProfileController
import com.example.model.Profile
import com.example.nextgen.viewmodel.ObservableViewModel
import kotlinx.coroutines.launch

class EditProfileViewModel(
  private val profile: Profile,
  private val profileController: ProfileController,
) : ObservableViewModel() {

  val userName by lazy {
    profile.userName
  }

  val firstName by lazy {
    profile.firstName
  }

  val lastName by lazy {
    profile.lastName
  }

  val imageUrl by lazy {
    profile.imageUrl
  }

  val bio by lazy {
    profile.bio
  }


  fun updateUserProfile(profile: Profile) {
    profileController.updateUserProfile(profile) {
      if (it is com.example.utility.Result.Success)
        viewModelScope.launch {
          profileController.setLocalUserProfile(profile)
        }
    }
  }

}