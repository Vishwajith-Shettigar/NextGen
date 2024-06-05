package com.example.nextgen.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.squareup.picasso.Picasso
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

  private const val REQUEST_IMAGE_CAPTURE = 1

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

//    val p = Profile.newBuilder().apply {
//      this.userId = "hellogrger1232"
//      this.userName = "Dark"
//      this.firstName = "Vish"
//      this.imageUrl = "https://avatars.githubusercontent.com/u/76042077?v=4"
//      this.bio = "Yenna pannitringe"
//      this.rating = 4.5F
//      this.privacy = Privacy.newBuilder().apply {
//        this.disableProfilePicture = false
//        this.disableChat = false
//        this.disableLocation = false
//      }.build()
//    }.build()
//    CoroutineScope(Dispatchers.IO).launch {
//
//      profileController.setLocalUserProfile(profile = p)
//
//    }
    val profileViewModel =
      ProfileViewModel(
        userId = "hellogrger1232", profileController = profileController
      )
    binding.viewModel = profileViewModel
    binding.lifecycleOwner = this

    profileViewModel.profile.observe(viewLifecycleOwner) {
      Log.e(LOG_KEY, it.toString())
      binding.apply {
        this.username.text = it.userName
        this.bio.text = it.bio
        Picasso.get().load(it.imageUrl)
          .error(R.drawable.profile_placeholder).into(this.profilePic)
        this.ratingText.text = it.rating.toString()
      }
    }

    binding.profilePic.setOnClickListener {
      val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    binding.parentProfileInfo.setOnClickListener {
      (activity as RouteToEditProfileActivity).routeToEditProfileActivity(
        profileViewModel
          .profile.value!!
      )
    }

    return binding.root
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
      // Update UI to display the new profile picture
      val imageBitmap = data?.extras?.get("data") as Bitmap
      binding.profilePic.setImageBitmap(imageBitmap)
         // Todo: Store image in firebase

    }
  }
  companion object {

    const val TAG = "ProfileFragment"

    fun newInstance(): ProfileFragment {
      return ProfileFragment()
    }
  }
}
