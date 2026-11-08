package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
    val id: String,
    val cubeId: String,
    val creatorId: String,
    val cubeName: String,
    val buyerName: String,
    val buyerEmail: String,
    val amount: Double,
    val currency: String,
    val processingFee: Double,
    val netAmount: Double,
    val status: String = "COMPLETED",
    val paymentMethod: String = "Credit Card",
    val createdAt: Long = System.currentTimeMillis()
)
