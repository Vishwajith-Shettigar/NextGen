package com.example.utility

import android.content.Intent
import android.os.Bundle
import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import com.google.protobuf.MessageLite

fun <T : MessageLite> Bundle.putProto(key: String, message: T) {
  putSerializable(key, message.toByteString())
}

fun <T : MessageLite> Bundle.getProto(name: String, defaultValue: T): T {
  val serializedByteString = getSerializable(name) as? ByteString
  return serializedByteString?.let {
    return@let try {
      @Suppress("UNCHECKED_CAST")
      defaultValue.newBuilderForType().mergeFrom(it).build() as T
    } catch (e: InvalidProtocolBufferException) {
      null
    }
  } ?: defaultValue
}

fun <T : MessageLite> Intent.putProtoExtra(key: String, message: T) {
  val extrasBundle = extras ?: Bundle()
  extrasBundle.putProto(key, message)
  replaceExtras(extrasBundle)
}

fun <T : MessageLite> Intent.getProtoExtra(name: String, default: T): T? {
  val bundle = extras ?: return default
  return bundle.getProto(name, default)
}