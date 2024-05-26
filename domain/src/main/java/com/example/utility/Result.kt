package com.example.utility

sealed class Result {

  class Success : Result()

  class Failure(val message:String):Result()
}
