package com.example.nextgen.home

import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import com.example.domain.constants.LOG_KEY
import com.example.model.Chat

class ChatViewModel(
  private val chat: Chat,
  private val chatSummaryClickListener: ChatSummaryClickListener,
) : HomeItemViewModel() {

  init {
    Log.e(LOG_KEY, chat.toString())
  }

  val username by lazy {
    chat.userName
  }
  val imageUrl by lazy {
    chat.imageUrl
  }

  val lastMessage by lazy {
    chat.lastMessage.text
  }

  val isSeen by lazy {
    chat.lastMessage.seen
  }

  val unReadMessage by lazy {
    chat.unreadMessage.toString()
  }

  val now = System.currentTimeMillis()
  val time by lazy {
    DateUtils.getRelativeTimeSpanString(chat.timestamp, now, DateUtils.DAY_IN_MILLIS);
  }

  fun onClickChat(view: View) {
    chatSummaryClickListener.onChatSummaryClicked(chat)
  }
}
