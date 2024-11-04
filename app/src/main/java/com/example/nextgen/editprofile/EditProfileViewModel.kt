package com.example.nextgen.editprofile

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
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

  val userName = MutableLiveData<String>().apply { value = profile.userName }
  val firstName = MutableLiveData<String>().apply { value = profile.firstName }
  val lastName = MutableLiveData<String>().apply { value = profile.lastName }
  val bio = MutableLiveData<String>().apply { value = profile.bio }
  var imageUrl: String = profile.imageUrl

  fun storeNewImage(bitmap: Bitmap, callback: (Result<String>) -> Unit) {
    viewModelScope.launch {
      profileController.uploadImageToStorage(bitmap = bitmap, userId = profile.userId) {
        if (it is Result.Success) {
          imageUrl = it.data.toString()
          callback(Result.Success(imageUrl))
        }
      }
    }
  }

  fun updateUserProfile(profile: Profile, callback: (Result<String>) -> Unit) {
    profileController.updateUserProfile(profile) {
      if (it is Result.Success) {
        viewModelScope.launch {
          profileController.setLocalUserProfile(profile, callback)
        }
      }
    }
  }
}
