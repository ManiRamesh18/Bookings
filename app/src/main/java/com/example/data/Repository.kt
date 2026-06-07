package com.example.data

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AppRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    val locationDao = db.savedLocationDao()
    val journeyDao = db.savedJourneyDao()
    val profileDao = db.userProfileDao()
    val bookingDao = db.offlineBookingDao()

    // --- Saved Locations ---
    val savedLocations: Flow<List<SavedLocation>> = locationDao.getAllLocations()

    suspend fun saveLocation(name: String, address: String, lat: Double, lng: Double) = withContext(Dispatchers.IO) {
        val loc = SavedLocation(
            id = UUID.randomUUID().toString(),
            name = name,
            address = address,
            latitude = lat,
            longitude = lng
        )
        locationDao.insertLocation(loc)
    }

    suspend fun deleteLocation(id: String) = withContext(Dispatchers.IO) {
        locationDao.deleteLocationById(id)
    }

    // --- Saved Journeys (Wishlist / Price Drop Trackers) ---
    val savedJourneys: Flow<List<SavedJourney>> = journeyDao.getAllJourneys()

    fun isJourneySaved(fromLoc: String, toLoc: String): Flow<Boolean> {
        return journeyDao.isJourneySaved(fromLoc, toLoc)
    }

    suspend fun toggleJourneySaved(fromLoc: String, toLoc: String, vehicleType: String) = withContext(Dispatchers.IO) {
        // Query if exists
        val id = "J_${fromLoc}_${toLoc}".replace(" ", "_")
        val journey = SavedJourney(
            id = id,
            fromLocation = fromLoc,
            toLocation = toLoc,
            vehicleType = vehicleType
        )
        // We'll write a simple check or overwrite
        journeyDao.insertJourney(journey)
    }

    suspend fun removeJourneyByRoute(fromLoc: String, toLoc: String) = withContext(Dispatchers.IO) {
        journeyDao.deleteJourneyByRoute(fromLoc, toLoc)
    }

    // --- User Profile ---
    val userProfile: Flow<UserProfile?> = profileDao.getUserProfileFlow()

    suspend fun ensureProfileCreated() = withContext(Dispatchers.IO) {
        val existing = profileDao.getUserProfile()
        if (existing == null) {
            val defaultProfile = UserProfile(
                id = "USER_DEFAULT_01",
                name = "Amit Ramesh",
                phone = "+91 98765 04321",
                email = "mani.ramesh@zohocorp.com",
                referralCode = "XCHG500R",
                earnedCredits = 150.0,
                preferredClass = "AC Class",
                notificationsEnabled = true
            )
            profileDao.insertProfile(defaultProfile)
        }
    }

    suspend fun updateProfile(name: String, email: String, phone: String, preferredClass: String) = withContext(Dispatchers.IO) {
        val existing = profileDao.getUserProfile() ?: UserProfile(
            id = "USER_DEFAULT_01",
            name = name,
            phone = phone,
            email = email,
            referralCode = "XCHG500R",
            earnedCredits = 150.0,
            preferredClass = preferredClass
        )
        val updated = existing.copy(
            name = name,
            email = email,
            phone = phone,
            preferredClass = preferredClass
        )
        profileDao.insertProfile(updated)
    }

    suspend fun applyCredits(amountToDeduct: Double) = withContext(Dispatchers.IO) {
        val existing = profileDao.getUserProfile()
        if (existing != null) {
            val remain = maxOf(0.0, existing.earnedCredits - amountToDeduct)
            profileDao.insertProfile(existing.copy(earnedCredits = remain))
        }
    }

    suspend fun addCredits(amountToAdd: Double) = withContext(Dispatchers.IO) {
        val existing = profileDao.getUserProfile()
        if (existing != null) {
            profileDao.insertProfile(existing.copy(earnedCredits = existing.earnedCredits + amountToAdd))
        }
    }

    // --- Bookings History ---
    val allBookings: Flow<List<OfflineBooking>> = bookingDao.getAllBookings()

    suspend fun createBooking(
        option: BookingOption,
        fromLoc: String,
        toLoc: String,
        selectedSeats: List<Int>,
        amount: Double,
        travelerNames: List<String>
    ): OfflineBooking = withContext(Dispatchers.IO) {
        val bookingId = "BK" + System.currentTimeMillis().toString().takeLast(6) + (10..99).random().toString()
        val booking = OfflineBooking(
            id = bookingId,
            optionId = option.id,
            provider = option.provider.name,
            vehicleType = option.vehicleType.name,
            operatorName = option.operatorName,
            serviceNumber = option.serviceNumber,
            fromLocation = fromLoc,
            toLocation = toLoc,
            departureTime = option.departureTime,
            arrivalTime = option.arrivalTime,
            selectedSeats = selectedSeats,
            amount = amount,
            status = "CONFIRMED",
            travelerNames = travelerNames.joinToString(","),
            qrcode = "CONFIRMED-$bookingId-SECURE"
        )
        bookingDao.insertBooking(booking)
        booking
    }

    suspend fun cancelBooking(bookingId: String) = withContext(Dispatchers.IO) {
        bookingDao.updateBookingStatus(bookingId, "CANCELLED")
    }

    // --- Search & Aggregates from Providers ---
    suspend fun getComparisonResults(
        fromLoc: String,
        toLoc: String,
        date: String
    ): List<BookingOption> = withContext(Dispatchers.IO) {
        // Mock a brief network loading delay (e.g. 800ms) to simulate real-time API gathering from 3 providers
        delay(800)
        MockData.getMockBookingOptions(fromLoc, toLoc, date)
    }

    // --- Tracking Simulation ---
    fun streamLiveTracking(bookingId: String, operatorName: String): Flow<TrackingState> = flow {
        val baseState = MockData.getMockTrackingState(bookingId, operatorName)
        emit(baseState)

        var speed = baseState.currentSpeedKmh
        var covered = baseState.distanceCoveredKm
        var lat = baseState.currentLatitude
        var lng = baseState.currentLongitude

        // Stream updates every few seconds
        while (true) {
            delay(5000) // 5s rate updating
            speed = (70..88).random()
            covered += 1
            lat += 0.001
            lng += 0.001

            val updatedTimeline = baseState.timeline.mapIndexed { idx, stop ->
                if (idx == 0 || idx == 1) stop.copy(isCompleted = true, isCurrent = false)
                else if (idx == 2) stop.copy(isCompleted = true, isCurrent = true) // current
                else stop
            }

            emit(
                baseState.copy(
                    currentSpeedKmh = speed,
                    distanceCoveredKm = covered,
                    currentLatitude = lat,
                    currentLongitude = lng,
                    timeline = updatedTimeline
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    // --- Reviews ---
    suspend fun getReviewsForOption(optionId: String): List<ReviewModel> = withContext(Dispatchers.IO) {
        MockData.getMockReviews(optionId)
    }

    // --- Gemini AI Travel Assistant comparison helper ---
    suspend fun askGeminiTravelAssistant(
        fromLoc: String,
        toLoc: String,
        options: List<BookingOption>,
        userQuestion: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getLocalFallbackAdvice(fromLoc, toLoc, options, userQuestion)
        }

        // Build the transport details description context
        val optionsSummary = options.joinToString("\n") { opt ->
            "- [${opt.provider}] ${opt.operatorName} (${opt.vehicleType} | ${opt.classCategory}): ${opt.price} INR, Departs ${opt.departureTime}, duration ${opt.durationText}, Seats: ${opt.seatsAvailable}, Rating: ${opt.rating}★"
        }

        val prompt = """
            You are "XChange Intelligence" - the smart AI assistant for XChange transport comparison.
            The user wants to travel from "$fromLoc" to "$toLoc".
            
            Here are the current calculated travel options available across IRCTC (trains), RedBus (buses), and MakeMyTrip:
            $optionsSummary
            
            The user specifically asks: "$userQuestion"
            
            Offer a highly helpful, concise reply (max 3 short paragraphs). State clearly which option gives the best value, comfort level, speed, or potential savings (which provider is cheapest vs fastest). Be friendly and direct.
        """.trimIndent()

        try {
            val client = OkHttpClient()
            val mediaType = "application/json; charset=utf-8".toMediaType()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                }
                put("contents", contentsArray)
            }

            // Using modern priority gemini-3.5-flash as mandated in skill docs
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestJson.toString().toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("Repository", "Gemini HTTP failed: code=${response.code} body=${response.body?.string()}")
                    return@withContext getLocalFallbackAdvice(fromLoc, toLoc, options, userQuestion)
                }

                val bodyStr = response.body?.string() ?: ""
                val jsonObj = JSONObject(bodyStr)
                val candidates = jsonObj.getJSONArray("candidates")
                val text = candidates.getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                return@withContext text
            }
        } catch (e: Exception) {
            Log.e("Repository", "Gemini Exception", e)
            return@withContext getLocalFallbackAdvice(fromLoc, toLoc, options, userQuestion)
        }
    }

    private fun getLocalFallbackAdvice(
        fromLoc: String,
        toLoc: String,
        options: List<BookingOption>,
        userQuestion: String
    ): String {
        if (options.isEmpty()) {
            return "No active options are loaded for this route. Consider exploring another route like Delhi to Jaipur."
        }

        val cheapest = options.minByOrNull { it.price }
        val highestRated = options.maxByOrNull { opt -> opt.rating }
        val fastest = options.minByOrNull { opt ->
            val hours = opt.durationText.substringBefore("h").trim().toIntOrNull() ?: 24
            hours
        }

        return """
            🎯 **XChange Route Insights ($fromLoc ➔ $toLoc)**:
            
            • **Lowest Price**: ${cheapest?.operatorName} (${cheapest?.provider}) at just **₹${cheapest?.price}** is your most budget-friendly choice.
            • **Highest Comfort**: ${highestRated?.operatorName} rated **${highestRated?.rating}★** with premium amenities like ${highestRated?.amenities?.take(3)?.joinToString(", ")}.
            • **Speed Champion**: ${fastest?.operatorName} completes the travel in just **${fastest?.durationText}**.
            
            *Tip: Booking via XChange secures up to ₹150 in instant comparison credits check out our Saved Addresses for quick setup!*
        """.trimIndent()
    }
}
