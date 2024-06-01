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
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MessageListViewModel(
  private val userId: String,
  private val chatController: ChatController,
  private val chat: Chat,
  private val messageOnLongPressListener: MessageOnLongPressListener,
  profileController: ProfileController,
) : ObservableViewModel() {
  private var _messageList = MutableLiveData<List<MessageViewModel>>()
  val messageList: LiveData<List<MessageViewModel>> get() = _messageList

  private var valueEventListener: ValueEventListener? = null

  val status: MutableLiveData<String> = MutableLiveData("fetching....")

  val messageText = ObservableField<String>()
  override fun onCleared() {
    super.onCleared()
    valueEventListener?.let {
      chatController.removeEventListener(it)
    }
  }

  private val viewModelJob = Job()
  private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  init {
    viewModelScope.launch {
      chatController.updateSeenAndUnreadMessage(chat.userId, userId)

      valueEventListener = chatController.retrieveMessages(chat.chatId) { result ->
        if (result is com.example.utility.Result.Success) {
          processData(result.data)
        }
      }

      profileController.getUserStatus(chat.userId) { result ->
        if (result is Result.Success) {
          status.value = (result.data)
        } else {
          status.postValue("Error retrieving status")
        }
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
    if (!messageText.get().toString().isBlank()) {
      chatController.sendMessage(chat.chatId, userId, chat.userId, messageText.get().toString()) {
        if (it is com.example.utility.Result.Success)
          messageText.set("")
      }
    }

  }

  fun deleteMessage(message: Message,index:Int,totalSize:Int
  ,callback: (Result<String>) -> Unit) {
    chatController.deleteChat(message, chat.chatId,index,totalSize){
      callback(it)
    }
  }

  fun processData(data: List<Message>) {
    val messageViewModelList: MutableList<MessageViewModel> = mutableListOf()
    data.mapIndexed { index, message ->
      messageViewModelList.add(
        MessageViewModel(
          userId, message, messageOnLongPressListener,
          index
        )
      )
    }
    chatController.updateSeenAndUnreadMessage(chat.userId, userId)
    _messageList.value = messageViewModelList
  }
}
