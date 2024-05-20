package com.example.nextgen.home

import android.app.Person
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.Fragment.FragmentScope
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentHomeBinding
import javax.inject.Inject

class HomeFragment : BaseFragment() {
  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var fragment: Fragment

  lateinit var binding: FragmentHomeBinding

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {


    // Inflate the layout for this fragment
    binding = FragmentHomeBinding.inflate(inflater,container,false)
    return binding.root
  }

  companion object {
    /** Returns instance of [HomeFragment] */
    fun newInstance(): HomeFragment {
      return HomeFragment()
    }
  }
}
