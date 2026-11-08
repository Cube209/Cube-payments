package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_logs")
data class SecurityLog(
    @PrimaryKey
    val id: String,
    val creatorId: String,
    val eventType: String, // "LOGIN", "PASSWORD_CHANGE", "2FA_TOGGLED", "PAYOUT_REQUEST", "DEVICE_REGISTERED"
    val description: String,
    val ipLocation: String = "San Francisco, US",
    val deviceInfo: String = "Pixel 8 Pro • Android 14",
    val timestamp: Long = System.currentTimeMillis()
)
