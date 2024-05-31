package com.example.nextgen.message

import com.example.model.Message
import com.example.nextgen.viewmodel.ObservableViewModel

class MessageViewModel(
  private val userId:String,
  private val message:Message,
  messageOnLongPressListener: MessageOnLongPressListener
):ObservableViewModel() {

  val isSender by lazy {
    userId==message.senderId
  }

  val text by lazy {
    message.text
  }

  val timestamp by lazy {
    message.timestamp.toString()
  }
}