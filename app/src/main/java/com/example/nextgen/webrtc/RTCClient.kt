package com.example.videocallapp

import android.app.Application
import android.os.Build
import android.util.Log
import com.example.domain.constants.LOG_KEY
import com.example.nextgen.webrtc.WebSocketManager
import org.webrtc.*
import org.webrtc.audio.JavaAudioDeviceModule


class RTCClient(
  private val application: Application,
  private val userId: String,
  private val userName: String,
  private val imageUrl: String?,
  private val webSocketManager: WebSocketManager,
  private val observer: PeerConnection.Observer,
) {

  init {
    initPeerConnectionFactory(application)
  }

  /*
    The code initializes essential components of WebRTC, including the
    EglBase for rendering, PeerConnectionFactory for managing peer
    connections, ICE servers for STUN and TURN, and local media sources.
  */

  private val eglContext = EglBase.create()
  private val peerConnectionFactory by lazy { createPeerConnectionFactory() }
  private val iceServer = listOf(
    PeerConnection.IceServer.builder("stun:iphone-stun.strato-iphone.de:3478")
      .createIceServer(),
    PeerConnection.IceServer("stun:openrelay.metered.ca:80"),
    PeerConnection.IceServer(
      "turn:openrelay.metered.ca:80",
      "openrelayproject",
      "openrelayproject"
    ),
    PeerConnection.IceServer(
      "turn:openrelay.metered.ca:443",
      "openrelayproject",
      "openrelayproject"
    ),
    PeerConnection.IceServer(
      "turn:openrelay.metered.ca:443?transport=tcp",
      "openrelayproject",
      "openrelayproject"
    ),

    )
  private val peerConnection by lazy { createPeerConnection(observer) }
  private val localVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }
  private val localAudioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints()) }
  private var videoCapturer: CameraVideoCapturer? = null
  private var localAudioTrack: AudioTrack? = null
  private var localVideoTrack: VideoTrack? = null


  /*
    These next three functions handle the initialization and creation of the
    PeerConnectionFactory and PeerConnection instances with appropriate
    configurations.
  */
  private fun initPeerConnectionFactory(application: Application) {
    val peerConnectionOption = PeerConnectionFactory.InitializationOptions.builder(application)
      .setEnableInternalTracer(true)
      .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
      .createInitializationOptions()

    PeerConnectionFactory.initialize(peerConnectionOption)
  }

  private fun createPeerConnectionFactory(): PeerConnectionFactory {
    return PeerConnectionFactory.builder()
      .setVideoEncoderFactory(
        DefaultVideoEncoderFactory(
          eglContext.eglBaseContext,
          true,
          true
        )
      )
      .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglContext.eglBaseContext))
      .setOptions(PeerConnectionFactory.Options().apply {
        disableEncryption = true
        disableNetworkMonitor = true
      }).setAudioDeviceModule(
        JavaAudioDeviceModule.builder(application)
          .setUseHardwareAcousticEchoCanceler(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
          .setUseHardwareNoiseSuppressor(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
          .createAudioDeviceModule().also {
            it.setMicrophoneMute(false)
            it.setSpeakerMute(false)
          }
      ).createPeerConnectionFactory()
  }

  private fun createPeerConnection(observer: PeerConnection.Observer): PeerConnection? {
    return peerConnectionFactory.createPeerConnection(iceServer, observer)
  }

  /*
   These next three functions handle the initialization of a SurfaceViewRenderer
   for local video and starting the local video capture.
  */

  fun initializeSurfaceView(surface: SurfaceViewRenderer) {
    surface.run {
      setEnableHardwareScaler(true)
      setMirror(true)
      init(eglContext.eglBaseContext, null)
    }
  }

  fun startLocalVideo(surface: SurfaceViewRenderer) {
    val surfaceTextureHelper =
      SurfaceTextureHelper.create(Thread.currentThread().name, eglContext.eglBaseContext)
    videoCapturer = getVideoCapturer(application)
    videoCapturer?.initialize(
      surfaceTextureHelper,
      surface.context, localVideoSource.capturerObserver
    )
    videoCapturer?.startCapture(320, 240, 30)
    localVideoTrack = peerConnectionFactory.createVideoTrack("local_track", localVideoSource)
    localVideoTrack?.addSink(surface)
    localAudioTrack =
      peerConnectionFactory.createAudioTrack("local_track_audio", localAudioSource)
    val localStream = peerConnectionFactory.createLocalMediaStream("local_stream")
    localStream.addTrack(localAudioTrack)
    localStream.addTrack(localVideoTrack)

    peerConnection?.addStream(localStream)

  }

  private fun getVideoCapturer(application: Application): CameraVideoCapturer {
    return Camera2Enumerator(application).run {
      deviceNames.find {
        isFrontFacing(it)
      }?.let {
        createCapturer(it, null)
      } ?: throw IllegalStateException()
    }
  }

  /*
    These functions are responsible for initiating a call, handling the
    reception of a remote session description, and answering an incoming
    call.
  */

  fun call(target: String) {
    val mediaConstraints = MediaConstraints()
    mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))

    try {


      peerConnection?.createOffer(object : SdpObserver {
        override fun onCreateSuccess(desc: SessionDescription?) {
          peerConnection?.setLocalDescription(object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {

            }

            override fun onSetSuccess() {
              val offer = hashMapOf(
                "sdp" to desc?.description,
                "type" to desc?.type
              )
              webSocketManager.sendMessageToSocket(
                MessageModel(
                  TYPE.CREATE_OFFER, userId, target, offer, null, null, imageUrl, userName
                )
              )
            }

            override fun onCreateFailure(p0: String?) {
            }

            override fun onSetFailure(p0: String?) {
            }

          }, desc)

        }

        override fun onSetSuccess() {
        }

        override fun onCreateFailure(p0: String?) {
        }

        override fun onSetFailure(p0: String?) {
        }
      }, mediaConstraints)

    } catch (e: Exception) {
      Log.e(LOG_KEY, e.toString())
    }
  }

  fun onRemoteSessionReceived(session: SessionDescription) {
    peerConnection?.setRemoteDescription(object : SdpObserver {
      override fun onCreateSuccess(p0: SessionDescription?) {

      }

      override fun onSetSuccess() {
      }

      override fun onCreateFailure(p0: String?) {
      }

      override fun onSetFailure(p0: String?) {
      }

    }, session)

  }

  fun answer(target: String) {
    val constraints = MediaConstraints()
    constraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))

    try {
      peerConnection?.createAnswer(object : SdpObserver {
        override fun onCreateSuccess(desc: SessionDescription?) {
          peerConnection?.setLocalDescription(object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {
            }


            override fun onSetSuccess() {
              val answer = hashMapOf(
                "sdp" to desc?.description,
                "type" to desc?.type
              )
              webSocketManager.sendMessageToSocket(
                MessageModel(
                  TYPE.CREATE_ANSWER, userId, target, answer
                )
              )
            }

            override fun onCreateFailure(p0: String?) {
            }

            override fun onSetFailure(p0: String?) {
            }

          }, desc)
        }

        override fun onSetSuccess() {
        }

        override fun onCreateFailure(p0: String?) {
        }

        override fun onSetFailure(p0: String?) {
        }
      }, constraints)
    } catch (e: Exception) {
      Log.e(LOG_KEY, e.toString())

    }
  }

  /*
    These functions handle the addition of ICE candidates, camera switching,
    audio toggling, video toggling, and ending a call.
  */

  fun addIceCandidate(p0: IceCandidate?) {
    peerConnection?.addIceCandidate(p0)
  }

  fun switchCamera() {
    videoCapturer?.switchCamera(null)
  }

  fun toggleAudio(mute: Boolean) {
    localAudioTrack?.setEnabled(mute)

  }

  fun toggleCamera(cameraPause: Boolean) {
    if (cameraPause) {
      localVideoTrack?.setEnabled(false) // Disable video track
      videoCapturer?.stopCapture() // Stop video capturer
    } else {
      localVideoTrack?.setEnabled(true) // Enable video track
      videoCapturer?.startCapture(1024, 720, 30) // Restart video capturer
    }
    // No need to re-negotiate as the track is just being enabled/disabled
  }

  fun deinitializeSurfaceViews(
    localSurface: SurfaceViewRenderer,
    remoteSurface: SurfaceViewRenderer,
  ) {
    localVideoTrack?.removeSink(localSurface)
    localSurface.release()
    remoteSurface.release()
  }

  fun endCall() {
    peerConnection?.close()
  }
}
