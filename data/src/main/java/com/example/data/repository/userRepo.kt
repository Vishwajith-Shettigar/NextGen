package com.example.data.repository

import android.provider.ContactsContract
import com.example.data.dao.UserDao
import com.example.data.entity.UserEntity
import com.example.model.Chat
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
      bio = user.bio
    )
    userDao.insertUser(userEntity)
  }

  suspend fun getUser(username: String): Profile? {
    val userEntity = userDao.getUser(username)
    return userEntity?.let {
      Profile.newBuilder().apply {
        this.userId = it.userId
        this.firstName = it.name
        this.imageUrl = it.imageUrl
        this.bio = it.bio
        this.userName = it.userName
      }.build()
    }
  }
}