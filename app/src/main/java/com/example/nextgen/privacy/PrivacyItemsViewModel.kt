package com.example.nextgen.privacy

import android.view.View
import com.example.model.PrivacyItem
import com.example.nextgen.viewmodel.ObservableViewModel

class PrivacyItemsViewModel(
private val privacyItem: PrivacyItem,
private val onPrivacyItemClicked: OnPrivacyItemClicked
) :ObservableViewModel() {

   val status by lazy{
    privacyItem.itemStatus
  }
   val itemName by lazy {
    privacyItem.itemName
  }

  val itemId by lazy {
    privacyItem.itemId
  }

  fun onClick(view:View){
    onPrivacyItemClicked.onPrivacyItemClicked(privacyItem = privacyItem,status=status)
  }
}