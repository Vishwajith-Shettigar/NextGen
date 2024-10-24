package com.example.nextgen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.data.repository.UserRepo
import com.example.domain.chat.ChatController
import com.example.domain.profile.ProfileController
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentProfileBinding
import com.example.nextgen.editprofile.RouteToEditProfileActivity
import com.example.nextgen.privacy.RouteToPrivacyActivity
import com.squareup.picasso.Picasso
import javax.inject.Inject

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

  lateinit var profileViewModel: ProfileViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onResume() {
    super.onResume()
    profileViewModel.loadProfile()
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    binding = FragmentProfileBinding.inflate(inflater, container, false)
    val userId = profileController.getUserId()

    profileViewModel =
      ProfileViewModel(
        userId = userId!!, profileController = profileController
      )
    binding.viewModel = profileViewModel
    binding.lifecycleOwner = this

profileViewModel.profile.observe(viewLifecycleOwner) {
    binding.apply {
        try {
            this.username.text = it.userName
            this.bio.text = it.bio
            if (it.privacy.disableProfilePicture) {
                Picasso.get().load(R.drawable.profile_placeholder).into(this.profilePic) // Default avatar
            } else if (!it.imageUrl.isBlank()) {
                Picasso.get().load(it.imageUrl)
                    .error(R.drawable.profile_placeholder).into(this.profilePic)
            } else {
                Picasso.get().load(R.drawable.profile_placeholder).into(this.profilePic) // Default avatar
            }
        } catch (e: Exception) {
            e.printStackTrace() // Optional: log the error
        }
    }
}

    binding.parentProfileInfo.setOnClickListener {
      (activity as RouteToEditProfileActivity).routeToEditProfileActivity(
        profileViewModel
          .profile.value!!
      )
    }

    binding.privacy.setOnClickListener {
      (activity as RouteToPrivacyActivity).routeToPrivacyActivity(profileViewModel.profile.value!!)
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
