package com.surgex.app.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Payment processing and wallet management
 */
class PaymentService {
    private val _walletBalance = MutableStateFlow(0.0)
    val walletBalance: StateFlow<Double> = _walletBalance

    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    /**
     * Process payment for a completed trip
     */
    fun processPayment(
        tripId: String,
        amount: Double,
        paymentMethodId: String,
        paymentType: String // CASH, CARD, WALLET, etc.
    ): PaymentResult {
        return try {
            val transaction = Transaction(
                tripId = tripId,
                amount = amount,
                paymentMethodId = paymentMethodId,
                paymentType = paymentType,
                status = "SUCCESS",
                timestamp = java.time.LocalDateTime.now()
            )
            _transactions.value = _transactions.value + transaction

            // Update wallet if using wallet payment
            if (paymentType == "WALLET") {
                _walletBalance.value -= amount
            }

            PaymentResult.Success(transaction)
        } catch (e: Exception) {
            PaymentResult.Failure(e.message ?: "Unknown error")
        }
    }

    /**
     * Add payment method
     */
    fun addPaymentMethod(
        methodType: String,
        name: String,
        details: String
    ) {
        val method = PaymentMethod(
            id = "PM_${System.currentTimeMillis()}",
            methodType = methodType,
            name = name,
            details = details
        )
        _paymentMethods.value = _paymentMethods.value + method
    }

    /**
     * Remove payment method
     */
    fun removePaymentMethod(methodId: String) {
        _paymentMethods.value = _paymentMethods.value.filter { it.id != methodId }
    }

    /**
     * Add credit to wallet (via bank transfer, etc.)
     */
    fun addWalletCredit(amount: Double, source: String = "Manual") {
        _walletBalance.value += amount
    }

    /**
     * Withdraw from wallet
     */
    fun withdrawFromWallet(amount: Double, bankAccount: String): PaymentResult {
        return if (_walletBalance.value >= amount) {
            _walletBalance.value -= amount
            PaymentResult.Success(
                Transaction(
                    tripId = "WITHDRAWAL",
                    amount = amount,
                    paymentMethodId = "BANK",
                    paymentType = "BANK_WITHDRAWAL",
                    status = "SUCCESS",
                    timestamp = java.time.LocalDateTime.now()
                )
            )
        } else {
            PaymentResult.Failure("Insufficient wallet balance")
        }
    }
}

data class PaymentMethod(
    val id: String,
    val methodType: String, // CARD, WALLET, BANK, etc.
    val name: String,
    val details: String
)

data class Transaction(
    val tripId: String,
    val amount: Double,
    val paymentMethodId: String,
    val paymentType: String,
    val status: String, // SUCCESS, FAILED, PENDING
    val timestamp: java.time.LocalDateTime
)

sealed class PaymentResult {
    data class Success(val transaction: Transaction) : PaymentResult()
    data class Failure(val error: String) : PaymentResult()
}
