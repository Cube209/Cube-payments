package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payouts")
data class Payout(
    @PrimaryKey
    val id: String,
    val creatorId: String,
    val amount: Double,
    val currency: String,
    val bankSummary: String,
    val status: String = "COMPLETED", // "PROCESSING", "COMPLETED"
    val referenceCode: String,
    val createdAt: Long = System.currentTimeMillis()
)
