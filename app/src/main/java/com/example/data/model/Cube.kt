package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cubes")
data class Cube(
    @PrimaryKey
    val id: String,
    val creatorId: String,
    val name: String,
    val description: String,
    val colorHex: String = "#00F0FF",
    val price: Double,
    val currency: String = "USD",
    val status: String = "PUBLISHED", // "PUBLISHED", "DRAFT", "PAUSED", "EXPIRED"
    val expirationType: String = "NEVER", // "NEVER", "CUSTOM"
    val expiresAt: Long = 0L,
    val borderStyle: String = "DEFAULT",
    val backgroundStyle: String = "SOLID",
    val fontStyle: String = "DEFAULT",
    val effectStyle: String = "NONE",
    val themeStyle: String = "CYBERPUNK",
    val avatarId: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isExpired: Boolean
        get() {
            if (status == "EXPIRED") return true
            if (expirationType == "CUSTOM" && expiresAt > 0L && System.currentTimeMillis() >= expiresAt) {
                return true
            }
            return false
        }

    val canReceivePurchases: Boolean
        get() = status == "PUBLISHED" && !isExpired

    val shareableUrl: String
        get() = "https://cubepay.me/c/$id"
}
