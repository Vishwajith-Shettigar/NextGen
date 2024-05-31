package com.example.nextgen.message

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.domain.chat.ChatController
import com.example.domain.constants.LOG_KEY
import com.example.model.Chat
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.Fragment.FragmentScope
import com.example.nextgen.R
import com.example.nextgen.databinding.ChatLayoutBinding
import com.example.nextgen.databinding.FragmentMessageBinding
import com.example.utility.getProto
import com.example.utility.putProto
import javax.inject.Inject

class MessageFragment : BaseFragment() {
  private lateinit var binding: FragmentMessageBinding
  @Inject
  lateinit var chatController: ChatController
  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    // Inflate the layout for this fragment
    binding = FragmentMessageBinding.inflate(inflater)
    val args= arguments?.getProto(MESSAGEFRAGMENT_ARGUMENTS_KEY,Chat.getDefaultInstance())
    Log.e(LOG_KEY,args?.chatId.toString())

    chatController.retrieveMessages("w2f6gmz6ac"){
      when(it){
        is com.example.utility.Result.Success->{
          Log.e(LOG_KEY,it.data.size.toString())
        }
        is com.example.utility.Result.Failure->{
          Log.e(LOG_KEY,it.message.toString())
        }
        else -> {}
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
}