package com.example.utility

sealed class Result<out T> {

  class Success<out T>(val data:T) : Result<T>()

  class Failure(val message:String):Result<Nothing>()
}
