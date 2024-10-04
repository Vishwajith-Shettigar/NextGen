package com.example.nextgen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.chat.ChatController
import com.example.model.Chat
import com.example.nextgen.viewmodel.ObservableViewModel

class HomeViewModel(
  private val chatController: ChatController,
  private val userId: String,
  private val chatSummaryClickListener: ChatSummaryClickListener
) : ObservableViewModel() {
  private var _chatList = MutableLiveData<List<HomeItemViewModel>>()
  val chatList: LiveData<List<HomeItemViewModel>> get() = _chatList

  init {
    chatController.retrieveChats(userId) { result ->
      if (result is com.example.utility.Result.Success) {
        processData(result.data)
      }
    }
  }

  fun processData(data: MutableList<Chat>) {
    val chatViewModelList: MutableList<HomeItemViewModel> = mutableListOf()
    data.forEach {
      chatViewModelList.add(ChatViewModel(it, chatSummaryClickListener))
    }
    _chatList.value = chatViewModelList
  }
}
