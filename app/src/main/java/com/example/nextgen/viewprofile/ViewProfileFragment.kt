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
import com.example.model.Profile
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

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    // Inflate the layout for this fragment
    val profile =
      arguments?.getProto(VIEWPROFILEFRAGMENT_INTENT_ARGUMENTS_KEY, Profile.getDefaultInstance())
    binding = FragmentViewProfileBinding.inflate(inflater, container, false)
    binding.ratingBar.setOnTouchListener { view, motionEvent ->

      when (motionEvent.action) {

        MotionEvent.ACTION_UP -> {

          Log.e(LOG_KEY, "rate user " + binding.ratingBar.rating)
          CoroutineScope(Dispatchers.IO).launch {
            profileController.updateUserRating(
              profile!!.userId,
              "Du1sIlaq3QhA484QKfQ9R9XsEfn2",
              binding.ratingBar.rating
            )
          }
          true
        }
        else -> {
          false
        }
      }

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
