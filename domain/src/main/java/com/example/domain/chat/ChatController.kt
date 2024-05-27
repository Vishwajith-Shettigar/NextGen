package com.example.domain.chat

import com.example.domain.constants.CHATS_NODE
import com.example.domain.constants.USERS_COLLECTION
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.util.nextAlphanumericString
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.utility.Result

class ChatController @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val firebaseDatabase: FirebaseDatabase,
) {
  private suspend fun saveUsersChatRecord(user1ID: String, user2ID: String) {
    val chatId = Random.nextAlphanumericString(4) + Random.nextAlphanumericString(6)
    val user1ChatData = hashMapOf(user2ID to chatId)
    val user2ChatData = hashMapOf(user1ID to chatId)
    try {
      firestore.runBatch { batch ->
        val user1DocRef = firestore.collection(USERS_COLLECTION).document(user1ID)
        batch.set(user1DocRef, mapOf("chats" to user1ChatData), SetOptions.merge())
        val user2DocRef = firestore.collection(USERS_COLLECTION).document(user2ID)
        batch.set(user2DocRef, mapOf("chats" to user2ChatData), SetOptions.merge())
      }.await()
    } catch (e: Exception) {
    }
  }

  fun initiateChat(user1ID: String, user2ID: String) {
    CoroutineScope(Dispatchers.IO).launch {
      val chatId = isChatExists(user1ID, user2ID)
      if (chatId != null) {

      } else {
        saveUsersChatRecord(user1ID, user2ID)
      }
    }
  }

  suspend fun isChatExists(user1ID: String, user2ID: String): String? {
    try {
      val userDocument = firestore.collection(USERS_COLLECTION).document(user1ID).get().await()

      if (userDocument.exists()) {
        val chats = userDocument.get("chats") as? Map<*, *>
        if (chats != null && user2ID in chats) {
          val chatId = chats[user2ID] as? String
          if (chatId != null) {
            return chatId
          }
        }
      } else {
        return null
      }

    } catch (e: java.lang.Exception) {
      return null
    }
    return null
  }

  suspend fun sendMessage(
    chatId: String,
    senderId: String,
    receiverId: String,
    text: String,
    callback: ((Result) -> Unit),
  ) {
    try {
      val messageRef = firebaseDatabase.getReference("$CHATS_NODE/$chatId").push()
      val messageData = mapOf(
        "senderId" to senderId,
        "receiverId" to receiverId,
        "text" to text
      )
      messageRef.setValue(messageData).await()
      callback(Result.Success())
    } catch (e: Exception) {
      callback(Result.Failure(e.message ?: "Couldn't send"))
    }
  }
}
