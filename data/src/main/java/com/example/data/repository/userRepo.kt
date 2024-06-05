package com.example.data.repository

import com.example.data.dao.UserDao
import com.example.data.entity.UserEntity
import com.example.model.Privacy
import com.example.model.Profile

class UserRepo(private val userDao: UserDao) {

  suspend fun insertUser(
    user:
    Profile,
  ) {
    val userEntity = UserEntity(
      userId = user.userId,
      userName = user.userName,
      imageUrl = user.imageUrl,
      name = user.firstName,
      bio = user.bio,
      disableProfilePicture = user.privacy.disableProfilePicture,
      disableLocation = user.privacy.disableLocation,
      disableChat = user.privacy.disableChat
    )
    userDao.insertUser(userEntity)
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
        this.firstName = it.name
        this.imageUrl = it.imageUrl
        this.bio = it.bio
        this.userName = it.userName
        this.privacy = privacy
      }.build()
    }
  }
}
