package com.example.utility

import com.google.firebase.firestore.GeoPoint;
import java.lang.Math.*


object GeoUtils {
  private const val EARTH_RADIUS = 6371000.0 // meters

  // Calculate the distance between two GeoPoints
  fun calculateDistance(point1: GeoPoint, point2: GeoPoint): Double {
    val lat1 = point1.latitude
    val lon1 = point1.longitude
    val lat2 = point2.latitude
    val lon2 = point2.longitude

    val dLat = (lat2 - lat1).toRadians()
    val dLon = (lon2 - lon1).toRadians()

    val a = sin(dLat / 2) * sin(dLat / 2) +
      cos(lat1.toRadians()) * cos(lat2.toRadians()) *
      sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return EARTH_RADIUS * c
  }

  // Extension function to convert degrees to radians
  private fun Double.toRadians() = this * Math.PI / 180
}