package com.example.nextgen.editprofile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.domain.constants.LOG_KEY
import com.example.domain.profile.ProfileController
import com.example.model.Profile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentEditProfileBinding
import com.example.nextgen.profile.ProfileViewModel
import com.example.utility.getProto
import com.example.utility.putProto
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val REQUEST_IMAGE_CAPTURE = 1

class EditProfileFragment : BaseFragment() {

  lateinit var binding: FragmentEditProfileBinding

  @Inject
  lateinit var profileController: ProfileController

  lateinit var profile: Profile
  lateinit var editProfileViewModel: EditProfileViewModel

  var bitmap: Bitmap? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

  }

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    // Inflate the layout for this fragment
    binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
    profile =
      arguments?.getProto(EDIT_PROFILE_FRAGMENT_ARGUMENTS_KEY, Profile.getDefaultInstance())!!
    Log.e(LOG_KEY, profile.toString())
    editProfileViewModel = EditProfileViewModel(profile, profileController)
    binding.viewModel = editProfileViewModel
    binding.lifecycleOwner = this

    binding.buttonSave.setOnClickListener {
      binding.loadingBlur.visibility = View.VISIBLE
      if (bitmap != null) {
        editProfileViewModel.storeNewImage(bitmap!!) {
          if (it is com.example.utility.Result.Success) {
            updateProfile(profile)
          }
        }
      } else {
        updateProfile(profile)
      }

    }

    binding.imageViewProfile.setOnClickListener {
      val pickPhotoIntent = Intent(Intent.ACTION_PICK)
      pickPhotoIntent.type = "image/*"
      startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_CAPTURE)
    }
    return binding.root
  }

  fun updateProfile(profile: Profile) {
    val newProfile = profile.toBuilder()
    newProfile.userName = binding.editTextUsername.text.toString()
    newProfile.firstName = binding.editTextFirstName.text.toString()
    newProfile.lastName = binding.editTextLastName.text.toString()
    newProfile.bio = binding.editTextBio.text.toString()
    newProfile.imageUrl = editProfileViewModel.imageUrl
    val modifiedProfile = newProfile.build()
    editProfileViewModel.updateUserProfile(modifiedProfile) {
      if (it is com.example.utility.Result.Success) {
        binding.loadingBlur.visibility = View.GONE
        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
      } else {
        binding.loadingBlur.visibility = View.GONE
        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()

      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      data?.data?.let { uri ->
        bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        // Do something with the bitmap (e.g., display it in an ImageView, upload it, etc.)
        binding.imageViewProfile.setImageBitmap(bitmap)
      }
    }
  }


  companion object {

    private val EDIT_PROFILE_FRAGMENT_ARGUMENTS_KEY = "EditProfileFragment.arguments"

    @JvmStatic
    fun newInstance(profile: Profile) =
      EditProfileFragment().apply {
        arguments = Bundle().apply {
          putProto(EDIT_PROFILE_FRAGMENT_ARGUMENTS_KEY, profile)
        }
      }
  }
}
