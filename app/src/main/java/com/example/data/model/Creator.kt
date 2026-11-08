package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creators")
data class Creator(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val displayName: String,
    val bio: String = "",
    val avatarId: Int = 1,
    val usernameColor: String = "#00F0FF",
    val hasUsernameColorPack: Boolean = false,
    val hasDecorationPack: Boolean = false,
    val bankName: String = "Chase Bank",
    val bankAccountNumber: String = "•••• 4821",
    val bankRouting: String = "021000021",
    val balance: Double = 0.0,
    val isEmailVerified: Boolean = false,
    val isTwoFactorEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
