package com.example.ui

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class AuthStep {
    object EnterPhone : AuthStep()
    object EnterOtp : AuthStep()
    object SignUpDetails : AuthStep()
    object Authenticated : AuthStep()
}

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)

    // --- Authentication States ---
    private val _authStep = MutableStateFlow<AuthStep>(AuthStep.EnterPhone)
    val authStep: StateFlow<AuthStep> = _authStep.asStateFlow()

    val userPhone = MutableStateFlow("")
    val userOtp = MutableStateFlow("")
    val signUpName = MutableStateFlow("")
    val signUpEmail = MutableStateFlow("")

    val otpTimerText = MutableStateFlow("30s")
    val isOtpCanResend = MutableStateFlow(false)
    val authError = MutableStateFlow("")
    val authLoading = MutableStateFlow(false)

    private var otpTimer: CountDownTimer? = null

    // --- Logged-In User Profile ---
    val profile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- Saved Locations & Journeys ---
    val savedLocations: StateFlow<List<SavedLocation>> = repository.savedLocations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedJourneys: StateFlow<List<SavedJourney>> = repository.savedJourneys
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Search Query Panel States ---
    val searchFrom = MutableStateFlow("Delhi (NDLS)")
    val searchTo = MutableStateFlow("Agra Cantt (AGC)")
    val searchDate = MutableStateFlow("2026-06-15")
    val searchTravelersCount = MutableStateFlow(1)
    val searchPrefClass = MutableStateFlow("AC Class")
    val isRoundTrip = MutableStateFlow(false)
    val filterType = MutableStateFlow("BOTH") // BUS, TRAIN, BOTH

    // --- Search Results & Sorting/Filtering ---
    private val _searchResults = MutableStateFlow<List<BookingOption>>(emptyList())
    val searchResults: StateFlow<List<BookingOption>> = _searchResults.asStateFlow()

    val searchResultsLoading = MutableStateFlow(false)
    val searchResultsError = MutableStateFlow("")

    val filterProvider = MutableStateFlow<Set<ProviderType>>(emptySet())
    val filterAmenities = MutableStateFlow<Set<String>>(emptySet())
    val sortOption = MutableStateFlow("PRICE_ASC") // PRICE_ASC, DURATION_ASC, RATING_DESC
    val maxPriceSlider = MutableStateFlow(2500.0)

    private val routeQueryFlow = combine(searchFrom, searchTo) { from, to -> Pair(from, to) }
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentIsSavedRoute: StateFlow<Boolean> = routeQueryFlow
        .flatMapLatest { (from, to) -> repository.isJourneySaved(from, to) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // --- Active Booking Details Selection States ---
    val selectedOption = MutableStateFlow<BookingOption?>(null)
    val selectedSeats = MutableStateFlow<List<Int>>(emptyList())
    val detailReviews = MutableStateFlow<List<ReviewModel>>(emptyList())
    val isDetailsLoading = MutableStateFlow(false)

    // --- Active Reservation Checkouts ---
    val currentBookingDetails = MutableStateFlow<OfflineBooking?>(null)
    val secureHoldTimerText = MutableStateFlow("10:00")
    private var holdTimer: CountDownTimer? = null

    // Payment Simulation
    val paymentMtd = MutableStateFlow("UPI") // UPI, CARD, WALLET, NET_BANKING
    val payUpiId = MutableStateFlow("")
    val payCardNo = MutableStateFlow("")
    val payCardExpiry = MutableStateFlow("")
    val payCardCvv = MutableStateFlow("")
    val paymentLoading = MutableStateFlow(false)
    val paymentSuccessBooking = MutableStateFlow<OfflineBooking?>(null)

    // --- Live Tracking States ---
    val trackingState = MutableStateFlow<TrackingState?>(null)
    private var trackingJob: Job? = null
    val isSosTriggered = MutableStateFlow(false)

    // --- My Bookings List ---
    val myBookings: StateFlow<List<OfflineBooking>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Referral, Subscription & Active promo codes ---
    val referralCreditsUsed = MutableStateFlow(0.0)
    val selectedPromoDiscount = MutableStateFlow(0.0) // INR discount code
    val activePromoCode = MutableStateFlow("")
    val activePassPlan = MutableStateFlow<String?>(null) // Traveller, Commuter, Business

    // Feedback rating flows
    val reviewRatingOverall = MutableStateFlow(5f)
    val reviewFeedbackText = MutableStateFlow("")
    val reviewStatusMsg = MutableStateFlow("")

    init {
        viewModelScope.launch {
            repository.ensureProfileCreated()
            // Preset 2 saved addresses to make the app delightful out-of-the-box
            if (repository.locationDao.getAllLocations().first().isEmpty()) {
                repository.saveLocation("Home Base", "D-12, Green Park Avenue, New Delhi", 28.5582, 77.2058)
                repository.saveLocation("Zoho HQ Office", "Estancia IT Park, Chennai, Tamil Nadu", 12.8229, 80.0436)
            }
        }
    }

    // --- Auth UI functions ---
    fun sendOtp() {
        if (userPhone.value.trim().length < 10) {
            authError.value = "Enter a valid 10-digit mobile number."
            return
        }
        authError.value = ""
        authLoading.value = true

        viewModelScope.launch {
            delay(1200) // Simulating OTP transmission
            authLoading.value = false
            _authStep.value = AuthStep.EnterOtp
            startOtpTimer()
        }
    }

    fun verifyOtp() {
        if (userOtp.value.trim().length != 6) {
            authError.value = "Enter the 6-digit OTP passcode."
            return
        }
        // Easy testing password/OTP configuration: accepts 123456 or any 6 digits for simple verification!
        authError.value = ""
        authLoading.value = true

        viewModelScope.launch {
            delay(1200)
            authLoading.value = false
            val isNew = profile.value == null
            if (isNew) {
                _authStep.value = AuthStep.SignUpDetails
            } else {
                _authStep.value = AuthStep.Authenticated
            }
        }
    }

    fun submitSignUp() {
        if (signUpName.value.trim().isEmpty()) {
            authError.value = "Please input your full name."
            return
        }
        authError.value = ""
        authLoading.value = true

        viewModelScope.launch {
            repository.updateProfile(
                name = signUpName.value,
                email = signUpEmail.value,
                phone = userPhone.value,
                preferredClass = "AC Class"
            )
            authLoading.value = false
            _authStep.value = AuthStep.Authenticated
        }
    }

    fun resendOtpCode() {
        if (!isOtpCanResend.value) return
        startOtpTimer()
        // Simulate OTP alert SMS trigger
    }

    private fun startOtpTimer() {
        isOtpCanResend.value = false
        otpTimer?.cancel()
        otpTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                otpTimerText.value = "${millisUntilFinished / 1000}s"
            }
            override fun onFinish() {
                otpTimerText.value = "Resend"
                isOtpCanResend.value = true
            }
        }.start()
    }

    fun logout() {
        _authStep.value = AuthStep.EnterPhone
        userPhone.value = ""
        userOtp.value = ""
        signUpName.value = ""
        signUpEmail.value = ""
    }

    // --- Search logic ---
    fun fetchComparisonTickets() {
        searchResultsError.value = ""
        searchResultsLoading.value = true
        viewModelScope.launch {
            try {
                val r = repository.getComparisonResults(
                    fromLoc = searchFrom.value,
                    toLoc = searchTo.value,
                    date = searchDate.value
                )
                _searchResults.value = r
                searchResultsLoading.value = false
            } catch (e: Exception) {
                searchResultsError.value = e.message ?: "Failed aggregating travel quotes"
                searchResultsLoading.value = false
            }
        }
    }

    data class FilterState(
        val providers: Set<ProviderType>,
        val amenities: Set<String>,
        val maxPrice: Double,
        val fType: String
    )

    private val filterStateFlow: Flow<FilterState> = combine(
        filterProvider,
        filterAmenities,
        maxPriceSlider,
        filterType
    ) { providers, amenities, maxPrice, fType ->
        FilterState(providers, amenities, maxPrice, fType)
    }

    // Combined filtered output
    val filteredResults: StateFlow<List<BookingOption>> = combine(
        _searchResults,
        sortOption,
        filterStateFlow
    ) { results, sort, filters ->
        var temp = results

        // 1. Filter by vehicle Type (BUS / TRAIN)
        if (filters.fType != "BOTH") {
            temp = temp.filter { it.vehicleType.name == filters.fType }
        }

        // 2. Filter by Providers (IRCTC, etc.)
        if (filters.providers.isNotEmpty()) {
            temp = temp.filter { filters.providers.contains(it.provider) }
        }

        // 3. Filter by price cap
        temp = temp.filter { it.price <= filters.maxPrice }

        // 4. Filter by amenities
        if (filters.amenities.isNotEmpty()) {
            temp = temp.filter { opt -> opt.amenities.containsAll(filters.amenities) }
        }

        // 5. Apply sorting
        when (sort) {
            "PRICE_ASC" -> temp.sortedBy { it.price }
            "DURATION_ASC" -> temp.sortedBy { opt ->
                val h = opt.durationText.substringBefore("h").trim().toIntOrNull() ?: 24
                h
            }
            "RATING_DESC" -> temp.sortedByDescending { it.rating }
            else -> temp
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Toggle Wishlist
    fun toggleFavoriteRoute() {
        viewModelScope.launch {
            if (currentIsSavedRoute.value) {
                repository.removeJourneyByRoute(searchFrom.value, searchTo.value)
            } else {
                repository.toggleJourneySaved(searchFrom.value, searchTo.value, filterType.value)
            }
        }
    }

    // --- Details and Reviews Selection ---
    fun loadSelectedOptionDetails(option: BookingOption) {
        selectedOption.value = option
        selectedSeats.value = emptyList()
        isDetailsLoading.value = true
        detailReviews.value = emptyList()

        viewModelScope.launch {
            val rev = repository.getReviewsForOption(option.id)
            detailReviews.value = rev
            isDetailsLoading.value = false
        }
    }

    fun selectSeatIndex(seat: Int) {
        val curr = selectedSeats.value.toMutableList()
        if (curr.contains(seat)) {
            curr.remove(seat)
        } else {
            // Support travelers maximum constraint matching booking rules (max 6)
            if (curr.size >= searchTravelersCount.value) {
                curr.removeAt(0) // replace oldest
            }
            curr.add(seat)
        }
        selectedSeats.value = curr
    }

    // --- Booking Initiations and Secure Holds ---
    fun initiateBookingPaymentHold() {
        val opt = selectedOption.value ?: return
        val seats = selectedSeats.value.ifEmpty { listOf((1..40).random()) }

        val travelers = (1..seats.size).map { "Traveler $it" }
        val fare = opt.price * seats.size

        // Adjust for referral savings in price calculations
        val discount = selectedPromoDiscount.value + referralCreditsUsed.value
        val finalTotal = maxOf(0.0, fare - discount)

        viewModelScope.launch {
            val booking = repository.createBooking(
                option = opt,
                fromLoc = searchFrom.value,
                toLoc = searchTo.value,
                selectedSeats = seats,
                amount = finalTotal,
                travelerNames = travelers
            )
            currentBookingDetails.value = booking
            startPaymentHoldTimer()
        }
    }

    private fun startPaymentHoldTimer() {
        holdTimer?.cancel()
        holdTimer = object : CountDownTimer(600000, 1000) { // 10 minutes hold
            override fun onTick(millisInFuture: Long) {
                val mins = (millisInFuture / 60000)
                val secs = (millisInFuture % 60000 / 1000)
                secureHoldTimerText.value = String.format("%02d:%02d", mins, secs)
            }
            override fun onFinish() {
                secureHoldTimerText.value = "Hold Expired"
                currentBookingDetails.value = null
            }
        }.start()
    }

    // Handle Promo Redemptions
    fun validatePromoCode() {
        if (activePromoCode.value.trim().uppercase() == "XCHANGE200") {
            selectedPromoDiscount.value = 200.0
        } else {
            selectedPromoDiscount.value = 0.0
        }
    }

    // Toggle referral credits usage
    fun toggleUseCredits(enabled: Boolean) {
        val credits = profile.value?.earnedCredits ?: 0.0
        if (enabled) {
            referralCreditsUsed.value = minOf(150.0, credits)
        } else {
            referralCreditsUsed.value = 0.0
        }
    }

    // --- Confirm Booking / Pay simulation ---
    fun processCheckoutPayment(onComplete: () -> Unit) {
        val booking = currentBookingDetails.value ?: return
        paymentLoading.value = true

        viewModelScope.launch {
            delay(1500) // Simulating direct connection to banking networks or Razorpay
            paymentLoading.value = false

            // Deduct credits if applied
            if (referralCreditsUsed.value > 0.0) {
                repository.applyCredits(referralCreditsUsed.value)
                referralCreditsUsed.value = 0.0
            }

            // Successfully processed
            paymentSuccessBooking.value = booking
            onComplete()
        }
    }

    // --- Live Location Tracker ---
    fun startTrackingJourney(bookingId: String, operatorName: String) {
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            repository.streamLiveTracking(bookingId, operatorName).collect { state ->
                trackingState.value = state
            }
        }
    }

    fun stopTrackingJourney() {
        trackingJob?.cancel()
        trackingState.value = null
    }

    fun triggerSOSAlert() {
        isSosTriggered.value = true
    }

    // --- Feedback ---
    fun submitFeedback(bookingId: String) {
        if (reviewFeedbackText.value.trim().isEmpty()) {
            reviewStatusMsg.value = "Please describe details about your ride safety or comfort."
            return
        }

        viewModelScope.launch {
            reviewStatusMsg.value = "Submitting secure feedback..."
            delay(1000)
            reviewStatusMsg.value = "Thank you! Gained ₹25 XChange ride rewards."
            repository.addCredits(25.0)
            reviewFeedbackText.value = ""
        }
    }

    // --- Saved Locations Custom Add Dialog ---
    fun addSavedAddress(name: String, address: String) {
        viewModelScope.launch {
            repository.saveLocation(name, address, 13.0, 80.0)
        }
    }

    // --- Subscriptions Passes ---
    fun purchaseSubscriptionPass(plan: String) {
        viewModelScope.launch {
            activePassPlan.value = plan
            repository.addCredits(50.0) // Give loyalty credit bonus on passes
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            repository.cancelBooking(bookingId)
        }
    }

    fun updateProfile(name: String, email: String, phone: String, preferredClass: String) {
        viewModelScope.launch {
            repository.updateProfile(name, email, phone, preferredClass)
        }
    }

    suspend fun askGeminiTravelAssistant(
        fromLoc: String,
        toLoc: String,
        options: List<BookingOption>,
        userQuestion: String
    ): String {
        return repository.askGeminiTravelAssistant(fromLoc, toLoc, options, userQuestion)
    }

    override fun onCleared() {
        super.onCleared()
        otpTimer?.cancel()
        holdTimer?.cancel()
        trackingJob?.cancel()
    }
}
