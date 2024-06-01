package com.example.nextgen.message

import android.database.Observable
import android.util.Log
import android.view.View
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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

  val messageText = ObservableField<String>()

  init {
    chatController.retrieveMessages(chat.chatId) { result ->
      if (result is com.example.utility.Result.Success) {
        processData(result.data)
      }
    }

    profileController.getUserStatus(chat.userId) { result ->
      if (result is Result.Success) {
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

  fun onSendClick(view: View) {
    chatController.sendMessage(chat.chatId, userId, chat.userId, messageText.get().toString()) {
      if(it is com.example.utility.Result.Success)
        messageText.set("")
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
