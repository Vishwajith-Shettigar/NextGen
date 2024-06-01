package com.example.nextgen.message

import android.text.format.DateUtils
import android.view.View
import com.example.model.Message
import com.example.nextgen.viewmodel.ObservableViewModel
import java.text.SimpleDateFormat
import java.util.*

class MessageViewModel(
  private val userId:String,
  private val message:Message,
  private val messageOnLongPressListener: MessageOnLongPressListener,
  private val index:Int
):ObservableViewModel() {

  val isSender by lazy {
    userId==message.senderId
  }

  val messageId by lazy{
    message.messageId
  }

  val isDeleted by lazy {
    message.isDeleted
  }

  var text: String = message.text


  val now = System.currentTimeMillis()
  val timestamp: String by lazy {
    val timeDifferenceMillis = now - message.timestamp
    when {
      isToday(message.timestamp) -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(message.timestamp)
      isYesterday(message.timestamp) -> "Yesterday " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(message.timestamp)
      else -> SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault()).format(message.timestamp)
    }
  }

  fun isToday(timestamp: Long): Boolean {
    val nowCalendar = Calendar.getInstance()
    val timestampCalendar = Calendar.getInstance()
    timestampCalendar.timeInMillis = timestamp
    return nowCalendar.get(Calendar.YEAR) == timestampCalendar.get(Calendar.YEAR) &&
      nowCalendar.get(Calendar.DAY_OF_YEAR) == timestampCalendar.get(Calendar.DAY_OF_YEAR)
  }

  fun isYesterday(timestamp: Long): Boolean {
    val nowCalendar = Calendar.getInstance()
    val timestampCalendar = Calendar.getInstance()
    timestampCalendar.timeInMillis = timestamp
    nowCalendar.add(Calendar.DAY_OF_YEAR, -1)
    return nowCalendar.get(Calendar.YEAR) == timestampCalendar.get(Calendar.YEAR) &&
      nowCalendar.get(Calendar.DAY_OF_YEAR) == timestampCalendar.get(Calendar.DAY_OF_YEAR)
  }

  fun onLongClick(view: View):Boolean{
    messageOnLongPressListener.onLongPress(message,index)
    return true
  }



}