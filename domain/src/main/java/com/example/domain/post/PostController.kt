package com.example.domain.post

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostController @Inject constructor(
  val fireBaseFireStore:FirebaseFirestore
) {



}