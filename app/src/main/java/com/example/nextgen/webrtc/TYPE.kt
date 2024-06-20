package com.example.videocallapp

enum class TYPE {
  STORE_USER, // Indicates a message for storing a user on the server.
  START_CALL,
  CALL_RESPONSE,
  CREATE_OFFER, // Signifies the creation of an offer in WebRTC.
  OFFER_RECIEVED, // Indicates the reception of an offer in WebRTC.
  CREATE_ANSWER, // Signifies the creation of an answer in WebRTC.
  ANSWER_RECIEVED, // ndicates the reception of an answer in WebRTC.
  ICE_CANDIDATE, // Represents the exchange of ICE candidates in WebRTC.
  CALL_ENDED
}