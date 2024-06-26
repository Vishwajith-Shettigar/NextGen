package com.example.nextgen.notification

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentNotificationBinding

class NotificationFragment : BaseFragment() {

  private lateinit var binding: FragmentNotificationBinding

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    binding = FragmentNotificationBinding.inflate(inflater, container, false)
    return binding.root
  }

  companion object {
    const val TAG = "NotificationFragment"

    fun newInstance(): NotificationFragment {
      return NotificationFragment()
    }
  }
}
