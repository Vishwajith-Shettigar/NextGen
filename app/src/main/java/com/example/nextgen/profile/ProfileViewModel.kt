package com.example.nextgen.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.profile.ProfileController
import com.example.model.Profile
import com.example.nextgen.viewmodel.ObservableViewModel
import kotlinx.coroutines.launch

class ProfileViewModel(
  private val userId: String,
  private val profileController: ProfileController,
) : ObservableViewModel() {

  private val _profile = MutableLiveData<Profile>()
  val profile: LiveData<Profile> get() = _profile

  private val _rating = MutableLiveData<Float>()
  val rating: LiveData<Float> get() = _rating

  init {
    loadProfile()
    viewModelScope.launch {
      _rating.value = (profileController.getRating(userId))
    }
  }

  fun loadProfile() {
    viewModelScope.launch {
      try {
        val profile = profileController.getLocalUserProfile(userId)
        _profile.postValue(profile)
      } catch (e: java.lang.Exception) {
      }
    }
  }
}
