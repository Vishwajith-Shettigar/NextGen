package com.example.nextgen.message

import com.example.model.Message
import com.example.nextgen.viewmodel.ObservableViewModel

class MessageViewModel(
  private val message:Message,
  messageOnLongPressListener: MessageOnLongPressListener

):ObservableViewModel() {
}