package com.example.nextgen.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.domain.chat.ChatController
import com.example.domain.post.PostController
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import java.util.Random
import javax.inject.Inject

class ProfileFragment : BaseFragment() {
  @Inject
  lateinit var postController: PostController
  @Inject
  lateinit var chatController: ChatController
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
    val view= inflater.inflate(R.layout.fragment_profile, container, false)
    chatController.sendMessage("w2f6gmz6ac","f9TJhzT3GmPimdgpP8lPt3LUxyc2",
    "TQb5C8BlvfdsFFd0ApSFmNmCqey1",Random().nextLong().toString()){

    }
    return view

  }

  companion object {

    const val TAG = "ProfileFragment"

    fun newInstance(): ProfileFragment {
      return ProfileFragment()
    }
  }
}
