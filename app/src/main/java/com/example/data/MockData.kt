package com.example.data

import kotlin.random.Random

object MockData {

    val popularLocations = listOf(
        "Delhi (NDLS)",
        "Mumbai (CSMT)",
        "Bangalore (SBC)",
        "Pune (PUNE)",
        "Agra Cantt (AGC)",
        "Chennai Central (MAS)",
        "Kolkata (HWH)"
    )

    fun getMockBookingOptions(fromLoc: String, toLoc: String, date: String): List<BookingOption> {
        val seed = (fromLoc.hashCode() + toLoc.hashCode() + date.hashCode()).toLong()
        val random = Random(seed)

        val options = mutableListOf<BookingOption>()

        // 1. Train options (IRCTC)
        options.add(
            BookingOption(
                id = "TR_VANDE_01",
                provider = ProviderType.IRCTC,
                vehicleType = VehicleType.TRAIN,
                operatorName = "Vande Bharat Express",
                serviceNumber = "22436",
                departureTime = "06:00 AM",
                arrivalTime = "02:10 PM",
                durationText = "8h 10m",
                price = 1450.0,
                strikePrice = 1750.0, // Highlighting some savings
                rating = 4.8f,
                reviewCount = 1204,
                seatsAvailable = 45,
                totalSeats = 78,
                classCategory = "CC (AC Chair Car)",
                amenities = listOf("WiFi", "Charging", "Food Included", "AC", "Infotainment"),
                routeStops = listOf(fromLoc, "Kanpur Central", "Prayagraj Jn", toLoc)
            )
        )

        options.add(
            BookingOption(
                id = "TR_RAJDHANI_02",
                provider = ProviderType.IRCTC,
                vehicleType = VehicleType.TRAIN,
                operatorName = "Rajdhani Express",
                serviceNumber = "12952",
                departureTime = "04:30 PM",
                arrivalTime = "08:15 AM",
                durationText = "15h 45m",
                price = 2200.0,
                strikePrice = 2400.0,
                rating = 4.6f,
                reviewCount = 2390,
                seatsAvailable = 12,
                totalSeats = 64,
                classCategory = "3A (AC 3 Tier)",
                amenities = listOf("Charging", "Blanket", "Food Included", "AC", "Reading Lamp"),
                routeStops = listOf(fromLoc, "Kota Jn", "Ratlam Jn", "Vadodara Jn", toLoc)
            )
        )

        options.add(
            BookingOption(
                id = "TR_SHATABDI_03",
                provider = ProviderType.IRCTC,
                vehicleType = VehicleType.TRAIN,
                operatorName = "Shatabdi Express",
                serviceNumber = "12002",
                departureTime = "06:15 AM",
                arrivalTime = "11:40 AM",
                durationText = "5h 25m",
                price = 850.0,
                strikePrice = 950.0,
                rating = 4.4f,
                reviewCount = 850,
                seatsAvailable = 142,
                totalSeats = 150,
                classCategory = "CC (AC Chair Car)",
                amenities = listOf("Charging", "AC", "Newspaper", "Water Bottle"),
                routeStops = listOf(fromLoc, "Mathura Jn", "Agra Cantt", "Gwalior Jn", toLoc)
            )
        )

        // 2. Bus Options (RedBus)
        options.add(
            BookingOption(
                id = "BS_INTRCITY_01",
                provider = ProviderType.REDBUS,
                vehicleType = VehicleType.BUS,
                operatorName = "IntrCity SmartBus",
                serviceNumber = "IC-8820 (2x1 Sleeper)",
                departureTime = "09:30 PM",
                arrivalTime = "06:00 AM",
                durationText = "8h 30m",
                price = 799.0,
                strikePrice = 1100.0,
                rating = 4.5f,
                reviewCount = 430,
                seatsAvailable = 18,
                totalSeats = 30,
                classCategory = "AC Sleeper",
                amenities = listOf("WiFi", "Charging", "Water Bottle", "AC", "GPS Tracked", "CCTV"),
                routeStops = listOf(fromLoc, "Express Toll Plaza", "Midway休憩所", toLoc)
            )
        )

        options.add(
            BookingOption(
                id = "BS_ZING_02",
                provider = ProviderType.REDBUS,
                vehicleType = VehicleType.BUS,
                operatorName = "Zingbus Premium",
                serviceNumber = "ZB-9102 (2x2 Multi-Axle)",
                departureTime = "10:15 PM",
                arrivalTime = "06:45 AM",
                durationText = "8h 30m",
                price = 650.0,
                rating = 4.2f,
                reviewCount = 180,
                seatsAvailable = 24,
                totalSeats = 36,
                classCategory = "AC Seater",
                amenities = listOf("Charging", "Water Bottle", "AC", "CCTV", "Emergency Hammer"),
                routeStops = listOf(fromLoc, "Bypass Junction", toLoc)
            )
        )

        // 3. MakeMyTrip Exclusive aggregated options (can be standard or customized)
        options.add(
            BookingOption(
                id = "MMT_SAFAR_01",
                provider = ProviderType.MAKEMYTRIP,
                vehicleType = VehicleType.BUS,
                operatorName = "Safar Travels & Cargo",
                serviceNumber = "SF-Premium Class",
                departureTime = "08:00 PM",
                arrivalTime = "05:15 AM",
                durationText = "9h 15m",
                price = 999.0,
                strikePrice = 1250.0,
                rating = 4.3f,
                reviewCount = 92,
                seatsAvailable = 8,
                totalSeats = 24,
                classCategory = "Premium Multi-Axle Sleeper",
                amenities = listOf("WiFi", "Charging", "Toilet", "Water Bottle", "AC", "Blanket", "Reading Light"),
                routeStops = listOf(fromLoc, "Sree Travels Lounge", "NH Highway Hub", toLoc)
            )
        )

        options.add(
            BookingOption(
                id = "MMT_SPECIAL_TRAIN",
                provider = ProviderType.MAKEMYTRIP,
                vehicleType = VehicleType.TRAIN,
                operatorName = "Gatimaan Special",
                serviceNumber = "12050",
                departureTime = "08:10 AM",
                arrivalTime = "01:25 PM",
                durationText = "5h 15m",
                price = 1100.0,
                rating = 4.7f,
                reviewCount = 492,
                seatsAvailable = 32,
                totalSeats = 110,
                classCategory = "EC (Executive Chair Car)",
                amenities = listOf("WiFi", "Charging", "Food Included", "Toilet", "AC"),
                routeStops = listOf(fromLoc, "Mathura Jn", toLoc)
            )
        )

        // Generate dynamic ones if the route isn't standard
        if (options.isEmpty()) {
            val names = listOf("BlueLine Travels", "Orange Express", "GreenCab Bus", "SuperFast Express")
            for (i in 1..4) {
                val isTrain = random.nextBoolean()
                val provider = ProviderType.values()[random.nextInt(ProviderType.values().size)]
                options.add(
                    BookingOption(
                        id = "DYN_${provider.name}_${i}",
                        provider = provider,
                        vehicleType = if (isTrain) VehicleType.TRAIN else VehicleType.BUS,
                        operatorName = names[random.nextInt(names.size)] + if (isTrain) " Express" else " Volvo",
                        serviceNumber = if (isTrain) "${random.nextInt(10000, 30000)}" else "EXP-${random.nextInt(100, 999)}",
                        departureTime = "${random.nextInt(6, 11)}:00 ${if (random.nextBoolean()) "AM" else "PM"}",
                        arrivalTime = "${random.nextInt(1, 6)}:00 ${if (random.nextBoolean()) "AM" else "PM"}",
                        durationText = "${random.nextInt(4, 12)}h 00m",
                        price = (random.nextInt(5, 18) * 100).toDouble() + 99.0,
                        rating = 3.5f + random.nextFloat() * 1.5f,
                        reviewCount = random.nextInt(10, 500),
                        seatsAvailable = random.nextInt(2, 35),
                        classCategory = if (isTrain) "3A (AC 3 Tier)" else "AC Sleeper",
                        amenities = listOf("Charging", "AC", "WiFi"),
                        routeStops = listOf(fromLoc, "Midway Station", toLoc)
                    )
                )
            }
        }

        return options
    }

    fun getMockReviews(optionId: String): List<ReviewModel> {
        return listOf(
            ReviewModel(
                id = "rev1",
                author = "Amir Khan",
                rating = 5.0f,
                dateText = "2 days ago",
                reviewText = "Extremely clean, punctual, and the seats were exceptionally comfortable. The booking comparison saved me around 250 Rupees on this Vande Bharat booking compared to direct aggregator. Awesome UI!",
                componentRatings = mapOf("Cleanliness" to 5.0f, "Comfort" to 5.0f, "Driver Behavior" to 5.0f, "Timeliness" to 5.0f, "Value" to 5.0f),
                helpfulCount = 42
            ),
            ReviewModel(
                id = "rev2",
                author = "Priya Vignesh",
                rating = 4.0f,
                dateText = "1 week ago",
                reviewText = "Overall really good service. The staff offered bottled water and sanitizers right away. Slight delay of 10 minutes at the entry gate due to verification protocols but caught up in the transit.",
                componentRatings = mapOf("Cleanliness" to 4.5f, "Comfort" to 4.0f, "Driver Behavior" to 4.5f, "Timeliness" to 4.0f, "Value" to 4.0f),
                helpfulCount = 18
            ),
            ReviewModel(
                id = "rev3",
                author = "Rohit Sharma",
                rating = 3.0f,
                dateText = "2 weeks ago",
                reviewText = "Charging point was initially loose, but the conductor helped fix it. The AC cooling was balanced but WiFi connection kept disconnecting during the forest routes.",
                componentRatings = mapOf("Cleanliness" to 3.5f, "Comfort" to 3.0f, "Driver Behavior" to 4.0f, "Timeliness" to 3.5f, "Value" to 3.0f),
                helpfulCount = 29
            )
        )
    }

    fun getMockTrackingState(bookingId: String, operatorName: String): TrackingState {
        val stops = listOf(
            StopModel("Origin Terminal", "09:30 PM", "09:30 PM", isCompleted = true, isCurrent = false),
            StopModel("Expressway Plaza-1", "10:45 PM", "10:49 PM", isCompleted = true, isCurrent = false),
            StopModel("Midway Lounge Rest Stop", "11:30 PM", "11:32 PM", isCompleted = true, isCurrent = true),
            StopModel("Metro Junction Bypass", "02:15 AM", "02:15 AM", isCompleted = false, isCurrent = false),
            StopModel("Final Destination Terminal", "06:00 AM", "06:00 AM", isCompleted = false, isCurrent = false)
        )

        return TrackingState(
            bookingId = bookingId,
            originalLatitude = 28.6139,
            originalLongitude = 77.2090,
            currentLatitude = 28.2500,
            currentLongitude = 77.3800,
            distanceCoveredKm = 145,
            totalDistanceKm = 420,
            currentSpeedKmh = 78,
            currentStop = "Midway Lounge Rest Stop",
            nextStop = "Metro Junction Bypass",
            nextStopETA = "15 mins away (02:15 AM)",
            driver = DriverModel(
                name = "Rajesh Kumar",
                rating = 4.7f,
                experienceYears = 8,
                phone = "+91 98765 43210"
            ),
            timeline = stops
        )
    }
}
