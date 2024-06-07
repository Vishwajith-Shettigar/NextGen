package com.example.nextgen.recyclerview

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.domain.constants.LOG_KEY
import com.example.domain.profile.DISABLE_CHAT_ID
import com.example.domain.profile.DISABLE_LOCATION_ID
import com.example.domain.profile.DISABLE_PROFILE_PICTURE
import com.example.nextgen.R
import com.squareup.picasso.Picasso

class BindingAdapters {

  companion object {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, url: String?) {
      try {
        Log.e(LOG_KEY, url.toString())
        if (url == null || url.isBlank() == true)
          Picasso.get().load(R.drawable.profile_placeholder)
            .error(R.drawable.profile_placeholder).into(view)
        else
          Picasso.get().load(url).error(R.drawable.profile_placeholder).into(view)
      } catch (e: Exception) {
        Picasso.get().load(R.drawable.profile_placeholder).into(view)
      }
    }

    @JvmStatic
    @BindingAdapter("loadPrivacyItemsIcon")
    fun loadPrivacyItemsIcon(view: ImageView, itemId: String?) {
      try {
        Log.e(LOG_KEY," IN loadPrivacyItemsIcon   "  + itemId)
        if (itemId == DISABLE_CHAT_ID) {
          Log.e(LOG_KEY," IN loadPrivacyItemsIcon   "  + itemId)

          Picasso.get().load(R.drawable.chaticon).
          error(R.drawable.chaticon).into(view)
        } else if (itemId == DISABLE_LOCATION_ID) {
          Picasso.get().load(R.drawable.nearby_24)
            .error(R.drawable.nearby_24).into(view)
        } else if (itemId == DISABLE_PROFILE_PICTURE) {
          Picasso.get().load(R.drawable.person_24).
          error(R.drawable.person_24).into(view)
        }

      } catch (e: Exception) {
        Picasso.get().load(R.drawable.privacy_icon).into(view)
      }
    }
  }
}
