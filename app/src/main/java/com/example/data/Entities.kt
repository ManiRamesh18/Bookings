package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "saved_locations")
data class SavedLocation(
    @PrimaryKey val id: String,
    val name: String, // Home, Office, Work etc.
    val address: String,
    val latitude: Double,
    val longitude: Double
)

@Entity(tableName = "saved_journeys")
data class SavedJourney(
    @PrimaryKey val id: String,
    val fromLocation: String,
    val toLocation: String,
    val notifyOnPriceDrop: Boolean = true,
    val dateText: String = "",
    val vehicleType: String = "BOTH"
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val referralCode: String,
    val earnedCredits: Double = 0.0,
    val preferredClass: String = "AC",
    val notificationsEnabled: Boolean = true
)

@Entity(tableName = "offline_bookings")
data class OfflineBooking(
    @PrimaryKey val id: String, // e.g. BK123456
    val optionId: String,
    val provider: String, // IRCTC, REDBUS, MAKEMYTRIP
    val vehicleType: String, // BUS, TRAIN
    val operatorName: String,
    val serviceNumber: String,
    val fromLocation: String,
    val toLocation: String,
    val departureTime: String,
    val arrivalTime: String,
    val selectedSeats: List<Int>,
    val amount: Double,
    val status: String, // CONFIRMED, CANCELLED, REFUNDED
    val travelerNames: String, // CSV names
    val qrcode: String // Mock barcode text
)

class DatabaseConverters {
    @TypeConverter
    fun fromIntList(list: List<Int>?): String {
        return list?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toIntList(csv: String?): List<Int> {
        if (csv.isNullOrEmpty()) return emptyList()
        return csv.split(",").mapNotNull { it.toIntOrNull() }
    }
}
