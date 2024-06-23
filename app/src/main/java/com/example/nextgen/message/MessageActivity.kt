package com.example.nextgen.message

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.domain.constants.LOG_KEY
import com.example.model.Chat
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.ActivityScope
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import com.example.nextgen.webrtc.WebSocketManager
import com.example.utility.getProtoExtra
import com.example.utility.putProtoExtra
import javax.inject.Inject


class MessageActivity : BaseActivity() {

  @Inject
  lateinit var webSocketManager: WebSocketManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_message)
    val args = intent.getProtoExtra(MESSAGEACTIVITY_INTENT_EXTRAS_KEY, Chat.getDefaultInstance())
    Log.e("vish",webSocketManager.UID.toString())

    supportFragmentManager.beginTransaction()
      .replace(R.id.frame_layout, MessageFragment.newInstance(args!!))
      .commit()
  }

  override fun injectDependencies(activityComponent: ActivityComponent) {
    activityComponent.inject(this)
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