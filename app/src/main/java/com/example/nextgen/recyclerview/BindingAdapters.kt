package com.example.nextgen.recyclerview

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.nextgen.R
import com.squareup.picasso.Picasso

class BindingAdapters {

  companion object {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, url: String) {
      if (url.isBlank())
        Picasso.get().load(R.drawable.ic_launcher_background)
          .error(R.drawable.ic_launcher_background).into(view)
      else
        Picasso.get().load(url).error(R.drawable.ic_launcher_background).into(view)
    }
  }
}
