package com.example.data

import java.io.Serializable

enum class ProviderType {
    IRCTC, REDBUS, MAKEMYTRIP
}

enum class VehicleType {
    BUS, TRAIN
}

data class BookingOption(
    val id: String,
    val provider: ProviderType,
    val vehicleType: VehicleType,
    val operatorName: String,
    val serviceNumber: String, // Train number or bus layout description
    val departureTime: String,
    val arrivalTime: String,
    val durationText: String,
    val price: Double,
    val strikePrice: Double? = null, // for estimated savings badge
    val rating: Float,
    val reviewCount: Int,
    val seatsAvailable: Int,
    val totalSeats: Int = 40,
    val classCategory: String, // Budget/AC/Premium
    val amenities: List<String>, // e.g. ["WiFi", "Charging", "Toilet", "Water", "AC", "Blanket"]
    val routeStops: List<String> = emptyList()
) : Serializable

data class ReviewModel(
    val id: String,
    val author: String,
    val rating: Float,
    val dateText: String,
    val reviewText: String,
    val componentRatings: Map<String, Float> = emptyMap(),
    val helpfulCount: Int = 0,
    var userLiked: Boolean = false
) : Serializable

data class StopModel(
    val name: String,
    val scheduledTime: String,
    val actualTime: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
) : Serializable

data class DriverModel(
    val name: String,
    val rating: Float,
    val experienceYears: Int,
    val phone: String,
    val photoUrl: String = ""
) : Serializable

data class TrackingState(
    val bookingId: String,
    val originalLatitude: Double,
    val originalLongitude: Double,
    val currentLatitude: Double,
    val currentLongitude: Double,
    val distanceCoveredKm: Int,
    val totalDistanceKm: Int,
    val currentSpeedKmh: Int,
    val currentStop: String,
    val nextStop: String,
    val nextStopETA: String,
    val driver: DriverModel,
    val timeline: List<StopModel>
) : Serializable
