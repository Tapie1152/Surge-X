package com.surgex.app.engine.fare

import com.surgex.app.domain.fare.FareBreakdown
import com.surgex.app.domain.fare.FareInput
import com.surgex.app.domain.fare.RideCategory

class FareEngine {

    fun calculate(
        distanceKm: Double,
        durationMinutes: Int,
        surgeMultiplier: Double = 1.0,
        baseFare: Double = 20.0,
        perKm: Double = 8.50,
        perMinute: Double = 1.50,
        bookingFee: Double = 5.00,
        waitingFee: Double = 0.00,
        tolls: Double = 0.00,
        category: RideCategory = RideCategory.SURGEX
    ): FareBreakdown {

        val normal = FareInput(
            distanceKm = distanceKm,
            durationMinutes = durationMinutes,
            category = category
        )

        val distanceFare = distanceKm * perKm
        val timeFare = durationMinutes * perMinute

        val subtotal =
            baseFare +
            distanceFare +
            timeFare +
            bookingFee +
            waitingFee +
            tolls

        val total = subtotal * surgeMultiplier

        /*
         * Surge is included in the final total.
         * The existing FareBreakdown model does not expose
         * surgeAmount, so we preserve compatibility with the UI.
         */
        return FareBreakdown(
            category = category,
            baseFare = baseFare,
            distanceFare = distanceFare,
            timeFare = timeFare,
            bookingFee = bookingFee,
            waitingFee = waitingFee,
            tolls = tolls,
            total = total
        )
    }
}
