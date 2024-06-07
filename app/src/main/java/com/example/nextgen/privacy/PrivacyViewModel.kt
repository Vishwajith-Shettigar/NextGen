package com.example.nextgen.privacy

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.profile.ProfileController
import com.example.model.PrivacyItem
import com.example.model.Profile
import com.example.nextgen.home.HomeItemViewModel
import com.example.nextgen.viewmodel.ObservableViewModel

class PrivacyViewModel(
   private val profile: Profile,
   fragment: Fragment,
private val  profileController: ProfileController
):ObservableViewModel() {

  private var _privacyitemsList = MutableLiveData<List<PrivacyItemsViewModel>>()
  val privacyitemsList: LiveData<List<PrivacyItemsViewModel>> get() = _privacyitemsList
  init {
loadPrivacyItems()

  }

  fun loadPrivacyItems(){

    val privacyItems= profileController.getPrivacyItems(profile.privacy)
   val data = mutableListOf<PrivacyItemsViewModel>()
    privacyItems.forEach {
      val privacyItemsViewModel= PrivacyItemsViewModel(it)
      data.add(privacyItemsViewModel)
    }
    _privacyitemsList.value=data
  }
}