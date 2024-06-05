package com.example.nextgen.recyclerview

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.domain.constants.LOG_KEY
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
  }
}
