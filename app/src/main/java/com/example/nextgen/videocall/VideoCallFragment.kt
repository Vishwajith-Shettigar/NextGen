package com.example.nextgen.videocall

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.domain.profile.ProfileController
import com.example.model.VideoCallScreenArguments
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentVideoCallBinding
import com.example.nextgen.webrtc.WebSocketManager
import com.example.utility.getProto
import com.example.utility.putProto
import com.example.videocallapp.*
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.SessionDescription
import javax.inject.Inject

class VideoCallFragment : BaseFragment() {

  private lateinit var binding: FragmentVideoCallBinding

  @Inject
  lateinit var activity: AppCompatActivity

  lateinit var userId: String
  lateinit var imageUrl: String
  lateinit var userName: String
  lateinit var targetUID: String

  private var rtcClient: RTCClient? = null
  private val gson = Gson()
  private var isMute = false
  private var isCameraPause = false
  private val rtcAudioManager by lazy { RtcAudioManager.create(requireContext()) }
  private var isSpeakerMode = true

  private lateinit var userRole: com.example.model.UserRole

  private lateinit var data: String

  @Inject
  lateinit var webSocketManager: WebSocketManager

  @Inject
  lateinit var profileController: ProfileController

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    // Inflate the layout for this fragment
    binding = FragmentVideoCallBinding.inflate(inflater, container, false)

    val args = requireArguments().getProto(
      VIDEO_CALL_FRAGMENT_KEY,
      VideoCallScreenArguments.getDefaultInstance()
    )

    targetUID = args.targetId
    userRole = args.userRole
    data = args.data
    userId = args.userId
    imageUrl = args?.imageUrl.toString()
    userName = args.userName

    init()

    getPermissionsForVideoCall()

    webSocketManager.message.observe(activity) {
      onNewMessage(it)
    }

    return binding.root
  }

  private fun init() {
    /*
    Initializes an instance of WebSocketManager and passes this
    (referring to the current fragment) as a callback.
    */

    /*
    Calls initSocket on WebSocketManager with the user's ID (uid)
    if it is not null.
    */

    /*
    Initializes an instance of RTCClient with the application context,
    user ID (uid), WebSocketManager, and a PeerConnectionObserver.

    Overrides onIceCandidate and onAddStream methods of the
    PeerConnectionObserver.

    In onIceCandidate, sends ICE candidate information to the other
    party via WebSocket.
    */

    rtcClient = RTCClient(
      activity.application!!,
      userId,
      userName,
      imageUrl,
      webSocketManager,
      object : PeerConnectionObserver() {
        override fun onIceCandidate(p0: IceCandidate?) {
          super.onIceCandidate(p0)
          rtcClient?.addIceCandidate(p0)
          val candidate = hashMapOf(
            "sdpMid" to p0?.sdpMid,
            "sdpMLineIndex" to p0?.sdpMLineIndex,
            "sdpCandidate" to p0?.sdp
          )

          webSocketManager?.sendMessageToSocket(
            MessageModel(TYPE.ICE_CANDIDATE, userId, targetUID, candidate)
          )
        }

        override fun onAddStream(p0: MediaStream?) {
          super.onAddStream(p0)
          p0?.videoTracks?.get(0)?.addSink(binding?.remoteView)
        }
      })


    // Sets the default audio device to the speaker phone using
    // rtcAudioManager.setDefaultAudioDevice.
    rtcAudioManager.setDefaultAudioDevice(RtcAudioManager.AudioDevice.SPEAKER_PHONE)

    // Switching camera
    binding?.switchCameraButton?.setOnClickListener {
      rtcClient?.switchCamera()
    }

    // Mic handling
    binding?.micButton?.setOnClickListener {
      if (isMute) {
        isMute = false
        binding!!.micButton.setImageResource(R.drawable.ic_baseline_mic_off_24)
      } else {
        isMute = true
        binding!!.micButton.setImageResource(R.drawable.ic_baseline_mic_24)
      }
      rtcClient?.toggleAudio(isMute)
    }

    // Video button handling
    binding?.videoButton?.setOnClickListener {
      if (isCameraPause) {
        isCameraPause = false
        binding!!.videoButton.setImageResource(R.drawable.ic_baseline_videocam_24)
      } else {
        isCameraPause = true
        binding!!.videoButton.setImageResource(R.drawable.ic_baseline_videocam_24)
      }
      rtcClient?.toggleCamera(isCameraPause)
    }

    // Speaker button handling
    binding?.audioOutputButton?.setOnClickListener {
      if (isSpeakerMode) {
        isSpeakerMode = false
        binding!!.audioOutputButton.setImageResource(R.drawable.ic_baseline_hearing_24)
        rtcAudioManager.setDefaultAudioDevice(RtcAudioManager.AudioDevice.EARPIECE)
      } else {
        isSpeakerMode = true
        binding!!.audioOutputButton.setImageResource(R.drawable.ic_baseline_speaker_up_24)
        rtcAudioManager.setDefaultAudioDevice(RtcAudioManager.AudioDevice.SPEAKER_PHONE)
      }
    }

    // End call button handling
    binding.endCallButton.setOnClickListener {
      val message = MessageModel(TYPE.CALL_ENDED, userId, targetUID, null)
      webSocketManager.sendMessageToSocket(message)
      exitFromActivity()
    }

    if (userRole == com.example.model.UserRole.CALLER) {
      binding?.callLayout?.visibility = View.VISIBLE
      binding?.apply {
        rtcClient?.initializeSurfaceView(binding.localView)
        rtcClient?.initializeSurfaceView(binding.remoteView)
        rtcClient?.startLocalVideo(binding.localView)
        rtcClient?.call(targetUID)
      }
    } else {
      binding?.callLayout?.visibility = View.VISIBLE
      binding?.apply {
        rtcClient?.initializeSurfaceView(localView)
        rtcClient?.initializeSurfaceView(binding.remoteView)
        rtcClient?.startLocalVideo(localView)
      }
      val session = SessionDescription(
        SessionDescription.Type.OFFER,
        data
      )
      Log.d("OFEERWEBRTC", "UID:- ${targetUID}")
      rtcClient?.onRemoteSessionReceived(session)
      rtcClient?.answer(targetUID)
      binding!!.remoteViewLoading.visibility = View.GONE

    }
  }

  // Here we are requesting the necessary permissions.
  private fun getPermissionsForVideoCall() {
    PermissionX.init(this)
      .permissions(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
      ).request { allGranted, _, _ ->
        if (allGranted) {

        } else {
          Toast.makeText(requireContext(), "you should accept all permissions", Toast.LENGTH_LONG)
            .show()
        }
      }
  }

  /*
  Requests permissions for audio and camera before initiating a video call.

  If permissions are granted, sends a WebSocket message of type START_CALL
  to the target user (doctorUid).
  */
  fun onclick(doctorUid: String) {
    PermissionX.init(this)
      .permissions(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
      ).request { allGranted, _, _ ->
        if (allGranted) {

          try {

            targetUID = doctorUid
            webSocketManager?.sendMessageToSocket(
              MessageModel(
                TYPE.START_CALL, userId, targetUID, null
              )
            )
          } catch (e: Exception) {
          }
        } else {
          Toast.makeText(requireContext(), "you should accept all permissions", Toast.LENGTH_LONG)
            .show()
        }
      }
  }

  /*
   Handles different types of messages received via WebSocket, such as call
   responses (CALL_RESPONSE), answers to calls (ANSWER_RECEIVED), and
   offers for calls (OFFER_RECEIVED).
  */
  fun onNewMessage(message: MessageModel) {
    when (message.type) {
      TYPE.ANSWER_RECIEVED -> {

        val session = SessionDescription(
          SessionDescription.Type.ANSWER,
          message.data.toString()
        )
        rtcClient?.onRemoteSessionReceived(session)
        lifecycleScope.launch {
          withContext(Dispatchers.Main) {
            binding?.remoteViewLoading?.visibility = View.GONE
          }
        }
      }

      // Parses and adds ICE candidates received via WebSocket.
      TYPE.ICE_CANDIDATE -> {
        try {
          val receivingCandidate = gson.fromJson(
            gson.toJson(message.data),
            IceCandidateModel::class.java
          )
          rtcClient?.addIceCandidate(
            IceCandidate(
              receivingCandidate.sdpMid,
              Math.toIntExact(receivingCandidate.sdpMLineIndex.toLong()),
              receivingCandidate.sdpCandidate
            )
          )
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }

      /*Here we handle the response for call ended that we
      receive from the remote peer via WebSocket.*/
      TYPE.CALL_ENDED -> {
        lifecycleScope.launch {
          withContext(Dispatchers.Main) {
            Toast.makeText(requireContext(), "The call has ended", Toast.LENGTH_LONG).show()
            exitFromActivity()
          }
        }
      }

      else -> {}
    }
  }

  override fun onDestroy() {
    super.onDestroy()

    rtcClient?.endCall() // Close any existing WebRTC connections
    rtcClient?.deinitializeSurfaceViews(binding.localView, binding.remoteView)
    rtcClient = null
  }

  fun exitFromActivity() {
    rtcClient?.endCall() // Close any existing WebRTC connections
    rtcClient?.deinitializeSurfaceViews(binding.localView, binding.remoteView)
    activity.finish()
  }


  companion object {
    const val VIDEO_CALL_FRAGMENT_KEY = "VideoCallFragment.key"

    @JvmStatic
    fun newInstance(args: VideoCallScreenArguments) =
      VideoCallFragment().apply {
        arguments = Bundle().apply {
          putProto(VIDEO_CALL_FRAGMENT_KEY, args)
        }
      }
  }
}
