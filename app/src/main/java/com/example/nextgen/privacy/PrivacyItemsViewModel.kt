package com.example.nextgen.privacy

import android.view.View
import androidx.databinding.ObservableField
import com.example.model.PrivacyItem
import com.example.nextgen.viewmodel.ObservableViewModel

class PrivacyItemsViewModel(
  private val index: Int,
  private val privacyItem: PrivacyItem,
  private val onPrivacyItemClicked: OnPrivacyItemClicked,
) : ObservableViewModel() {


  var status = ObservableField<Boolean>(privacyItem.itemStatus)
  init {
    status.set(privacyItem.itemStatus)
  }

  val itemName by lazy {
    privacyItem.itemName
  }

  val itemId by lazy {
    privacyItem.itemId
  }

  fun onClick(view: View) {
    onPrivacyItemClicked.onPrivacyItemClicked(
      privacyItem = privacyItem,
      status = status.get()!!,
      index
    )
  }
}