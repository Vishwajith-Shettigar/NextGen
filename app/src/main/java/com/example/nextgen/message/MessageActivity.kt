package com.example.nextgen.message

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.domain.constants.LOG_KEY
import com.example.model.Chat
import com.example.nextgen.R
import com.example.utility.getProtoExtra
import com.example.utility.putProtoExtra

class MessageActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_message)
    val args = intent.getProtoExtra(MESSAGEACTIVITY_INTENT_EXTRAS_KEY, Chat.getDefaultInstance())
  }

  companion object {

    /** Key for MessageActivity extras */
    val MESSAGEACTIVITY_INTENT_EXTRAS_KEY = "MessageActivity.extras"

    fun createMessageActivity(context: Context, chat: Chat): Intent {
      val intent = Intent(context, MessageActivity::class.java)
      intent.putProtoExtra(MESSAGEACTIVITY_INTENT_EXTRAS_KEY, chat)
      return intent
    }
  }
}