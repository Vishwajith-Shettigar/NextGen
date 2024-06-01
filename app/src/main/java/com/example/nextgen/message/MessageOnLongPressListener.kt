package com.example.nextgen.message

import com.example.model.Message

interface MessageOnLongPressListener {
  fun onLongPress(message: Message,index:Int)
}
