package com.example.nextgen.viewprofile

import android.media.Rating
import androidx.databinding.ObservableFloat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.profile.ProfileController
import com.example.model.ViewProfile
import com.example.nextgen.privacy.PrivacyItemsViewModel
import com.example.nextgen.viewmodel.ObservableViewModel
import kotlinx.coroutines.launch

class ViewProfileViewModel(
  private val userId: String,
   val viewProfile: ViewProfile,
 private val profileController: ProfileController
) : ObservableViewModel() {

  val existingRating: Float=viewProfile.existingRating

  val rating:String= viewProfile.rating.toString()

fun updateRating(rating: Float){
  viewModelScope.launch {
    profileController.updateUserRating(userId, viewProfile.userId,rating)
  }
}

}