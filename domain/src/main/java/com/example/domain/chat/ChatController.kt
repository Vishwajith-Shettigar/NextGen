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

  fun sendMessage(
    chatId: String,
    senderId: String,
    receiverId: String,
    text: String,
    callback: ((Result<*>) -> Unit),
  ) {
    try {
      val messageRef = firebaseDatabase.getReference("$CHATS_NODE/$chatId").push()
      val messageData = mapOf(
        "senderId" to senderId,
        "receiverId" to receiverId,
        "text" to text,
        "timestamp" to Date().time
      )
      messageRef.setValue(messageData)
      callback(Result.Success("Sent"))
    } catch (e: Exception) {
      callback(Result.Failure(e.message ?: "Couldn't send"))
    }
  }

  fun retrieveChats(
    user1ID: String,
    callback: (com.example.utility.Result<MutableList<Chat>>) -> Unit,
  ) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val chats: MutableList<Chat> = mutableListOf()
        val document = firestore.collection(USERS_COLLECTION).document(user1ID).get().await()
        val usersList = document.get("chats") as? Map<*, *>

        usersList?.mapKeys { entry ->
          val userId = entry.key as String
          val chatId = entry.value as String

          var imageUrl: String? = null
          var username: String? = null
          var lastMessage: String? = null

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

          val userLastMessage = getUserLastMessage(chatId)
          when (userLastMessage) {
            is com.example.utility.Result.Success -> {


              lastMessage = userLastMessage.data.text
            }
            is com.example.utility.Result.Failure -> {
              // Handle failure if needed
            }
          }

          val chat = Chat.newBuilder().apply {
            this.chatId = chatId
            this.userId = userId
            this.imageUrl = imageUrl ?: ""
            this.userName = username ?: ""
            this.lastMessage = lastMessage ?: ""
          }.build()
          chats.add(chat)
        }

        withContext(Dispatchers.Main) {
          callback(com.example.utility.Result.Success(chats))
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          callback(com.example.utility.Result.Failure("Failed to retrieve chats: ${e.message}"))
        }
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  suspend fun getUserLastMessage(chatId: String): Result<Message> {
    return suspendCancellableCoroutine { continuation ->
      val databaseReference = FirebaseDatabase.getInstance().getReference("$CHATS_NODE/$chatId")

      // Query to get the latest chat entry based on the timestamp
      val latestChatQuery = databaseReference.orderByChild("timestamp").limitToLast(1)

      // Attach a value event listener to the query
      val listener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
          // Check if the snapshot has children
          if (dataSnapshot.hasChildren()) {
            // Loop through the result, though there should be only one item
            for (snapshot in dataSnapshot.children) {
              // Assuming your chat data is of type Chat
              val latestChat = snapshot.getValue(Message::class.java)
              // Resume the coroutine with the result
              if (latestChat != null) {
                continuation.resume(Result.Success(latestChat)) {
                }
              } else {
                continuation.resume(Result.Failure("Failed to retrieve latest chat")) {
                }
              }
            }
          } else {
            // Handle case where there are no chat entries
          }
        }

        override fun onCancelled(databaseError: DatabaseError) {
          // Handle possible errors
          continuation.resumeWithException(databaseError.toException())
        }
      }
      latestChatQuery.addListenerForSingleValueEvent(listener)

      continuation.invokeOnCancellation {
        latestChatQuery.removeEventListener(listener)
      }
    }
  }
}
