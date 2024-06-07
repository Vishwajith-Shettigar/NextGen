package com.example.nextgen.privacy

import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.constants.LOG_KEY
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
    Log.e(LOG_KEY, "Shinchan -> "+ privacyItem.itemStatus)
    Log.e(LOG_KEY, "here  " + status.get())
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