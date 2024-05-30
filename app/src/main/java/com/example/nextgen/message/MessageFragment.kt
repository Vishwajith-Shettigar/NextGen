package com.example.nextgen.message

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.model.Chat
import com.example.nextgen.R
import com.example.nextgen.databinding.ChatLayoutBinding
import com.example.nextgen.databinding.FragmentMessageBinding
import com.example.utility.putProto

class MessageFragment : Fragment() {
  private lateinit var binding: FragmentMessageBinding
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    // Inflate the layout for this fragment
    binding = FragmentMessageBinding.inflate(inflater)

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