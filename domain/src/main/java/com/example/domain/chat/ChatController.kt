package com.example.domain.chat

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatController @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val firebaseDatabase: FirebaseDatabase

  ) {

  fun initiateChat(user1ID:String,user2ID: String){
    CoroutineScope(Dispatchers.IO).launch{
//      isChatExists(user1ID,user2ID)
    }
  }

//  suspend fun isChatExists(user1ID:String,user2ID: String){
//    firestore.collection()
//
//  }

}