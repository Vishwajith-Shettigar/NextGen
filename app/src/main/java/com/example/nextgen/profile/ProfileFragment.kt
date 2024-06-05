package com.example.nextgen.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.data.repository.UserRepo
import com.example.domain.chat.ChatController
import com.example.domain.constants.LOG_KEY
import com.example.domain.post.PostController
import com.example.domain.profile.ProfileController
import com.example.model.Privacy
import com.example.model.Profile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentProfileBinding
import com.example.nextgen.editprofile.RouteToEditProfileActivity
import java.util.Random
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : BaseFragment() {
  @Inject
  lateinit var profileController: ProfileController

  @Inject
  lateinit var chatController: ChatController

  @Inject
  lateinit var userRepo: UserRepo

  @Inject
  lateinit var activity: AppCompatActivity

  lateinit var binding: FragmentProfileBinding

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
    binding = FragmentProfileBinding.inflate(inflater, container, false)
    val userId = profileController.getUserId()

    val p = Profile.newBuilder().apply {
      this.userId = "hellogrger1232"
      this.userName = "Dark"
      this.firstName = "Vish"
      this.imageUrl = "https://avatars.githubusercontent.com/u/76042077?v=4"
      this.bio = "Yenna pannitringe"
      this.rating = 4.5F
      this.privacy = Privacy.newBuilder().apply {
        this.disableProfilePicture = false
        this.disableChat = false
        this.disableLocation = false
      }.build()
    }.build()
    CoroutineScope(Dispatchers.IO).launch {

      profileController.setLocalUserProfile(profile = p)

    }
    val profileViewModel =
      ProfileViewModel(
        userId = "hellogrger1232", profileController = profileController
      )
    binding.viewModel = profileViewModel

    profileViewModel.profile.observe(viewLifecycleOwner) {
      Log.e(LOG_KEY, it.toString())
      binding.apply {
        this.username.text = it.userName
        this.bio.text = it.bio
        this.profilePic.setImageURI(Uri.parse(it.imageUrl))
        this.ratingText.text = it.rating.toString()
      }
    }

    binding.parentProfileInfo.setOnClickListener {
      (activity as RouteToEditProfileActivity).routeToEditProfileActivity(
        profileViewModel
          .profile.value!!
      )
    }

    return binding.root
  }

  companion object {

    const val TAG = "ProfileFragment"

    fun newInstance(): ProfileFragment {
      return ProfileFragment()
    }
  }
}
