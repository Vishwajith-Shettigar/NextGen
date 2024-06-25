package com.example.nextgen.recyclerview

import android.graphics.drawable.Drawable
import android.media.Rating
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.example.domain.constants.LOG_KEY
import com.example.domain.profile.DISABLE_CHAT_ID
import com.example.domain.profile.DISABLE_LOCATION_ID
import com.example.domain.profile.DISABLE_PROFILE_PICTURE
import com.example.nextgen.R
import com.google.errorprone.annotations.InlineMeValidationDisabled
import com.squareup.picasso.Picasso

class BindingAdapters {

  companion object {

    @JvmStatic
    @BindingAdapter(value = ["imageUrl", "isDisabled"], requireAll = false)
    fun loadImage(view: ImageView, url: String?, isDisabled: Boolean) {
      if (isDisabled) {
        Picasso.get().load(R.drawable.profile_placeholder).into(view)
        return
      }
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
    @BindingAdapter(value = ["rating"], requireAll = true)
    fun loadStars(view: ImageView, rating: LiveData<Float>) {
      if (rating.value == 5F)
        Picasso.get().load(R.drawable.full_star).error(R.drawable.half_star).into(view)
      else {
        Picasso.get().load(R.drawable.half_star).error(R.drawable.half_star).into(view)
      }
    }

    @JvmStatic
    @BindingAdapter("loadPrivacyItemsIcon")
    fun loadPrivacyItemsIcon(view: ImageView, itemId: String?) {
      try {
        Log.e(LOG_KEY, " IN loadPrivacyItemsIcon   " + itemId)
        if (itemId == DISABLE_CHAT_ID) {
          Log.e(LOG_KEY, " IN loadPrivacyItemsIcon   " + itemId)

          Picasso.get().load(R.drawable.chaticon).error(R.drawable.chaticon).into(view)
        } else if (itemId == DISABLE_LOCATION_ID) {
          Picasso.get().load(R.drawable.nearby_24)
            .error(R.drawable.nearby_24).into(view)
        } else if (itemId == DISABLE_PROFILE_PICTURE) {
          Picasso.get().load(R.drawable.person_24).error(R.drawable.person_24).into(view)
        }

      } catch (e: Exception) {
        Picasso.get().load(R.drawable.privacy_icon).into(view)
      }
    }
  }
}
