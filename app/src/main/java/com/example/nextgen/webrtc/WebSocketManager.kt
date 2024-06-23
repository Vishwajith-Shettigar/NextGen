package com.example.nextgen.webrtc

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.model.Profile
import com.example.videocallapp.MessageModel
import com.example.videocallapp.TYPE
import com.google.gson.Gson
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake

@Singleton
class WebSocketManager @Inject constructor() {

  private val _message= MutableLiveData<MessageModel>()
  val message: LiveData<MessageModel> get() = _message

  private var webSocket: WebSocketClient?=null
   var UID:String?=null
  private val gson = Gson()

  val serverUri = URI("ws://192.168.43.214:2000")

  fun initSocket(uid:String){
    UID = uid

    webSocket = object:WebSocketClient(serverUri){
      override fun onOpen(handshakedata: ServerHandshake?) {
        sendMessageToSocket(
          MessageModel(
            TYPE.STORE_USER,uid,null,null
          )
        )
        Log.d("#","HANDSHAKEDATA:- ${handshakedata.toString()}")
      }

      override fun onMessage(message: String?) {
        try {
//          messageInterface.onNewMessage(gson.fromJson(message,MessageModel::class.java))
          _message.postValue(gson.fromJson(message,MessageModel::class.java))

          Log.d("#","ONNEWMESSAGE:- ${message.toString()}")
        }catch (e:Exception){
          e.printStackTrace()
          Log.d("#","EXCEPTION:- ${e.message.toString()}")
        }
      }

      override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("#", "onClose: $reason")
      }

      override fun onError(ex: Exception?) {
        Log.d("#", "onError: $ex")
      }

    }
    webSocket?.connect()
    Log.d("#","Connection:- ${webSocket?.socket?.isConnected.toString()}")
  }

  /*
    The sendMessageToSocket function is responsible for converting a
    MessageModel object to JSON and sending it to the WebSocket server.
  */

  fun sendMessageToSocket(message: MessageModel) {
    Log.e("vish","cree ofee")
    try {
      Log.e("vish", "sendMessageToSocket: $message")
      webSocket?.send(Gson().toJson(message))
      Log.e("vish", "sendMessageToSocket JSON FORMAT: ${Gson().toJson(message)}")
    } catch (e: Exception) {
      Log.e("vish", "sendMessageToSocket: $e")
    }
  }
}