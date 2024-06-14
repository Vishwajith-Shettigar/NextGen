package com.example.nextgen.viewprofile

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.constants.LOG_KEY
import com.example.domain.profile.ProfileController
import com.example.model.Privacy
import com.example.model.Profile
import com.example.model.ViewProfile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentViewProfileBinding
import com.example.utility.getProto
import com.example.utility.getProtoExtra
import com.example.utility.putProto
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewProfileFragment : BaseFragment() {

  lateinit var binding: FragmentViewProfileBinding

  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var profileController: ProfileController

  lateinit var viewProfileViewModel: ViewProfileViewModel

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    // Inflate the layout for this fragment
    val profile =
      arguments?.getProto(VIEWPROFILEFRAGMENT_INTENT_ARGUMENTS_KEY, Profile.getDefaultInstance())!!
    binding = FragmentViewProfileBinding.inflate(inflater, container, false)

    // Todo :Remove later
    // Test data for debugging.
    val viewProfile = ViewProfile.newBuilder().apply {
      this.userId = "Du1sIlaq3QhA484QKfQ9R9XsEfn2"
      this.userName = "pixel 4"
      this.fullName = "Vishwajith Shettigar"
      this.imageUrl = "https://miro.medium.com/v2/resize:fit:786/format:webp/0*vUlSsz1sMQ38o5gd.jpg"
      this.bio = "Not your type"
      this.privacy = Privacy.newBuilder().apply {
        this.disableProfilePicture = false
        this.disableChat = true
      }.build()
      this.rating = 4.4F
      this.existingRating = 3.5F
    }.build()

    viewProfileViewModel = ViewProfileViewModel(profile!!.userId, viewProfile, profileController)
    binding.ratingBar.setOnTouchListener { view, motionEvent ->
      when (motionEvent.action) {
        MotionEvent.ACTION_UP -> {
          Log.e(LOG_KEY, "rate user " + binding.ratingBar.rating)
          viewProfileViewModel.updateRating(
            binding.ratingBar.rating
          )
          true
        }
        else -> {
          false
        }
      }

    }

    binding.apply {
      viewModel = viewProfileViewModel
      lifecycleOwner = viewLifecycleOwner
    }

    return binding.root
  }

  companion object {
    /** Key for ViewProfileFragment arguments. */
    val VIEWPROFILEFRAGMENT_INTENT_ARGUMENTS_KEY = "ViewProfileFragment.arguments"

    @JvmStatic
    fun newInstance(profile: Profile) =
      ViewProfileFragment().apply {
        arguments = Bundle().apply {
          putProto(VIEWPROFILEFRAGMENT_INTENT_ARGUMENTS_KEY, profile)
        }
      }
  }
}
