package com.example.nextgen.editprofile

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.example.domain.profile.ProfileController
import com.example.model.Profile
import com.example.nextgen.viewmodel.ObservableViewModel
import com.example.utility.Result
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

  var imageUrl: String = profile.imageUrl


  val bio by lazy {
    profile.bio
  }


  fun storeNewImage(bitmap: Bitmap, callback:(com.example.utility.Result<String>)->Unit){
    viewModelScope.launch {
      profileController.uploadImageToStorage(bitmap = bitmap, userId = profile.userId) {
        if (it is com.example.utility.Result.Success) {
          imageUrl = it.data.toString()
          callback(com.example.utility.Result.Success(imageUrl))
        }
      }
    }
  }

  fun updateUserProfile(profile: Profile,callback: (Result<String>) -> Unit) {

    profileController.updateUserProfile(profile) {
      if (it is com.example.utility.Result.Success)
        viewModelScope.launch {
          profileController.setLocalUserProfile(profile,callback)
        }
    }
  }

}