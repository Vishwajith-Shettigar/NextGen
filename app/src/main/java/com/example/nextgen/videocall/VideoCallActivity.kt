package com.example.nextgen.videocall

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.model.VideoCallScreenArguments
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import com.example.utility.getProtoExtra
import com.example.utility.putProtoExtra
import com.example.videocallapp.UserRole

class VideoCallActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_video_call)
    val args =
      intent.getProtoExtra(VIDEO_CALL_ACTIVITY_KEY, VideoCallScreenArguments.getDefaultInstance())
    supportFragmentManager.beginTransaction().replace(
      R.id.frame_layout, VideoCallFragment.newInstance(
        args!!
      )
    ).commit()
  }

  override fun injectDependencies(activityComponent: ActivityComponent) {
    activityComponent.inject(this)
  }

  companion object {
    const val VIDEO_CALL_ACTIVITY_KEY = "VideoCallActivity.key"

    fun createVideoCallActivity(
      context: Context,
      name: String,
      data: String,
      userRole: UserRole,
      userId: String,
      imageUrl: String,
      userName: String
    ): Intent {

      val args = VideoCallScreenArguments.newBuilder().apply {
        this.targetId = name
        this.data = data
        this.userId = userId
        this.imageUrl = imageUrl
        this.userName = userName
        if (userRole == UserRole.CALLER)
          this.userRole = com.example.model.UserRole.CALLER
        else
          this.userRole = com.example.model.UserRole.CALLEE

      }.build()

      //Todo: shift to protobuf
      val videoCallActivity = Intent(context, VideoCallActivity::class.java)
      videoCallActivity.putProtoExtra(VIDEO_CALL_ACTIVITY_KEY, args)
      return videoCallActivity
    }

  }
}
