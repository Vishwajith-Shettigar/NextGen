package com.example.nextgen.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.domain.chat.ChatController
import com.example.domain.nearby.NearByController
import com.example.domain.profile.ProfileController
import com.example.model.Chat
class MessageListViewModelFactory(
  private val userId: String,
  private val chatController: ChatController,
  private val chat: Chat,
  private val messageOnLongPressListener: MessageOnLongPressListener,
  private val profileController: ProfileController,
  private val nearByController: NearByController
) : ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
    if (modelClass.isAssignableFrom(MessageListViewModel::class.java)) {
      return MessageListViewModel(userId, chatController, chat, messageOnLongPressListener, profileController,nearByController) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}

