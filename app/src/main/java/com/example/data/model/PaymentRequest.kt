package com.example.data.model

data class PaymentRequest(
    val id: String,
    val planName: String,
    val amount: String,
    val trxId: String,
    val userEmail: String,
    val timestamp: Long,
    val status: String // "Pending", "Approved", "Rejected"
)
