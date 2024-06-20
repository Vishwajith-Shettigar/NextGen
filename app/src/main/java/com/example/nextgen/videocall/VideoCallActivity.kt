package com.example.nextgen.videocall

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import com.example.utility.putProtoExtra
import com.example.videocallapp.MessageModel
import com.example.videocallapp.UserRole

class VideoCallActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_video_call)
    val targetName = intent.getStringExtra("targetName")
    val data = intent.getStringExtra("data")
    val userRole=intent.getStringExtra("userRole")
    supportFragmentManager.beginTransaction().replace(
      R.id.frame_layout, VideoCallFragment.newInstance(
        targetName!!, data!!,userRole!!
      )
    ).commit()
  }

  override fun injectDependencies(activityComponent: ActivityComponent) {
    activityComponent.inject(this)
  }

  companion object {
    const val VIDEO_CALL_ACTIVITY_KEY = "VideoCallActivity.key"

    fun createVideoCallActivity(context: Context, name: String, data: String,userRole:UserRole): Intent {

      //Todo: shift to protobuf
      val videoCallActivity = Intent(context, VideoCallActivity::class.java)
      videoCallActivity.putExtra("targetName", name)
      videoCallActivity.putExtra("data", data)
      videoCallActivity.putExtra("userRole",userRole.name)
      return videoCallActivity
    }

  }
}