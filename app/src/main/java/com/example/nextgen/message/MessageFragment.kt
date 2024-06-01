package com.example.nextgen.message

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.example.domain.chat.ChatController
import com.example.domain.constants.LOG_KEY
import com.example.domain.profile.ProfileController
import com.example.model.Chat
import com.example.model.Message
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
import com.example.utility.getProto
import com.example.utility.putProto
import javax.inject.Inject

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

  private val userId by lazy {
    profileController.getUserId()
  }

  lateinit var chat: Chat

  lateinit var viewModelFactory: MessageListViewModelFactory
  lateinit var messageListViewModel: MessageListViewModel

  var messageListAdapter = BaseAdapter<MessageViewModel>()

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    retainInstance = false
    // Inflate the layout for this fragment
    binding = FragmentMessageBinding.inflate(inflater, container, false)
    chat = arguments?.getProto(MESSAGEFRAGMENT_ARGUMENTS_KEY, Chat.getDefaultInstance())!!

    viewModelFactory = MessageListViewModelFactory(
      userId!!,
      chatController,
      chat,
      this as MessageOnLongPressListener,
      profileController
    )
    messageListViewModel =
      ViewModelProvider(this, viewModelFactory)[MessageListViewModel::class.java]


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
