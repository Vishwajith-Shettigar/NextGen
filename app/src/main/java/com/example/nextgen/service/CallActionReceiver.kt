package com.example.nextgen.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class CallActionReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    when (intent.action) {
      "ACTION_ACCEPT" -> {
        // Handle accept action
        Toast.makeText(context, "Call Accepted", Toast.LENGTH_SHORT).show()

      }
      "ACTION_REJECT" -> {
        // Handle reject action
        Toast.makeText(context, "Call Rejected", Toast.LENGTH_SHORT).show()
      }
    }
  }
}
