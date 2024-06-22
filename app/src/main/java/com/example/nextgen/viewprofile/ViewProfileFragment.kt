package com.example.nextgen.viewprofile

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.chat.ChatController
import com.example.domain.constants.LOG_KEY
import com.example.domain.profile.ProfileController
import com.example.model.Chat
import com.example.model.Privacy
import com.example.model.Profile
import com.example.model.ViewProfile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.DialogYesNoOptionsBinding
import com.example.nextgen.databinding.FragmentViewProfileBinding
import com.example.nextgen.home.ChatSummaryClickListener
import com.example.nextgen.message.MessageActivity
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
  @Inject
  lateinit var chatController: ChatController

  lateinit var viewProfileViewModel: ViewProfileViewModel

  val userId by lazy {
    profileController.getUserId()
  }

  lateinit var viewProfile:Profile

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    // Inflate the layout for this fragment
     viewProfile =
      arguments?.getProto(VIEWPROFILEFRAGMENT_INTENT_ARGUMENTS_KEY, Profile.getDefaultInstance())!!
    binding = FragmentViewProfileBinding.inflate(inflater, container, false)

    viewProfileViewModel = ViewProfileViewModel(userId!!, viewProfile, profileController,chatController)
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

    if (viewProfile.privacy.disableProfilePicture)
//      binding.chatIcon.setImageDrawable(R.drawable.cha)

    binding.chatIcon.setOnClickListener {
      if(viewProfileViewModel.chatId==null){
        showMessageDialog()
      }else{
        routeToMessageScreen(viewProfile, viewProfileViewModel.chatId!!)
      }
    }

    return binding.root
  }

  private fun showMessageDialog(){
    val dialogbinding = DialogYesNoOptionsBinding.inflate(layoutInflater)

    dialogbinding.tvDialogTitle.text="Say hi :) ?"
    dialogbinding.rbYes.visibility=View.GONE
    dialogbinding.rbNo.visibility=View.GONE
    dialogbinding.btnSave.text="Send"
    dialogbinding.btnCancel.text="Cancel"
    // Build the AlertDialog
    val builder = AlertDialog.Builder(activity)
    builder.setView(dialogbinding.root)
    val alertDialog = builder.create()
    dialogbinding.btnSave.setOnClickListener {
      if (viewProfileViewModel.chatId.isNullOrBlank()) {
        Log.e(LOG_KEY, "is null")
        chatController.initiateChat(userId!!, viewProfile.userId) {
          if (it is com.example.utility.Result.Success) {
            Log.e(LOG_KEY, "succsss nitiate")
            sendMesssage(viewProfile, it.data)
            alertDialog.dismiss()
          } else {
            Toast.makeText(requireContext(), "Something went wrong !", Toast.LENGTH_SHORT)
              .show()
          }
        }
      }
    }

    dialogbinding.btnCancel.setOnClickListener {
      // Do something when Cancel is clicked
      alertDialog.dismiss()
    }

    // Create the AlertDialog

    alertDialog.show()
  }


  fun sendMesssage(viewProfile: Profile, chatId: String) {
    CoroutineScope(Dispatchers.IO).launch {
      chatController.sendMessage(chatId, userId!!, viewProfile.userId, "Hi!") {
        if (it is com.example.utility.Result.Success) {
          Log.e(LOG_KEY, "succsss send message")

        routeToMessageScreen(viewProfile,chatId)
        } else {
          Toast.makeText(requireContext(), "Something went wrong !", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  fun routeToMessageScreen(viewProfile: Profile, chatId: String){
    val chat = Chat.newBuilder().apply {
      this.chatId = chatId
      this.userId = viewProfile.userId
      this.imageUrl = viewProfile.imageUrl ?: ""
      this.userName = viewProfile.userName ?: ""
    }.build()

    startActivity(MessageActivity.createMessageActivity(activity, chat))
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
