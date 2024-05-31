package com.example.nextgen.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.chat.ChatController
import com.example.model.Chat
import com.example.model.Message
import com.example.nextgen.viewmodel.ObservableViewModel

class MessageListViewModel(
  private val userId:String,
  chatController: ChatController,
  chat: Chat,
  private val messageOnLongPressListener: MessageOnLongPressListener,
) :ObservableViewModel(){
  private var _messageList = MutableLiveData<List<MessageViewModel>>()
  val messageList: LiveData<List<MessageViewModel>> get() = _messageList

  val userName by lazy {
    chat.userName
  }

  val imageUrl by lazy {
    chat.imageUrl
  }
  init {
    chatController.retrieveMessages("w2f6gmz6ac") { result ->
      if (result is com.example.utility.Result.Success) {
        processData(result.data)
      }
    }
  }

  fun processData(data: List<Message>) {
    val messageViewModelList: MutableList<MessageViewModel> = mutableListOf()
    data.forEach {
      messageViewModelList.add(MessageViewModel(userId,it, messageOnLongPressListener))
    }
    _messageList.value = messageViewModelList
  }

}
