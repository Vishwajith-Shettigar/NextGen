package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.UserEntity

@Dao
interface UserDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Query("SELECT * FROM user WHERE userId = :userId LIMIT 1")
  suspend fun getUser(userId: String): UserEntity?

  @Query("UPDATE user SET disableChat = :disableChatStatus WHERE userId = :userId")
  suspend fun updateDisableChatStatus(userId: String, disableChatStatus: Boolean): Int

  @Query("UPDATE user SET disableLocation = :disableLocationStatus WHERE userId = :userId")
  suspend fun updatedisableLocationStatus(userId: String, disableLocationStatus: Boolean): Int

  @Query("UPDATE user SET disableProfilePicture = :disableProfilePictureStatus WHERE userId = :userId")
  suspend fun updatedisableProfilePicture(userId: String, disableProfilePictureStatus: Boolean): Int
}
