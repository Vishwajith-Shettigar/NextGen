package com.example.nextgen.viewprofile

import android.media.Rating
import androidx.databinding.ObservableFloat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.chat.ChatController
import com.example.domain.profile.ProfileController
import com.example.model.Profile
import com.example.model.ViewProfile
import com.example.nextgen.privacy.PrivacyItemsViewModel
import com.example.nextgen.viewmodel.ObservableViewModel
import kotlinx.coroutines.launch

class ViewProfileViewModel(
  private val userId: String,
  val viewProfile: Profile,
  private val profileController: ProfileController,
  private val chatController: ChatController
) : ObservableViewModel() {

  var chatId: String? = null

  init {

    viewModelScope.launch {
      chatId = chatController.isChatExists(userId, viewProfile.userId)
    }
  }

  val userName by lazy {
    viewProfile.userName
  }

  val fullName: String by lazy {
    viewProfile.firstName + " " + viewProfile.lastName
  }

  val existingRating: Float = viewProfile.rated

  val rating: String = viewProfile.rating.toString()

  fun updateRating(rating: Float) {
    viewModelScope.launch {
      profileController.updateUserRating(userId, viewProfile.userId, rating)
    }
  }

}