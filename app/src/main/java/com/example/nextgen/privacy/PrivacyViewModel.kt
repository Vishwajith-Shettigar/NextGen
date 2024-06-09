package com.example.nextgen.privacy

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.constants.LOG_KEY
import com.example.domain.profile.ProfileController
import com.example.model.PrivacyItem
import com.example.model.Profile
import com.example.nextgen.home.HomeItemViewModel
import com.example.nextgen.viewmodel.ObservableViewModel
import kotlinx.coroutines.launch

class PrivacyViewModel(
  private val profile: Profile,
  fragment: Fragment,
  private val profileController: ProfileController,
  private val onPrivacyItemClicked: OnPrivacyItemClicked,
) : ObservableViewModel() {

  private var _privacyitemsList = MutableLiveData<List<PrivacyItemsViewModel>>()
  val privacyitemsList: LiveData<List<PrivacyItemsViewModel>> get() = _privacyitemsList

  init {
    loadPrivacyItems()

  }

  fun loadPrivacyItems() {

    val privacyItems = profileController.getPrivacyItems(profile.privacy)
    val data = mutableListOf<PrivacyItemsViewModel>()
    privacyItems.mapIndexed { index, privacyItem ->
      val privacyItemsViewModel = PrivacyItemsViewModel(index, privacyItem, onPrivacyItemClicked)
      data.add(privacyItemsViewModel)
    }
    _privacyitemsList.value = data
  }

  fun updateDisableChatStatus(
    choice: Boolean,
    callback: (com.example.utility.Result<String>) -> Unit,
  ) {
    viewModelScope.launch {
      profileController.updateDisableChatStatus(userId = profile.userId, choice) {
        if (it is com.example.utility.Result.Success) {
          callback(com.example.utility.Result.Success("Successful"))
        } else {

          callback(com.example.utility.Result.Failure("Failed"))
        }
      }
    }
  }
  fun updatedisableLocationStatus(
    choice: Boolean,
    callback: (com.example.utility.Result<String>) -> Unit,
  ) {
    viewModelScope.launch {
      profileController.updatedisableLocationStatus(userId = profile.userId, choice) {
        if (it is com.example.utility.Result.Success) {
          callback(com.example.utility.Result.Success("Successful"))
        } else {

          callback(com.example.utility.Result.Failure("Failed"))
        }
      }
    }
  }

  fun updatedisableProfilePicture(
    choice: Boolean,
    callback: (com.example.utility.Result<String>) -> Unit,
  ) {
    viewModelScope.launch {
      profileController.updatedisableProfilePicture(userId = profile.userId, choice) {
        if (it is com.example.utility.Result.Success) {
          callback(com.example.utility.Result.Success("Successful"))
        } else {

          callback(com.example.utility.Result.Failure("Failed"))
        }
      }
    }
  }
}


