package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLocationDao {
    @Query("SELECT * FROM saved_locations ORDER BY name ASC")
    fun getAllLocations(): Flow<List<SavedLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: SavedLocation)

    @Query("DELETE FROM saved_locations WHERE id = :id")
    suspend fun deleteLocationById(id: String)
}

@Dao
interface SavedJourneyDao {
    @Query("SELECT * FROM saved_journeys ORDER BY id DESC")
    fun getAllJourneys(): Flow<List<SavedJourney>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJourney(journey: SavedJourney)

    @Query("DELETE FROM saved_journeys WHERE id = :id")
    suspend fun deleteJourneyById(id: String)

    @Query("DELETE FROM saved_journeys WHERE fromLocation = :fromLoc AND toLocation = :toLoc")
    suspend fun deleteJourneyByRoute(fromLoc: String, toLoc: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_journeys WHERE fromLocation = :fromLoc AND toLocation = :toLoc)")
    fun isJourneySaved(fromLoc: String, toLoc: String): Flow<Boolean>
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles LIMIT 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Update
    suspend fun updateProfile(profile: UserProfile)
}

@Dao
interface OfflineBookingDao {
    @Query("SELECT * FROM offline_bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<OfflineBooking>>

    @Query("SELECT * FROM offline_bookings WHERE id = :id LIMIT 1")
    suspend fun getBookingById(id: String): OfflineBooking?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: OfflineBooking)

    @Query("UPDATE offline_bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: String, status: String)
}
