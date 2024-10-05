package com.example.nextgen.webrtc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nextgen.BuildConfig
import com.example.videocallapp.MessageModel
import com.example.videocallapp.TYPE
import com.google.gson.Gson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor() {

  private val _message= MutableLiveData<MessageModel>()
  val message: LiveData<MessageModel> get() = _message

  private var webSocket: WebSocketClient?=null
   var UID:String?=null
  private val gson = Gson()

  val serverUri = URI(BuildConfig.SERVER_URL)

  fun initSocket(uid:String){
    UID = uid

    webSocket = object:WebSocketClient(serverUri){
      override fun onOpen(handshakedata: ServerHandshake?) {
        sendMessageToSocket(
          MessageModel(
            TYPE.STORE_USER,uid,null,null
          )
        )
      }

      override fun onMessage(message: String?) {
        try {
          _message.postValue(gson.fromJson(message,MessageModel::class.java))
        }catch (e:Exception){
          e.printStackTrace()
        }
      }

      override fun onClose(code: Int, reason: String?, remote: Boolean) {
      }

      override fun onError(ex: Exception?) {
      }

    }
    webSocket?.connect()
  }

  /*
    The sendMessageToSocket function is responsible for converting a
    MessageModel object to JSON and sending it to the WebSocket server.
  */

  fun sendMessageToSocket(message: MessageModel) {
    try {
      webSocket?.send(Gson().toJson(message))
    } catch (e: Exception) {
    }
  }
}