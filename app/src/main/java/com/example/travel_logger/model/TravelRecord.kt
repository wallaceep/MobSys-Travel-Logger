package com.example.travel_logger.model

import java.io.Serializable

data class TravelRecord(
    val id: String,
    val locationName: String,
    val photoUri: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val rating: Float = 0.0f,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
