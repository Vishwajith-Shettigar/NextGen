package com.example.nextgen.privacy

import com.example.model.Privacy
import com.example.model.PrivacyItem

interface OnPrivacyItemClicked {
  fun onPrivacyItemClicked(privacyItem: PrivacyItem,status:Boolean,index: Int)
}