package com.example.nextgen.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nextgen.R

class ProfileFragment : Fragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_profile, container, false)
  }

  companion object {
    fun newInstance(): ProfileFragment {
      return ProfileFragment()
    }
  }
}
