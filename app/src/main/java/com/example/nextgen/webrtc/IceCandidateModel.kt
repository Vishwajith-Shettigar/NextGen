package com.example.videocallapp

class IceCandidateModel(
  val sdpMid: String, // A String representing the SDP (Session Description Protocol) mid of the ICE candidate.
  val sdpMLineIndex: Double, //  A Double representing the SDP MLine index of the ICE candidate.
  val sdpCandidate: String //  A String representing the SDP candidate string of the ICE candidate.
)
