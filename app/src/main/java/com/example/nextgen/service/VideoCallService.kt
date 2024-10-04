package com.example.nextgen.service

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.nextgen.Application.MyApplication
import com.example.nextgen.R
import com.example.nextgen.webrtc.WebSocketManager
import com.example.videocallapp.TYPE
import javax.inject.Inject


class VideoCallService:android.app.Service() {

  @Inject
  lateinit var webSocketManager: WebSocketManager

  @Inject
  lateinit var context: Context

  private lateinit var userId:String

  private val NOTIFICATION_ID=101

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
     userId = intent?.getStringExtra("USER_ID")!!
    webSocketManager.initSocket(userId)
    // Use the data
    return START_NOT_STICKY
  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate() {
    super.onCreate()
    (applicationContext as MyApplication).appComponent.inject(this)

    webSocketManager.message.observeForever { messageModel ->
      if (messageModel?.type == null )
        return@observeForever
      else {
        when (messageModel.type) {
          TYPE.OFFER_RECIEVED -> {
            createVideoCallNotification(messageModel.imageUrl,messageModel.userName!!)

          }
          TYPE.CALL_ENDED -> {
            with(NotificationManagerCompat.from(this)) {
              cancel(NOTIFICATION_ID)
            }
          }
          else -> {}
        }
      }
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun createVideoCallNotification(imageUrl:String?,userName:String){

    val acceptIntent = Intent(this, CallActionReceiver::class.java).apply {
      action = "ACTION_ACCEPT"
    }
    val rejectIntent = Intent(this, CallActionReceiver::class.java).apply {
      action = "ACTION_REJECT"
    }

    val acceptPendingIntent = PendingIntent.getBroadcast(this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val rejectPendingIntent = PendingIntent.getBroadcast(this, 0, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(this, "CALL_CHANNEL_ID")
      .setSmallIcon(R.drawable.person_24) // Replace with your own icon
      .setContentTitle("Incoming Call")
      .setContentText("Caller: $userName")
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_CALL)
      .setAutoCancel(true)
      .addAction(R.drawable.ic_accept, "Accept", acceptPendingIntent)
      .addAction(R.drawable.ic_reject, "Reject", rejectPendingIntent)

    // Optionally set the caller image
    if(!imageUrl.isNullOrBlank()){
      val callerImage = BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.parse(imageUrl)))
      builder.setLargeIcon(callerImage)
    }else
  {
    val callerImage = BitmapFactory.decodeResource(resources, R.drawable.profile_placeholder)
    builder.setLargeIcon(callerImage)
  }

    with(NotificationManagerCompat.from(this)) {
      if (ActivityCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
      }
      notify(NOTIFICATION_ID, builder.build())
    }
  }

  override fun onBind(p0: Intent?): IBinder? {
    return null
  }
}

