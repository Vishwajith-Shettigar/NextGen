package com.example.nextgen.privacy

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.model.PrivacyItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PrivacyItemsViewModel(
    private val index: Int,
    private val privacyItem: PrivacyItem,
    private val onPrivacyItemClicked: OnPrivacyItemClicked
) : ViewModel() {

    private val _status = MutableStateFlow(privacyItem.itemStatus)
    val status: StateFlow<Boolean> = _status

    val itemName: String by lazy {
        privacyItem.itemName
    }

    val itemId: String by lazy {
        privacyItem.itemId
    }

    init {
        _status.value = privacyItem.itemStatus
    }

    fun onClick(view: View) {
        // Emit the current status value to onPrivacyItemClicked callback
        onPrivacyItemClicked.onPrivacyItemClicked(
            privacyItem = privacyItem,
            status = _status.value,  // Access the current status from StateFlow
            index = index
        )
    }

    fun updateStatus(newStatus: Boolean) {
        _status.value = newStatus
    }
}