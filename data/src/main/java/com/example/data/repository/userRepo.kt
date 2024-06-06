package com.example.data.repository

import android.speech.RecognizerResultsIntent
import android.util.Log
import com.example.data.dao.UserDao
import com.example.data.entity.UserEntity
import com.example.model.Privacy
import com.example.model.Profile

class UserRepo(private val userDao: UserDao) {

  suspend fun insertUser(
    user:
    Profile
  ) {
    try {
      val userEntity = UserEntity(
        userId = user.userId,
        userName = user.userName,
        imageUrl = user.imageUrl,
        firstName = user.firstName,
        lastName = user.lastName,
        bio = user.bio,
        rating = user.rating,
        disableProfilePicture = user.privacy.disableProfilePicture,
        disableLocation = user.privacy.disableLocation,
        disableChat = user.privacy.disableChat
      )
      userDao.insertUser(userEntity)
    } catch (e: java.lang.Exception) {
    }
  }

  suspend fun getUser(userId: String): Profile? {
    val userEntity = userDao.getUser(userId)
    return userEntity?.let {

      val privacy = Privacy.newBuilder().apply {
        this.disableProfilePicture = it.disableProfilePicture
        this.disableLocation = it.disableLocation
        this.disableChat = it.disableChat
      }.build()

      Profile.newBuilder().apply {
        this.userId = it.userId
        this.firstName = it.firstName
        this.lastName = it.lastName
        this.imageUrl = it.imageUrl
        this.bio = it.bio
        this.rating = it.rating
        this.userName = it.userName
        this.privacy = privacy
      }.build()
    }
  }
}
