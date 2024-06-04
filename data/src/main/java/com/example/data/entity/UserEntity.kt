package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
data class UserEntity(
  @PrimaryKey val userId: String,
  val userName:String,
  val imageUrl: String,
  val name: String,
  val bio: String
)