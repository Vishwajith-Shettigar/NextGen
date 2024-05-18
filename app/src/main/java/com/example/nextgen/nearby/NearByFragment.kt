package com.example.nextgen.nearby

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nextgen.R

/**
 * A simple [Fragment] subclass.
 * Use the [NearByFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NearByFragment : Fragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_near_by, container, false)
  }

  companion object {
    fun newInstance(): NearByFragment {
      return NearByFragment()
    }
  }
}