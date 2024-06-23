package com.example.videocallapp

data class MessageModel(
  val type: TYPE, // An enum of type TYPE indicating the type of the message. It specifies the purpose or category of the message.
  val name: String? = null, // A nullable String representing the name associated with the message. It is used in scenarios such as storing a user or indicating the caller's or callee's name.
  val target: String? = null, // A nullable String representing the target user for the message. It signifies the intended recipient of the message.
  val data: Any? = null, // A generic Any type representing additional data associated with the message. The actual content of data depends on the message type.
  val isOnline: Boolean? = null,
  val isAvailable: Boolean? = null,
  val imageUrl:String?=null,
  val userName:String?=null
)