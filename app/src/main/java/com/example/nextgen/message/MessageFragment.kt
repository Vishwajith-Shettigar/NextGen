package com.example.nextgen.message

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.example.domain.chat.ChatController
import com.example.domain.constants.LOG_KEY
import com.example.domain.constants.VIDEO_CALL_AVAILABLE
import com.example.domain.nearby.NearByController
import com.example.domain.profile.ProfileController
import com.example.model.Chat
import com.example.model.Message
import com.example.model.Profile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.Fragment.FragmentScope
import com.example.nextgen.R
import com.example.nextgen.databinding.ChatLayoutBinding
import com.example.nextgen.databinding.FragmentMessageBinding
import com.example.nextgen.databinding.ReceiverMessageLayoutBinding
import com.example.nextgen.databinding.SenderMessageLayoutBinding
import com.example.nextgen.home.ChatSummaryClickListener
import com.example.nextgen.home.ChatViewModel
import com.example.nextgen.home.HomeItemViewModel
import com.example.nextgen.home.HomeViewModel
import com.example.nextgen.recyclerview.BaseAdapter
import com.example.nextgen.videocall.VideoCallActivity
import com.example.nextgen.viewprofile.ViewProfileActivity
import com.example.nextgen.webrtc.WebSocketManager
import com.example.utility.getProto
import com.example.utility.putProto
import com.example.videocallapp.MessageModel
import com.example.videocallapp.TYPE
import com.example.videocallapp.UserRole
import com.permissionx.guolindev.PermissionX
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageFragment : BaseFragment(), MessageOnLongPressListener {
  private lateinit var binding: FragmentMessageBinding

  @Inject
  lateinit var chatController: ChatController

  @Inject
  lateinit var fragment: Fragment

  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var profileController: ProfileController

  @Inject
  lateinit var nearByController: NearByController

  @Inject
  lateinit var webSocketManager: WebSocketManager

  private val userId by lazy {
    profileController.getUserId()
  }

  lateinit var profile: Profile

  var viewProfile: Profile? = null

  lateinit var chat: Chat

  lateinit var viewModelFactory: MessageListViewModelFactory
  lateinit var messageListViewModel: MessageListViewModel

  var messageListAdapter = BaseAdapter<MessageViewModel>()

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onResume() {
    super.onResume()
    getViewProfile()
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    retainInstance = false
    // Inflate the layout for this fragment
    binding = FragmentMessageBinding.inflate(inflater, container, false)
    chat = arguments?.getProto(MESSAGEFRAGMENT_ARGUMENTS_KEY, Chat.getDefaultInstance())!!

    lifecycleScope.launch {
      profile = profileController.getLocalUserProfile(userId!!)!!
    }

    viewModelFactory = MessageListViewModelFactory(
      userId!!,
      chatController,
      chat,
      this as MessageOnLongPressListener,
      profileController,
      nearByController
    )
    messageListViewModel =
      ViewModelProvider(this, viewModelFactory)[MessageListViewModel::class.java]

    getViewProfile()

    val chatLayoutManager = LinearLayoutManager(activity.applicationContext)
    binding.viewModel = messageListViewModel
    binding.lifecycleOwner = this

    binding.recyclerview.apply {
      adapter = messageListAdapter
      layoutManager = chatLayoutManager
    }

    messageListViewModel.messageList.observe(viewLifecycleOwner) {
      messageListAdapter.itemList = it as MutableList<MessageViewModel>
      binding.recyclerview.scrollToPosition(it.size - 1)
    }

    messageListAdapter.expressionGetViewType = { messageViewModel ->
      if (messageViewModel.isSender)
        BaseAdapter.ViewType.SENDER_MESSAGE
      else
        BaseAdapter.ViewType.RECEIVER_MESSAGE
    }

    messageListAdapter.expressionOnCreateViewHolder = { viewGroup, viewType ->
      when (viewType) {
        BaseAdapter.ViewType.SENDER_MESSAGE -> {
          SenderMessageLayoutBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
          )
        }
        BaseAdapter.ViewType.RECEIVER_MESSAGE -> {
          ReceiverMessageLayoutBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
          )
        }
        else -> {
          throw IllegalArgumentException("Encountered unexpected view type: $viewType")
        }
      }
    }

    messageListAdapter.expressionViewHolderBinding = { viewModel, viewtype, viewBinding ->


      val itemBinding: ViewBinding
      if (viewtype == BaseAdapter.ViewType.SENDER_MESSAGE) {
        itemBinding = viewBinding as SenderMessageLayoutBinding
        itemBinding.viewModel = viewModel
      } else {
        itemBinding = viewBinding as ReceiverMessageLayoutBinding
        itemBinding.viewModel = viewModel

      }
    }

    binding.videoCallBtn.setOnClickListener {
      if (VIDEO_CALL_AVAILABLE==false) {
        Toast.makeText(activity, "Not available right now.", Toast.LENGTH_LONG).show()
        return@setOnClickListener
      }
      PermissionX.init(this)
        .permissions(
          Manifest.permission.RECORD_AUDIO,
          Manifest.permission.CAMERA
        ).request { allGranted, _, _ ->
          if (allGranted) {
            try {
              val targetUID = chat.userId
              webSocketManager.sendMessageToSocket(
                MessageModel(
                  TYPE.START_CALL, userId, targetUID, null
                )
              )
            } catch (e: Exception) {
            }
          } else {
            Toast.makeText(requireContext(), "you should accept all permissions", Toast.LENGTH_LONG)
              .show()
          }
        }
    }

    webSocketManager.message.observe(viewLifecycleOwner) { message ->
      if (message?.type == null)
        return@observe

      when (message.type) {
        TYPE.CALL_RESPONSE -> {
          if (!message.isOnline!!) {
            //user is not reachable
            lifecycleScope.launch {
              withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "user is not reachable", Toast.LENGTH_LONG).show()

              }
            }
          } else if (!message.isAvailable!!) {
            //user is not reachable
            lifecycleScope.launch {
              withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), message.data.toString(), Toast.LENGTH_LONG).show()

              }
            }
          } else {
            //we are ready for call, we started a call
            lifecycleScope.launch {
              withContext(Dispatchers.Main) {

                startActivity(
                  VideoCallActivity.createVideoCallActivity(
                    activity, chat.userId, "", UserRole.CALLER,
                    profile.userId, profile.imageUrl, profile.userName
                  )
                )


              }
            }
          }
        }
        else -> {}
      }
    }

    binding.topLayout.setOnClickListener {
      if (viewProfile != null)
        startActivity(ViewProfileActivity.createViewProfileActivity(activity, viewProfile!!))
    }

    return binding.root
  }

  companion object {

    private val MESSAGEFRAGMENT_ARGUMENTS_KEY = "MessageFragment.arguments"

    fun newInstance(chat: Chat) =
      MessageFragment().apply {
        arguments = Bundle().apply {
          putProto(MESSAGEFRAGMENT_ARGUMENTS_KEY, chat)
        }
      }
  }

  private fun getViewProfile() {
    messageListViewModel.getUserDetails {
      if (it is com.example.utility.Result.Success) {
        viewProfile = it.data
      }
    }
  }

  override fun onLongPress(message: Message, index: Int) {
    if (!message.isDeleted && message.senderId == userId) {
      binding.deletechat.visibility = View.VISIBLE

      binding.deletechat.setOnClickListener {

        messageListViewModel.deleteMessage(message, index, messageListAdapter.itemList.size) {
          if (it is com.example.utility.Result.Success)
            binding.deletechat.visibility = View.GONE
        }
      }
    }
  }
}
