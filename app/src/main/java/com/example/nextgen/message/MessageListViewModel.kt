package com.example.nextgen.message

import android.database.Observable
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.chat.ChatController
import com.example.domain.constants.LOG_KEY
import com.example.domain.profile.ProfileController
import com.example.model.Chat
import com.example.model.Message
import com.example.nextgen.viewmodel.ObservableViewModel
import com.example.utility.Result

class MessageListViewModel(
  private val userId: String,
 private val chatController: ChatController,
  private val chat: Chat,
  private val messageOnLongPressListener: MessageOnLongPressListener,
  profileController: ProfileController,
) : ObservableViewModel() {
  private var _messageList = MutableLiveData<List<MessageViewModel>>()
  val messageList: LiveData<List<MessageViewModel>> get() = _messageList

  val status: MutableLiveData<String> = MutableLiveData("fetching....")

  val messageText: MutableLiveData<String> = MutableLiveData("")

   var chatId:String?=chat.chatId
  init {
    profileController.getUserStatus(chat.userId) { result ->
      if (result is Result.Success) {
        Log.e(LOG_KEY, result.data + " hello")
        status.value = (result.data)  // Use postValue for background thread updates
      } else {
        // Handle failure case if necessary
        status.postValue("Error retrieving status") // or some default value
      }
    }
  }


  val userName by lazy {
    chat.userName
  }

  val imageUrl by lazy {
    chat.imageUrl
  }

  init {
    chatController.retrieveMessages(chat.chatId) { result ->
      if (result is com.example.utility.Result.Success) {
        processData(result.data)
      }
    }
  }

  fun onSendClick(view: View){
    if(chatId=="" || chatId==null){
      chatController.initiateChat(userId,chat.userId){result->
        if (result is com.example.utility.Result.Success) {
         chatId=result.data
        }
      }
    }
    chatController.sendMessage(chat.chatId,userId,chat.userId,messageText.value.toString()){
    }
  }

  fun processData(data: List<Message>) {
    val messageViewModelList: MutableList<MessageViewModel> = mutableListOf()
    data.forEach {
      messageViewModelList.add(MessageViewModel(userId, it, messageOnLongPressListener))
    }
    _messageList.value = messageViewModelList
  }

}
