package com.surgex.app.domain.fare

enum class RideCategory {
    SURGEX,          // Standard
    SURGEX_COMFORT,
    SURGEX_PRIORITY,
    SURGEX_PREMIUM,
    SURGEX_XL
}

data class FareInput(
    val distanceKm: Double,
    val durationMinutes: Int,
    val category: RideCategory = RideCategory.SURGEX,
    val waitingMinutes: Int = 0,
    val tolls: Double = 0.0
)

data class FareBreakdown(
    val category: RideCategory,
    val baseFare: Double,
    val distanceFare: Double,
    val timeFare: Double,
    val bookingFee: Double,
    val waitingFee: Double,
    val tolls: Double,
    val total: Double,
    val currency: String = "R"
)

object FareCalculator {

    // Cape Town focused rates (you can adjust later)
    private data class CategoryRates(
        val baseFare: Double,
        val perKm: Double,
        val perMinute: Double,
        val bookingFee: Double,
        val waitingPerMinute: Double,
        val minimumFare: Double
    )

    private val rates = mapOf(
        RideCategory.SURGEX to CategoryRates(
            baseFare = 18.0,
            perKm = 8.50,
            perMinute = 1.40,
            bookingFee = 5.0,
            waitingPerMinute = 1.20,
            minimumFare = 35.0
        ),
        RideCategory.SURGEX_COMFORT to CategoryRates(
            baseFare = 25.0,
            perKm = 10.50,
            perMinute = 1.70,
            bookingFee = 7.0,
            waitingPerMinute = 1.50,
            minimumFare = 48.0
        ),
        RideCategory.SURGEX_PRIORITY to CategoryRates(
            baseFare = 30.0,
            perKm = 11.80,
            perMinute = 1.90,
            bookingFee = 8.0,
            waitingPerMinute = 1.70,
            minimumFare = 55.0
        ),
        RideCategory.SURGEX_PREMIUM to CategoryRates(
            baseFare = 45.0,
            perKm = 15.50,
            perMinute = 2.40,
            bookingFee = 12.0,
            waitingPerMinute = 2.20,
            minimumFare = 85.0
        ),
        RideCategory.SURGEX_XL to CategoryRates(
            baseFare = 35.0,
            perKm = 12.80,
            perMinute = 2.00,
            bookingFee = 10.0,
            waitingPerMinute = 1.80,
            minimumFare = 70.0
        )
    )

    fun calculate(input: FareInput): FareBreakdown {
        val r = rates[input.category] ?: rates[RideCategory.SURGEX]!!

        val distanceFare = input.distanceKm * r.perKm
        val timeFare = input.durationMinutes * r.perMinute
        val waitingFee = input.waitingMinutes * r.waitingPerMinute

        var total = r.baseFare + distanceFare + timeFare + r.bookingFee + waitingFee + input.tolls

        // Enforce minimum fare
        if (total < r.minimumFare) {
            total = r.minimumFare
        }

        return FareBreakdown(
            category = input.category,
            baseFare = r.baseFare,
            distanceFare = distanceFare,
            timeFare = timeFare,
            bookingFee = r.bookingFee,
            waitingFee = waitingFee,
            tolls = input.tolls,
            total = total
        )
    }

    // Helper to get estimated fare for a category (used on ride selection screen)
    fun estimate(
        distanceKm: Double,
        durationMinutes: Int,
        category: RideCategory
    ): Double {
        return calculate(
            FareInput(
                distanceKm = distanceKm,
                durationMinutes = durationMinutes,
                category = category
            )
        ).total
    }
}
