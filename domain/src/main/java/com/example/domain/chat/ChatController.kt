package com.example.domain.chat

import android.util.Log
import com.example.domain.constants.CHATS_NODE
import com.example.domain.constants.LOG_KEY
import com.example.domain.constants.USERS_COLLECTION
import com.example.domain.profile.ProfileController
import com.example.model.Chat
import com.example.model.Message
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.util.nextAlphanumericString
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.tasks.await
import com.example.utility.Result
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ListenerRegistration
import java.util.Date
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.*

@Singleton
class ChatController @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val firebaseDatabase: FirebaseDatabase,
  private val profileController: ProfileController,
) {

  // Saves users chat record.
  suspend fun saveUsersChatRecord(user1ID: String, user2ID: String) {
    val chatId = Random.nextAlphanumericString(4) + Random.nextAlphanumericString(6)
    val user1ChatData = hashMapOf(
      user2ID to hashMapOf(
        "chatId" to chatId,
        "lastMessage" to ""
      )
    )

    val user2ChatData = hashMapOf(
      user1ID to hashMapOf(
        "chatId" to chatId,
        "lastMessage" to ""
      )
    )
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

  // Checks whether chat between two users happened or not.
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

  // Saves last message sent by users
  private suspend fun saveLastMessage(senderId: String, receiverId: String, text: String) {
    Log.e(LOG_KEY, "saveLast")
    try {
      // Creating the paths for the nested fields
      val senderChatField = "chats.$receiverId.lastMessage"
      val receiverChatField = "chats.$senderId.lastMessage"

      // Update the sender's document
      firestore.collection(USERS_COLLECTION).document(senderId)
        .update(senderChatField, text)
        .await()

      // Update the receiver's document
      firestore.collection(USERS_COLLECTION).document(receiverId)
        .update(receiverChatField, text)
        .await()

      Log.e(LOG_KEY, "Last message saved successfully")
    } catch (e: Exception) {
      Log.e(LOG_KEY, "Failed to save last message: ${e.message}")
    }
  }

  // Sends message
  fun sendMessage(
    chatId: String,
    senderId: String,
    receiverId: String,
    text: String,
    callback: (Result<*>) -> Unit,
  ) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val messageRef = firebaseDatabase.getReference("$CHATS_NODE/$chatId").push()
        val messageData = mapOf(
          "senderId" to senderId,
          "receiverId" to receiverId,
          "text" to text,
          "timestamp" to Date().time
        )

        // Use await() to ensure the operation completes
        messageRef.setValue(messageData).await()

        // Save the last message
        saveLastMessage(senderId, receiverId, text)

        // Invoke the callback with success result on the main thread
        withContext(Dispatchers.Main) {
          callback(Result.Success("Successful"))
        }
      } catch (e: Exception) {
        // Invoke the callback with failure result on the main thread
        withContext(Dispatchers.Main) {
          callback(Result.Failure(e.message ?: "Couldn't send message"))
        }
      }
    }
  }

  // Retrieve chats and listens to changes. Changes may be in number of chats or in lastMessage.
  fun retrieveChats(
    user1ID: String,
    callback: (com.example.utility.Result<MutableList<Chat>>) -> Unit,
  ) {
    try {

      val chats: MutableList<Chat> = mutableListOf()
      val userDocumentRef = firestore.collection(USERS_COLLECTION).document(user1ID)

      // Add a listener to listen for real-time updates
      userDocumentRef.addSnapshotListener { snapshot, e ->
        if (e != null) {
          callback(com.example.utility.Result.Failure("Failed to retrieve chats: ${e.message}"))
          return@addSnapshotListener
        }

        if (snapshot != null && snapshot.exists()) {
          val usersList = snapshot.get("chats") as? Map<*, *>
          if (usersList != null) {
            CoroutineScope(Dispatchers.IO).launch {
              val deferredChats = usersList.map { entry ->
                async {
                  val userId = entry.key as String
                  val child = entry.value as Map<*, *>

                  val chatId = child.get("chatId")
                  var imageUrl: String? = null
                  var username: String? = null
                  val lastMessage: String = child.get("lastMessage") as String

                  // Get user profile
                  val userProfileResult = profileController.getUserProfile(userId)
                  when (userProfileResult) {
                    is com.example.utility.Result.Success -> {
                      val doc = userProfileResult.data
                      imageUrl = doc.getString("imageUrl")
                      username = doc.getString("username")
                    }
                    is com.example.utility.Result.Failure -> {
                      // Handle failure if needed
                    }
                  }

                  Chat.newBuilder().apply {
                    this.chatId = chatId as String?
                    this.userId = userId
                    this.imageUrl = imageUrl ?: ""
                    this.userName = username ?: ""
                    this.lastMessage = lastMessage ?: ""
                  }.build()
                }
              }
              val chatList = deferredChats.awaitAll()
              chats.clear()
              chats.addAll(chatList)
              withContext(Dispatchers.Main) {
                callback(com.example.utility.Result.Success(chats))
              }
            }
          } else {
            callback(com.example.utility.Result.Failure("No chats found"))
          }
        } else {
          callback(com.example.utility.Result.Failure("No document found"))
        }
      }
    } catch (e: java.lang.Exception) {
      callback(com.example.utility.Result.Failure(e.toString()))
    }
  }
}
