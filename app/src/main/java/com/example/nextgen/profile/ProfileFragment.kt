package com.example.nextgen.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.domain.post.PostController
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import javax.inject.Inject

class ProfileFragment : BaseFragment() {
  @Inject
  lateinit var postController: PostController
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
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
