package com.example.nextgen.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.chat.ChatController
import com.example.model.Message

class MessageListViewModel(
  chatController: ChatController,
  chatId: String,
  private val messageOnLongPressListener: MessageOnLongPressListener,
) {
  private var _messageList = MutableLiveData<List<MessageViewModel>>()
  val messageList: LiveData<List<MessageViewModel>> get() = _messageList

  init {
    chatController.retrieveMessages(chatId) { result ->
      if (result is com.example.utility.Result.Success) {
        processData(result.data)
      }
    }
  }

  fun processData(data: List<Message>) {
    val messageViewModelList: MutableList<MessageViewModel> = mutableListOf()
    data.forEach {
      messageViewModelList.add(MessageViewModel(it, messageOnLongPressListener))
    }
    _messageList.value = messageViewModelList
  }

}
