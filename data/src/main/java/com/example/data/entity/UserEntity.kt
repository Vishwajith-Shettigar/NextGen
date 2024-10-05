package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
data class UserEntity(
  @PrimaryKey val userId: String,
  val userName:String,
  val imageUrl: String,
  val firstName: String,
  val lastName:String,
  val bio: String,
  val rating:Float,
  val disableProfilePicture:Boolean,
  val disableLocation:Boolean,
  val disableChat:Boolean
)