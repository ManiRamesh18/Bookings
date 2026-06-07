package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Screen Enumeration representation
enum class AppScreen {
    AUTH, HOME, RESULTS, DETAILS, CONFIRMATION, PAYMENT, SUCCESS, TRACKING, PROFILE_SETTINGS, BOOKINGS, REFER_EARN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: AppViewModel) {
    var currentScreen by remember { mutableStateFlowOf(AppScreen.AUTH) }
    val scope = rememberCoroutineScope()

    // Observe auth status to route cleanly
    val authByVM by viewModel.authStep.collectAsState()
    LaunchedEffect(authByVM) {
        if (authByVM == AuthStep.Authenticated && currentScreen == AppScreen.AUTH) {
            currentScreen = AppScreen.HOME
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding(),
        bottomBar = {
            if (currentScreen != AppScreen.AUTH && currentScreen != AppScreen.PAYMENT && currentScreen != AppScreen.TRACKING) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { currentScreen = AppScreen.HOME },
                        icon = { Icon(Icons.Default.Search, "Search") },
                        label = { Text("Search", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.BOOKINGS,
                        onClick = { currentScreen = AppScreen.BOOKINGS },
                        icon = { Icon(Icons.Default.ConfirmationNumber, "My Bookings") },
                        label = { Text("Bookings", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.REFER_EARN,
                        onClick = { currentScreen = AppScreen.REFER_EARN },
                        icon = { Icon(Icons.Default.CardGiftcard, "Offers & Passes") },
                        label = { Text("Club Pass", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PROFILE_SETTINGS,
                        onClick = { currentScreen = AppScreen.PROFILE_SETTINGS },
                        icon = { Icon(Icons.Default.Person, "Profile") },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    AppScreen.AUTH -> AuthScreen(viewModel)
                    AppScreen.HOME -> HomeScreen(viewModel, onSearchTriggered = {
                        viewModel.fetchComparisonTickets()
                        currentScreen = AppScreen.RESULTS
                    })
                    AppScreen.RESULTS -> ResultsScreen(
                        viewModel = viewModel,
                        onBackPressed = { currentScreen = AppScreen.HOME },
                        onOptionSelected = { option ->
                            viewModel.loadSelectedOptionDetails(option)
                            currentScreen = AppScreen.DETAILS
                        }
                    )
                    AppScreen.DETAILS -> DetailsScreen(
                        viewModel = viewModel,
                        onBackPressed = { currentScreen = AppScreen.RESULTS },
                        onProceedToBook = {
                            viewModel.initiateBookingPaymentHold()
                            currentScreen = AppScreen.CONFIRMATION
                        }
                    )
                    AppScreen.CONFIRMATION -> ConfirmationScreen(
                        viewModel = viewModel,
                        onBackPressed = { currentScreen = AppScreen.DETAILS },
                        onPayClicked = { currentScreen = AppScreen.PAYMENT }
                    )
                    AppScreen.PAYMENT -> PaymentScreen(
                        viewModel = viewModel,
                        onBackPressed = { currentScreen = AppScreen.CONFIRMATION },
                        onPaymentStatus = { success ->
                            if (success) {
                                currentScreen = AppScreen.SUCCESS
                            } else {
                                currentScreen = AppScreen.CONFIRMATION
                            }
                        }
                    )
                    AppScreen.SUCCESS -> SuccessScreen(
                        viewModel = viewModel,
                        onGoHome = { currentScreen = AppScreen.HOME },
                        onTrackClicked = { booking ->
                            viewModel.startTrackingJourney(booking.id, booking.operatorName)
                            currentScreen = AppScreen.TRACKING
                        }
                    )
                    AppScreen.TRACKING -> TrackingScreen(
                        viewModel = viewModel,
                        onBackPressed = {
                            viewModel.stopTrackingJourney()
                            currentScreen = AppScreen.BOOKINGS
                        }
                    )
                    AppScreen.PROFILE_SETTINGS -> ProfileSettingsScreen(viewModel, onBackToHome = { currentScreen = AppScreen.HOME })
                    AppScreen.BOOKINGS -> MyBookingsScreen(
                        viewModel = viewModel,
                        onTrackBooking = { booking ->
                            viewModel.startTrackingJourney(booking.id, booking.operatorName)
                            currentScreen = AppScreen.TRACKING
                        }
                    )
                    AppScreen.REFER_EARN -> ClubPassScreen(viewModel)
                }
            }
        }
    }
}

// --- helper flow wrapper ---
private fun <T> mutableStateFlowOf(value: T) = mutableStateOf(value)

// ==========================================
// 1. AUTH SCREEN
// ==========================================
@Composable
fun AuthScreen(viewModel: AppViewModel) {
    val step by viewModel.authStep.collectAsState()
    val phone by viewModel.userPhone.collectAsState()
    val otp by viewModel.userOtp.collectAsState()
    val name by viewModel.signUpName.collectAsState()
    val email by viewModel.signUpEmail.collectAsState()
    val error by viewModel.authError.collectAsState()
    val loading by viewModel.authLoading.collectAsState()
    val timerText by viewModel.otpTimerText.collectAsState()
    val canResend by viewModel.isOtpCanResend.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Unified logo display
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            CircleShape
                        )
                        .padding(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "XChange Bookings",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = "Compare & Book Seat Tickets Across Providers",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                when (step) {
                    is AuthStep.EnterPhone -> {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { viewModel.userPhone.value = it },
                            label = { Text("Mobile Number") },
                            placeholder = { Text("+91 98765-XXXXX") },
                            leadingIcon = { Icon(Icons.Default.Phone, null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.sendOtp() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !loading
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Request OTP Code", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    is AuthStep.EnterOtp -> {
                        Text(
                            text = "OTP Code sent to ${phone}. Enter 123456 to test instantly!",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        OutlinedTextField(
                            value = otp,
                            onValueChange = { viewModel.userOtp.value = it.take(6) },
                            label = { Text("Verification Code") },
                            placeholder = { Text("6-Digit OTP") },
                            leadingIcon = { Icon(Icons.Default.Security, null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Didn't receive code?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )

                            TextButton(
                                onClick = { viewModel.resendOtpCode() },
                                enabled = canResend
                            ) {
                                Text(
                                    text = "Resend ($timerText)",
                                    fontWeight = FontWeight.Bold,
                                    color = if (canResend) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.verifyOtp() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !loading
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Verify Passcode", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    is AuthStep.SignUpDetails -> {
                        Text(
                            text = "Create Your Account Profile",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 16.dp),
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.signUpName.value = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.signUpEmail.value = it },
                            label = { Text("E-mail Address (Optional)") },
                            leadingIcon = { Icon(Icons.Default.Email, null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.submitSignUp() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !loading
                        ) {
                            if (loading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Text("Complete Register & Enter", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}

// ==========================================
// 2. HOME SCREEN (SEARCH FORM)
// ==========================================
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onSearchTriggered: () -> Unit
) {
    val fromLoc by viewModel.searchFrom.collectAsState()
    val toLoc by viewModel.searchTo.collectAsState()
    val date by viewModel.searchDate.collectAsState()
    val travelers by viewModel.searchTravelersCount.collectAsState()
    val prefClass by viewModel.searchPrefClass.collectAsState()
    val isRound by viewModel.isRoundTrip.collectAsState()
    val fType by viewModel.filterType.collectAsState()

    val savedLocations by viewModel.savedLocations.collectAsState()
    val savedJourneys by viewModel.savedJourneys.collectAsState()
    val profileByVM by viewModel.profile.collectAsState()

    var showDatePickerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcome Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello, ${profileByVM?.name ?: "Passenger"} 👋",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Ready to discover travel choices?",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                )
            }

            // Wallet Quick Info
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "₹${profileByVM?.earnedCredits?.toInt() ?: 0} Club Credits",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SEARCH BOARD CARD
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Trip Category Toggles (Bus/Train Type Selector)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    listOf("BOTH" to "All Rides", "TRAIN" to "Trains Only", "BUS" to "Buses Only").forEach { (typeKey, label) ->
                        val selected = fType == typeKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { viewModel.filterType.value = typeKey }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Places inputs (From -> To)
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        OutlinedTextField(
                            value = fromLoc,
                            onValueChange = { viewModel.searchFrom.value = it },
                            label = { Text("From Base") },
                            leadingIcon = { Icon(Icons.Default.Place, null, tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = toLoc,
                            onValueChange = { viewModel.searchTo.value = it },
                            label = { Text("To Destination") },
                            leadingIcon = { Icon(Icons.Default.Place, null, tint = MaterialTheme.colorScheme.error) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Swap Locations Floating icon
                    IconButton(
                        onClick = {
                            val temp = fromLoc
                            viewModel.searchFrom.value = toLoc
                            viewModel.searchTo.value = temp
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp, top = 20.dp, bottom = 20.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap Locations",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Date & Round Trip
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { viewModel.searchDate.value = it },
                        label = { Text("Journey Date") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDatePickerDialog = true },
                        readOnly = true, // Force tap selection
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Round-trip Toggle
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isRound) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { viewModel.isRoundTrip.value = !isRound }
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isRound, onCheckedChange = { viewModel.isRoundTrip.value = it })
                            Text("Round Trip", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Traveler counts and class categorization preference
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Travelers selector card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Text("Passengers", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { if (travelers > 1) viewModel.searchTravelersCount.value = travelers - 1 },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.RemoveCircleOutline, null, modifier = Modifier.size(18.dp))
                            }
                            Text("$travelers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            IconButton(
                                onClick = { if (travelers < 6) viewModel.searchTravelersCount.value = travelers + 1 },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.AddCircleOutline, null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Preferences Dropdown Class type
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { /* Toggle dynamic class categories list */ }
                            .padding(8.dp)
                    ) {
                        Text("Preferred Class", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text(prefClass, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onSearchTriggered() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.DirectionsBus, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compare Ticket Options", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // QUICK BOOK FAV DETAILS
        Text("Saved Addresses", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .height(86.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(savedLocations.size) { index ->
                val loc = savedLocations[index]
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable {
                        // Dynamically populate search values
                        viewModel.searchFrom.value = loc.name
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (loc.name.contains("Home")) Icons.Default.Home else Icons.Default.Work,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(loc.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(loc.address, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SPECIAL AI ADVICE PREVIEW MODULE
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "AI",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("XChange AI Smart Advisor", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Did you know that trains from $fromLoc to $toLoc are on average 40% cheaper than buses for night travel? Toggle trains explicitly above to discover heavy value options.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    // Quick Simulated DatePicker
    if (showDatePickerDialog) {
        AlertDialog(
            onDismissRequest = { showDatePickerDialog = false },
            title = { Text("Select Date") },
            text = {
                Column {
                    listOf("2026-06-15" to "Mon Jun 15, 2026", "2026-06-16" to "Tue Jun 16, 2026", "2026-06-20" to "Sat Jun 20, 2026").forEach { (dateStr, dateLab) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.searchDate.value = dateStr
                                    showDatePickerDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(dateLab, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

// ==========================================
// 3. RESULTS SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    viewModel: AppViewModel,
    onBackPressed: () -> Unit,
    onOptionSelected: (BookingOption) -> Unit
) {
    val fromLoc by viewModel.searchFrom.collectAsState()
    val toLoc by viewModel.searchTo.collectAsState()
    val date by viewModel.searchDate.collectAsState()
    val list by viewModel.filteredResults.collectAsState()
    val loading by viewModel.searchResultsLoading.collectAsState()
    val error by viewModel.searchResultsError.collectAsState()
    val isSavedRoute by viewModel.currentIsSavedRoute.collectAsState()

    // Filters and sorting variables
    val sortOpt by viewModel.sortOption.collectAsState()
    val currentMaxPrice by viewModel.maxPriceSlider.collectAsState()
    val activeProviders by viewModel.filterProvider.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TOP CONTROL HEADER
        Surface(
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$fromLoc ➔ $toLoc",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$date | ${list.size} Choices found",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    // Saved heart/wishlist
                    IconButton(onClick = { viewModel.toggleFavoriteRoute() }) {
                        Icon(
                            imageVector = if (isSavedRoute) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save Route",
                            tint = if (isSavedRoute) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sort & Filters buttons bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Sorting toggle option scroll
                    Box(
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .clickable {
                                // Toggle sorting simple rotation
                                val nextSort = when (sortOpt) {
                                    "PRICE_ASC" -> "DURATION_ASC"
                                    "DURATION_ASC" -> "RATING_DESC"
                                    else -> "PRICE_ASC"
                                }
                                viewModel.sortOption.value = nextSort
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sort, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            val sortLab = when (sortOpt) {
                                "PRICE_ASC" -> "Price: Low to High"
                                "DURATION_ASC" -> "Duration: Shortest"
                                "RATING_DESC" -> "Rating: High to Low"
                                else -> "Sort"
                            }
                            Text(sortLab, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Filter dialog toggle
                    Box(
                        modifier = Modifier
                            .background(
                                if (activeProviders.isNotEmpty() || currentMaxPrice < 2500.0)
                                    MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .clickable { showFilterSheet = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FilterList, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Filters", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // AGGREGATED SAVINGS BANNER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                val cheapest = list.minByOrNull { it.price }
                val normalMMT = list.firstOrNull { it.provider == ProviderType.MAKEMYTRIP }?.price ?: 1200.0
                val savingDiff = maxOf(0.0, normalMMT - (cheapest?.price ?: 0.0)).toInt()

                Text(
                    text = if (savingDiff > 0)
                        "XChange aggregated choices save you up to ₹$savingDiff on this route!"
                    else "XChange filters guarantee the verified lowest prices across 3 portals.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // CARDS LIST / LAZY COLUMN
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Fetching rates from IRCTC, RedBus, MMT in parallel...", fontSize = 12.sp)
                }
            }
        } else if (list.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.DirectionsBus, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No tickets matching current filters", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = {
                        viewModel.filterProvider.value = emptySet()
                        viewModel.maxPriceSlider.value = 2500.0
                    }) {
                        Text("Reset Filter Constraints")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(list) { option ->
                    BookingCard(option = option, onClick = { onOptionSelected(option) })
                }
            }
        }
    }

    // Simulated Bottom Sheet Filter Dialog
    if (showFilterSheet) {
        AlertDialog(
            onDismissRequest = { showFilterSheet = false },
            title = { Text("Search Filters Setting", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Max Ticket Price (INR)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Slider(
                        value = currentMaxPrice.toFloat(),
                        onValueChange = { viewModel.maxPriceSlider.value = it.toDouble() },
                        valueRange = 500f..2500f,
                        steps = 4
                    )
                    Text("Selected: ₹${currentMaxPrice.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Divider()

                    Text("Transportation Systems", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProviderType.values().forEach { provider ->
                            val active = activeProviders.contains(provider)
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val newSet = activeProviders.toMutableSet()
                                        if (active) newSet.remove(provider) else newSet.add(provider)
                                        viewModel.filterProvider.value = newSet
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Box(
                                    modifier = Modifier.padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(provider.name, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showFilterSheet = false }) {
                    Text("Apply Constraints")
                }
            }
        )
    }
}

@Composable
fun BookingCard(option: BookingOption, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Provider and service tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Provider Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val badgeColor = when (option.provider) {
                        ProviderType.IRCTC -> Color(0xFFF97316) // Train orange
                        ProviderType.REDBUS -> Color(0xFFEF4444) // Bus red
                        ProviderType.MAKEMYTRIP -> Color(0xFF3B82F6) // Travel blue
                    }
                    Box(
                        modifier = Modifier
                            .background(badgeColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            option.provider.name,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (option.vehicleType == VehicleType.TRAIN) "Train ${option.serviceNumber}" else option.serviceNumber,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                // Premium Seat Tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        "${option.rating} (${option.reviewCount})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body Row: Stations and duration timeline
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(option.departureTime, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Departs", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }

                // Layout Timeline connector
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1.5f)
                ) {
                    Text(option.durationText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        val stroke = Stroke(width = 2f)
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.4f),
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 3f
                        )
                        drawCircle(
                            color = Color.Cyan,
                            radius = 6f,
                            center = Offset(0f, size.height / 2)
                        )
                        drawCircle(
                            color = Color.Magenta,
                            radius = 6f,
                            center = Offset(size.width, size.height / 2)
                        )
                    }
                    Text(
                        text = if (option.vehicleType == VehicleType.TRAIN) "Direct Train" else "Express Highway",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(option.arrivalTime, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Arrives", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Row: Pricing options & seat counters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = option.operatorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${option.seatsAvailable} Seats Left!",
                        fontSize = 11.sp,
                        color = if (option.seatsAvailable < 15) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    if (option.strikePrice != null) {
                        Text(
                            "₹${option.strikePrice.toInt()}",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            fontSize = 11.sp,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    }
                    Text(
                        "₹${option.price.toInt()}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. DETAILS SCREEN (SEAT & DETAILS)
// ==========================================
@Composable
fun DetailsScreen(
    viewModel: AppViewModel,
    onBackPressed: () -> Unit,
    onProceedToBook: () -> Unit
) {
    val option by viewModel.selectedOption.collectAsState()
    val selected by viewModel.selectedSeats.collectAsState()
    val reviews by viewModel.detailReviews.collectAsState()
    val reviewsLoading by viewModel.isDetailsLoading.collectAsState()
    val travelersCount by viewModel.searchTravelersCount.collectAsState()

    // AI questions flows
    var userAiQuestion by remember { mutableStateOf("") }
    var aiReplyText by remember { mutableStateOf("") }
    var aiLoadingState by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    if (option == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = onBackPressed) { Text("Back to aggregated options list") }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Option summary header
        Surface(tonalElevation = 6.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(option!!.operatorName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "${option!!.classCategory} | ${option!!.vehicleType.name}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // STEP CARD SELECTION
            Text("Select Seat Allocation Layout", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(
                "Please allocate. Need to configure up to $travelersCount seats matching travelers count.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            InteractiveSeatGrid(
                selectedSeats = selected,
                onSeatSelected = { seatNum -> viewModel.selectSeatIndex(seatNum) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Amenities List block
            Card(
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Included Amenities", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        option!!.amenities.forEach { amen ->
                            val icon = when (amen.lowercase()) {
                                "wifi" -> Icons.Default.Wifi
                                "charging" -> Icons.Default.Bolt
                                "toilet" -> Icons.Default.Wc
                                else -> Icons.Default.Weekend
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(amen, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SPECIAL INTELLIGENT AI SIDE PANELS DECK
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "AI Travel Route Adviser",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Ask the advisor regarding safety, typical delays, food preferences, or cheap tickets optimization on this specific ride.",
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = userAiQuestion,
                            onValueChange = { userAiQuestion = it },
                            placeholder = { Text("e.g. Is this train regularly on time?", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Button(
                            onClick = {
                                aiLoadingState = true
                                scope.launch {
                                    val reply = viewModel.askGeminiTravelAssistant(
                                        fromLoc = viewModel.searchFrom.value,
                                        toLoc = viewModel.searchTo.value,
                                        options = listOf(option!!),
                                        userQuestion = userAiQuestion
                                    )
                                    aiReplyText = reply
                                    aiLoadingState = false
                                }
                            },
                            enabled = !aiLoadingState && userAiQuestion.trim().isNotEmpty()
                        ) {
                            Text("Ask", fontSize = 11.sp)
                        }
                    }

                    if (aiLoadingState) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    }

                    if (aiReplyText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = aiReplyText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.background,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // REVIEWS SEGMENT
            Text("Traveler Reviews", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            reviews.forEach { rev ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(rev.author, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Row {
                                (1..5).forEach { star ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (star <= rev.rating) MaterialTheme.colorScheme.tertiary else Color.Gray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                        Text(rev.reviewText, fontSize = 11.sp, modifier = Modifier.padding(vertical = 4.dp))
                        Text(rev.dateText, fontSize = 9.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Proceed Sticky bar
            Button(
                onClick = onProceedToBook,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Reserve Chosen Seats (${selected.size.ifZero(1)} Tickets)",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun Int.ifZero(value: Int): Int = if (this == 0) value else this

@Composable
fun InteractiveSeatGrid(
    selectedSeats: List<Int>,
    onSeatSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Steering tag indicator for bus direction / locomotive engine reference
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.DirectionsBus, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Front Deck Locomotive Direction", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid structure (simulated standard row layout)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in 1..8) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Left seats pair
                        for (col in 1..2) {
                            val seatId = (row - 1) * 4 + col
                            val isSelected = selectedSeats.contains(seatId)
                            val isBooked = seatId in listOf(3, 8, 12, 19, 24)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isBooked -> Color.Gray.copy(alpha = 0.5f)
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .clickable(enabled = !isBooked) { onSeatSelected(seatId) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$seatId",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Isle gap spacer walk paths
                        Spacer(modifier = Modifier.width(20.dp))

                        // Right seats pair
                        for (col in 3..4) {
                            val seatId = (row - 1) * 4 + col
                            val isSelected = selectedSeats.contains(seatId)
                            val isBooked = seatId in listOf(5, 14, 21, 30)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isBooked -> Color.Gray.copy(alpha = 0.5f)
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .clickable(enabled = !isBooked) { onSeatSelected(seatId) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$seatId",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Available", fontSize = 10.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Occupied", fontSize = 10.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Selected", fontSize = 10.sp)
                }
            }
        }
    }
}

// ==========================================
// 5. CONFIRMATION SCREEN (HOLD TIMER RECONCILIATIONS)
// ==========================================
@Composable
fun ConfirmationScreen(
    viewModel: AppViewModel,
    onBackPressed: () -> Unit,
    onPayClicked: () -> Unit
) {
    val bookingDetails by viewModel.currentBookingDetails.collectAsState()
    val holdTimerSecs by viewModel.secureHoldTimerText.collectAsState()
    val promoDiscount by viewModel.selectedPromoDiscount.collectAsState()
    val useCredits by viewModel.referralCreditsUsed.collectAsState()
    val profileByVM by viewModel.profile.collectAsState()

    var promoInput by remember { mutableStateOf("") }

    if (bookingDetails == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                Text("Booking ticket hold period elapsed. Search again.")
                Button(onClick = onBackPressed) { Text("Search Again") }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Timer warn alert count banners
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.HourglassBottom, null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Seats reserved on 10-Min Hold Interval!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    "Pay within $holdTimerSecs seconds to lock this rate.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TRAVEL ITINERARY CARD
        Text("Itinerary Overview", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${bookingDetails!!.operatorName} (${bookingDetails!!.provider})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "${bookingDetails!!.fromLocation} to ${bookingDetails!!.toLocation}",
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Departure: ${bookingDetails!!.departureTime}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("Seats Allocated: ${bookingDetails!!.selectedSeats.joinToString(", ")}", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ADD-ON SPECIAL PROMO REDEMPTIONS
        Text("Offers & Promotion Code", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = promoInput,
                        onValueChange = { promoInput = it },
                        placeholder = { Text("Enter Coupon (XCHANGE200)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(onClick = {
                        viewModel.activePromoCode.value = promoInput
                        viewModel.validatePromoCode()
                    }) {
                        Text("Apply")
                    }
                }
                if (promoDiscount > 0.0) {
                    Text(
                        "Code success! Saved ₹${promoDiscount.toInt()} immediately.",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // CLUB CREDITS CHECKBOX
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = useCredits > 0.0,
                    onCheckedChange = { viewModel.toggleUseCredits(it) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Use Club Comparison Credits", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        "Avail up to ₹150 from your collection (Current: ₹${profileByVM?.earnedCredits?.toInt()})",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FINAL ITEMIZED PRICES BREAKDOWN
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Fare Reconciliation Summary", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                val originalBase = bookingDetails!!.amount + promoDiscount + useCredits
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Base Portal Ticket(s) Fare", fontSize = 12.sp)
                    Text("₹${originalBase.toInt()}", fontSize = 12.sp)
                }

                if (promoDiscount > 0.0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Promo Code Applied", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                        Text("-₹${promoDiscount.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }

                if (useCredits > 0.0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Club Loyalty Credits Applied", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                        Text("-₹${useCredits.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Final Payable via XChange Gateway", fontWeight = FontWeight.Bold)
                    Text("₹${bookingDetails!!.amount.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onPayClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Proceed to Secure Gateway Checkout", fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 6. PAYMENT GATEWAY VIEW
// ==========================================
@Composable
fun PaymentScreen(
    viewModel: AppViewModel,
    onBackPressed: () -> Unit,
    onPaymentStatus: (Boolean) -> Unit
) {
    val bookingDetails by viewModel.currentBookingDetails.collectAsState()
    val method by viewModel.paymentMtd.collectAsState()
    val upiId by viewModel.payUpiId.collectAsState()
    val cardNo by viewModel.payCardNo.collectAsState()
    val cardExp by viewModel.payCardExpiry.collectAsState()
    val cardCvv by viewModel.payCardCvv.collectAsState()
    val loading by viewModel.paymentLoading.collectAsState()

    if (bookingDetails == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(52.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "XChange Payment Checkout Gateway",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            "Total Payable: ₹${bookingDetails!!.amount.toInt()}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // CHOOSE PAYMENT CHANNELS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("UPI" to "UPI Pay", "CARD" to "Card Pin", "NET_BANKING" to "NetBank").forEach { (typeKey, label) ->
                val selected = method == typeKey
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.paymentMtd.value = typeKey },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dynamic methods boxes
        when (method) {
            "UPI" -> {
                OutlinedTextField(
                    value = upiId,
                    onValueChange = { viewModel.payUpiId.value = it },
                    label = { Text("Virtual UPI Address ID") },
                    placeholder = { Text("username@okhdfcbank") },
                    leadingIcon = { Icon(Icons.Default.QrCode, null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            "CARD" -> {
                OutlinedTextField(
                    value = cardNo,
                    onValueChange = { viewModel.payCardNo.value = it.take(16) },
                    label = { Text("16-Digit Card Number") },
                    leadingIcon = { Icon(Icons.Default.CreditCard, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cardExp,
                        onValueChange = { viewModel.payCardExpiry.value = it.take(5) },
                        label = { Text("Expiry (MM/YY)") },
                        modifier = Modifier.weight(1.5f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = cardCvv,
                        onValueChange = { viewModel.payCardCvv.value = it.take(3) },
                        label = { Text("CVV") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            }

            else -> {
                Text(
                    "Redirects securely to your chosen banking core infrastructure once verified.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.processCheckoutPayment {
                    onPaymentStatus(true)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Process Payment Verified", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBackPressed) {
            Text("Cancel Order Holds", color = Color.Gray)
        }
    }
}

// ==========================================
// 7. SUCCESS TKT SCREEN
// ==========================================
@Composable
fun SuccessScreen(
    viewModel: AppViewModel,
    onGoHome: () -> Unit,
    onTrackClicked: (OfflineBooking) -> Unit
) {
    val bookedObj by viewModel.paymentSuccessBooking.collectAsState()

    if (bookedObj == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = onGoHome) { Text("Search Again") }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // success checklist checklist checklist
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Ticket Booked Confirmed!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Text(
            "Booking ID: ${bookedObj!!.id}",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // EMBEDDED TICKET SHAPE CARD WITH BARCODE
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    bookedObj!!.operatorName,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Text(
                    "${bookedObj!!.fromLocation} ➔ ${bookedObj!!.toLocation}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("DEPARTS", fontSize = 9.sp, color = Color.Gray)
                        Text(bookedObj!!.departureTime, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("SEATS", fontSize = 9.sp, color = Color.Gray)
                        Text(bookedObj!!.selectedSeats.joinToString(", "), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // HIGH FIDELITY SIMULATED BARCODE/QR VISUALIZATION Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    val bars = 32
                    val barWidth = size.width / (bars * 1.5f)
                    var currentX = 0f

                    for (i in 0 until bars) {
                        val isBlack = (i % 2 == 0) || (i % 5 == 0) // dynamic pattern
                        val widthFactor = if (i % 3 == 0) 1.5f else 1.0f

                        if (isBlack) {
                            drawRect(
                                color = Color.Black,
                                size = Size(barWidth * widthFactor, size.height)
                            )
                        }
                        currentX += barWidth * widthFactor * 1.5f
                        // shift drawings
                        drawRect(
                            color = if (isBlack) Color.DarkGray else Color.Transparent,
                            topLeft = Offset(currentX, 0f),
                            size = Size(barWidth * widthFactor, size.height)
                        )
                    }
                }

                Text(
                    "SCAN FOR BOARDING VERIFICATION",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { onTrackClicked(bookedObj!!) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Map, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Track Live Vehicle Progress", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onGoHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Return to Search Board")
        }
    }
}

// ==========================================
// 8. LIVE LOCATION TRACKING SCREEN WITH SOS
// ==========================================
@Composable
fun TrackingScreen(
    viewModel: AppViewModel,
    onBackPressed: () -> Unit
) {
    val state by viewModel.trackingState.collectAsState()
    val isSosTriggered by viewModel.isSosTriggered.collectAsState()

    var reviewDialogShow by remember { mutableStateOf(false) }

    if (state == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text("Connecting Live tracking engine WebSocket streams...", fontSize = 12.sp)
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TOP CONTROLS WITH SOS RED PILL BUTTON
        Surface(tonalElevation = 6.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Live Transit Tracker", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                // PANIC SOS FLOTATION RED PILL
                Button(
                    onClick = { viewModel.triggerSOSAlert() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Warning, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SOS Emergency", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // SOS ALERT BANNER
            if (isSosTriggered) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "SOS Active Panic Alert broadcasted!",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Emergency services and police dispatcher notified. GPS Coordinates shared automatically with loved ones.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // SIMULATED MAP Canvas DRAWINGS
            Text("Simulated Journey Map Coordinates", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E293B)) // dark grid canvas color
                ) {
                    // Draw simulated lines paths
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = Color.Blue.copy(alpha = 0.3f), radius = 40f, center = Offset(size.width * 0.2f, size.height * 0.7f))
                        drawCircle(color = Color.Magenta.copy(alpha = 0.3f), radius = 40f, center = Offset(size.width * 0.8f, size.height * 0.3f))

                        // Line connector
                        drawLine(
                            color = Color.White.copy(alpha = 0.3f),
                            start = Offset(size.width * 0.2f, size.height * 0.7f),
                            end = Offset(size.width * 0.8f, size.height * 0.3f),
                            strokeWidth = 6f
                        )

                        // Current vehicle pointer
                        val currentX = size.width * 0.2f + (size.width * 0.6f * (state!!.distanceCoveredKm / state!!.totalDistanceKm.toFloat()))
                        val currentY = size.height * 0.7f - (size.height * 0.4f * (state!!.distanceCoveredKm / state!!.totalDistanceKm.toFloat()))

                        drawCircle(
                            color = Color.Cyan,
                            radius = 12f,
                            center = Offset(currentX, currentY)
                        )
                    }

                    // ETA Tag on maps
                    Text(
                        "Speed: ${state!!.currentSpeedKmh} km/h | Lat: ${String.format("%.4f", state!!.currentLatitude)}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DRIVER PROFILE CARD WITH COMMUNICATIONS ACTIONS
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar icon outline
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(state!!.driver.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Designated Professional Driver | ${state!!.driver.experienceYears} Years Exp", fontSize = 11.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
                            Text("${state!!.driver.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Direct call actions
                    IconButton(
                        onClick = { /* trigger intent calls */ },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    ) {
                        Icon(Icons.Default.Call, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STATS INFO DASHBOARDS PANEL
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Journey State Dashboard", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Current Stop", fontSize = 11.sp, color = Color.Gray)
                            Text(state!!.currentStop, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Next Stop ETA", fontSize = 11.sp, color = Color.Gray)
                            Text(state!!.nextStopETA, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = state!!.distanceCoveredKm / state!!.totalDistanceKm.toFloat(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "${state!!.distanceCoveredKm} km covered out of ${state!!.totalDistanceKm} total kilometers.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STOPS TIMELINE SECTION
            Text("Route Stops Sequence", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            state!!.timeline.forEach { stop ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (stop.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (stop.isCompleted) MaterialTheme.colorScheme.secondary else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stop.name,
                            fontWeight = if (stop.isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (stop.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Text("Scheduled: ${stop.scheduledTime} | Actual: ${stop.actualTime}", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Post review trigger
            Button(
                onClick = { reviewDialogShow = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Leave Ride Safety Feedback")
            }
        }
    }

    if (reviewDialogShow) {
        val fbText by viewModel.reviewFeedbackText.collectAsState()
        val ratingObj by viewModel.reviewRatingOverall.collectAsState()
        val statusByVM by viewModel.reviewStatusMsg.collectAsState()

        AlertDialog(
            onDismissRequest = { reviewDialogShow = false },
            title = { Text("How was your travel?", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Safety and Cleanliness Verification Scores", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { rate ->
                            IconButton(onClick = { viewModel.reviewRatingOverall.value = rate.toFloat() }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (rate <= ratingObj) MaterialTheme.colorScheme.tertiary else Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = fbText,
                        onValueChange = { viewModel.reviewFeedbackText.value = it },
                        label = { Text("E.g. Clean cabins, courteous operator behavior") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (statusByVM.isNotEmpty()) {
                        Text(statusByVM, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.submitFeedback(state!!.bookingId)
                    reviewDialogShow = false
                }) {
                    Text("Submit Feedback")
                }
            }
        )
    }
}

// ==========================================
// 9. MY BOOKINGS LOG CONSOLE
// ==========================================
@Composable
fun MyBookingsScreen(
    viewModel: AppViewModel,
    onTrackBooking: (OfflineBooking) -> Unit
) {
    val bookingsList by viewModel.myBookings.collectAsState()
    var tabSelected by remember { mutableStateOf(0) } // 0: Upcoming, 1: Completed, 2: Cancelled

    val filtered = bookingsList.filter { b ->
        when (tabSelected) {
            0 -> b.status == "CONFIRMED"
            1 -> b.status == "COMPLETED"
            else -> b.status == "CANCELLED"
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(tonalElevation = 6.dp) {
            Column {
                Text(
                    "My Booking Journeys",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                TabRow(selectedTabIndex = tabSelected) {
                    Tab(selected = tabSelected == 0, onClick = { tabSelected = 0 }) {
                        Text("Upcoming", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                    }
                    Tab(selected = tabSelected == 1, onClick = { tabSelected = 1 }) {
                        Text("Past", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                    }
                    Tab(selected = tabSelected == 2, onClick = { tabSelected = 2 }) {
                        Text("Cancelled", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No matched bookings in this tab Category.", fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered) { book ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(book.operatorName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (book.status == "CONFIRMED") MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(book.status, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                "Route: ${book.fromLocation} ➔ ${book.toLocation}",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Departure: ${book.departureTime}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Fare paid: ₹${book.amount.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (book.status == "CONFIRMED") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onTrackBooking(book) },
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Map, null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Track Ride", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.cancelBooking(book.id) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                        contentPadding = PaddingValues(vertical = 6.dp)
                                    ) {
                                        Text("Cancel Seats", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 10. PROFILE & SETTINGS
// ==========================================
@Composable
fun ProfileSettingsScreen(
    viewModel: AppViewModel,
    onBackToHome: () -> Unit
) {
    val profileByVM by viewModel.profile.collectAsState()

    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var classPref by remember { mutableStateOf("") }
    var savingFinished by remember { mutableStateOf(false) }

    LaunchedEffect(profileByVM) {
        if (profileByVM != null) {
            nameInput = profileByVM!!.name
            emailInput = profileByVM!!.email
            phoneInput = profileByVM!!.phone
            classPref = profileByVM!!.preferredClass
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Passenger Profile Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("E-mail Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phoneInput,
            onValueChange = { phoneInput = it },
            label = { Text("Contact mobile Contact") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = classPref,
            onValueChange = { classPref = it },
            label = { Text("Cabin / Coach preference settings") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (savingFinished) {
            Text("Profile details updated successfully!", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                viewModel.updateProfile(nameInput, emailInput, phoneInput, classPref)
                savingFinished = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile Reconciliations")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                viewModel.logout()
                onBackToHome()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout Accounts", color = Color.Red)
        }
    }
}

// ==========================================
// 11. ofertas & pass screen (Club Pass & Referral invites)
// ==========================================
@Composable
fun ClubPassScreen(viewModel: AppViewModel) {
    val profileByVM by viewModel.profile.collectAsState()
    val activePass by viewModel.activePassPlan.collectAsState()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 1. Top App Bar (Geometric Balance Header)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Transparent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Central Hub",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Transparent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Primary Hero Card (Information Overview)
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier
                .fillMaxWidth()
                .height(192.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Text(
                        text = "DAILY INSIGHT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (activePass != null) "Premium Active" else "84% Optimal",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "All booking channels and travel routes are performing within optimal parameters.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        maxLines = 2
                    )
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Optimize",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Geometric Info Grid (2 Columns)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Data Sync / Wallet Balance
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .weight(1f)
                    .clickable { /* action */ }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "DATA SYNC",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${profileByVM?.earnedCredits?.toInt() ?: 0} Credits",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Card 2: Latency / Invite Code
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .weight(1f)
                    .clickable { /* Copy code */ }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Invite",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "LATENCY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = profileByVM?.referralCode ?: "XCHG500R",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Task List (Geometric Alignment)
        Card(
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header of task list
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pending Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "VIEW ALL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { /* View All action */ }
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                // Packages List
                val plans = listOf(
                    Triple("Lite Active", "Traveller Lite Pass (₹199/m)", "L"),
                    Triple("Premium Active", "Commuter Premium Pass (₹499/m)", "P"),
                    Triple("Elite Active", "Elite Business Club Board (₹999/m)", "E")
                )

                plans.forEachIndexed { index, (shortName, planName, avatarChar) ->
                    val isCurrentPlan = activePass?.contains(shortName.split(" ")[0]) == true || (activePass == null && index == 0)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.purchaseSubscriptionPass(planName.split(" (")[0]) }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Perfect Lavender Circular Avatar
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (isCurrentPlan) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = avatarChar,
                                    color = if (isCurrentPlan) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = planName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isCurrentPlan) "Active Plan - Saved 15%" else "Backup System Config",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Select",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (index < plans.size - 1) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}
