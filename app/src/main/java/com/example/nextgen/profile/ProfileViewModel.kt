package com.example.nextgen.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.constants.LOG_KEY
import com.example.domain.profile.ProfileController
import com.example.model.Profile
import com.example.nextgen.viewmodel.ObservableViewModel
import kotlinx.coroutines.launch

class ProfileViewModel(
  private val userId:String,
  private val profileController: ProfileController
):ObservableViewModel() {

  private val _profile = MutableLiveData<Profile>()
  val profile: LiveData<Profile> get() = _profile

  override fun onCleared() {
    super.onCleared()
    Log.e(LOG_KEY,"ProfileViewModel cleared")
  }
  init {
      loadProfile()
  }

  fun loadProfile(){
    viewModelScope.launch {
      try {
          val profile= profileController.getLocalUserProfile(userId)
        Log.e(LOG_KEY,profile.toString())
        _profile.postValue(profile)
      }
      catch (e:java.lang.Exception){}
    }
  }
}
