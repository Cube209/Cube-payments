package com.example.data.repository

import com.example.data.local.CubeDatabase
import com.example.data.model.Creator
import com.example.data.model.Cube
import com.example.data.model.Payout
import com.example.data.model.SecurityLog
import com.example.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CubePaymentRepository(private val db: CubeDatabase) {

    val creatorDao = db.creatorDao()
    val cubeDao = db.cubeDao()
    val transactionDao = db.transactionDao()
    val payoutDao = db.payoutDao()
    val securityLogDao = db.securityLogDao()

    // ----------------------------------------------------
    // AUTH & CREATOR ACCOUNTS
    // ----------------------------------------------------

    fun getCreator(id: String): Flow<Creator?> = creatorDao.getCreatorById(id)

    suspend fun getCreatorOnce(id: String): Creator? = creatorDao.getCreatorByIdOnce(id)

    suspend fun signIn(usernameOrEmail: String, passwordPlain: String): Result<Creator> {
        val trimmed = usernameOrEmail.trim()
        val creator = if (trimmed.contains("@")) {
            creatorDao.getCreatorByEmail(trimmed)
        } else {
            creatorDao.getCreatorByUsername(trimmed)
        }

        if (creator == null) {
            return Result.failure(Exception("Account not found. Please check username or email."))
        }

        if (creator.passwordHash != passwordPlain) {
            return Result.failure(Exception("Incorrect password. Please try again."))
        }

        // Log successful login
        securityLogDao.insertLog(
            SecurityLog(
                id = UUID.randomUUID().toString(),
                creatorId = creator.id,
                eventType = "LOGIN",
                description = "Creator signed in successfully",
                ipLocation = "Current Device",
                deviceInfo = "Android Mobile Session"
            )
        )

        return Result.success(creator)
    }

    suspend fun createAccount(
        username: String,
        email: String,
        passwordPlain: String,
        displayName: String
    ): Result<Creator> {
        val cleanUsername = username.trim().lowercase()
        val cleanEmail = email.trim().lowercase()

        if (cleanUsername.isBlank()) {
            return Result.failure(Exception("Username cannot be empty."))
        }
        if (!cleanUsername.matches(Regex("^[a-zA-Z0-9_]{3,20}$"))) {
            return Result.failure(Exception("Username must be 3-20 alphanumeric characters or underscores."))
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return Result.failure(Exception("Please enter a valid email address."))
        }
        if (passwordPlain.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters."))
        }

        val existingUser = creatorDao.getCreatorByUsername(cleanUsername)
        if (existingUser != null) {
            return Result.failure(Exception("Username @$cleanUsername is already taken."))
        }

        val existingEmail = creatorDao.getCreatorByEmail(cleanEmail)
        if (existingEmail != null) {
            return Result.failure(Exception("Email $cleanEmail is already registered."))
        }

        val newCreator = Creator(
            id = "creator-" + UUID.randomUUID().toString().take(8),
            username = cleanUsername,
            email = cleanEmail,
            passwordHash = passwordPlain,
            displayName = displayName.ifBlank { cleanUsername },
            bio = "New Cube creator",
            balance = 0.0,
            hasUsernameColorPack = false,
            hasDecorationPack = false,
            isEmailVerified = false,
            isTwoFactorEnabled = false
        )

        creatorDao.insertCreator(newCreator)

        securityLogDao.insertLog(
            SecurityLog(
                id = UUID.randomUUID().toString(),
                creatorId = newCreator.id,
                eventType = "ACCOUNT_CREATED",
                description = "Account registered and secured",
                ipLocation = "Current Device",
                deviceInfo = "Android Mobile Session"
            )
        )

        return Result.success(newCreator)
    }

    suspend fun updateCreatorProfile(creator: Creator) {
        creatorDao.updateCreator(creator)
    }

    suspend fun updateUsernameColor(creatorId: String, colorHex: String) {
        val creator = creatorDao.getCreatorByIdOnce(creatorId) ?: return
        creatorDao.updateCreator(creator.copy(usernameColor = colorHex))
    }

    // ----------------------------------------------------
    // IN-APP STORE PURCHASES
    // ----------------------------------------------------

    suspend fun purchaseStoreItem(creatorId: String, itemType: String): Result<Unit> {
        val creator = creatorDao.getCreatorByIdOnce(creatorId)
            ?: return Result.failure(Exception("Creator account not found"))

        val updated = when (itemType) {
            "USERNAME_COLOR" -> {
                creator.copy(hasUsernameColorPack = true)
            }
            "DECORATION_PACK" -> {
                creator.copy(hasDecorationPack = true)
            }
            else -> return Result.failure(Exception("Unknown product"))
        }

        creatorDao.updateCreator(updated)

        securityLogDao.insertLog(
            SecurityLog(
                id = UUID.randomUUID().toString(),
                creatorId = creatorId,
                eventType = "STORE_PURCHASE",
                description = "Unlocked $itemType from In-App Store",
                ipLocation = "Current Device",
                deviceInfo = "Google Play In-App Billing"
            )
        )

        return Result.success(Unit)
    }

    // ----------------------------------------------------
    // CUBES CRUD & ACCOUNT LIMITS
    // ----------------------------------------------------

    fun getCubes(creatorId: String): Flow<List<Cube>> = cubeDao.getCubesByCreator(creatorId)

    fun getCubeCount(creatorId: String): Flow<Int> = cubeDao.getCubeCountByCreator(creatorId)

    fun getCube(cubeId: String): Flow<Cube?> = cubeDao.getCubeById(cubeId)

    suspend fun getCubeOnce(cubeId: String): Cube? = cubeDao.getCubeByIdOnce(cubeId)

    suspend fun createCube(cube: Cube): Result<Unit> {
        // Enforce maximum 50 Cubes per account (Section 3 & 12)
        val currentCount = cubeDao.getCubeCountByCreatorOnce(cube.creatorId)
        if (currentCount >= 50) {
            return Result.failure(Exception("Account limit reached: Maximum 50 Cubes allowed per account."))
        }

        if (cube.name.isBlank()) {
            return Result.failure(Exception("Cube name is required."))
        }

        if (cube.price <= 0.0) {
            return Result.failure(Exception("Please enter a valid price greater than 0."))
        }

        cubeDao.insertCube(cube)
        return Result.success(Unit)
    }

    suspend fun updateCube(updatedCube: Cube): Result<Unit> {
        val existing = cubeDao.getCubeByIdOnce(updatedCube.id)
            ?: return Result.failure(Exception("Cube not found."))

        // Requirement 5: "Expired Cubes cannot be extended"
        if (existing.isExpired && updatedCube.expiresAt != existing.expiresAt) {
            return Result.failure(Exception("Policy restriction: Expired Cubes cannot be extended."))
        }

        cubeDao.updateCube(updatedCube.copy(updatedAt = System.currentTimeMillis()))
        return Result.success(Unit)
    }

    suspend fun pauseCube(cubeId: String) {
        cubeDao.updateCubeStatus(cubeId, "PAUSED")
    }

    suspend fun unpauseCube(cubeId: String) {
        val cube = cubeDao.getCubeByIdOnce(cubeId) ?: return
        if (cube.isExpired) return
        cubeDao.updateCubeStatus(cubeId, "PUBLISHED")
    }

    suspend fun deleteCube(cubeId: String) {
        // Deleted cubes free up slots
        cubeDao.deleteCubeById(cubeId)
    }

    // ----------------------------------------------------
    // PAYMENTS & BUYER CHECKOUT
    // ----------------------------------------------------

    suspend fun processBuyerPayment(
        cubeId: String,
        buyerName: String,
        buyerEmail: String,
        paymentMethod: String
    ): Result<Transaction> {
        val cube = cubeDao.getCubeByIdOnce(cubeId)
            ?: return Result.failure(Exception("Cube not found or link is invalid."))

        // Requirement 5: Expired Cubes cannot receive purchases
        if (cube.isExpired) {
            return Result.failure(Exception("This Cube has expired and can no longer receive purchases."))
        }

        if (cube.status == "PAUSED") {
            return Result.failure(Exception("This Cube is currently paused by the creator."))
        }

        if (cube.status == "DRAFT") {
            return Result.failure(Exception("This Cube is still a draft and is not published for payments."))
        }

        if (buyerName.isBlank() || buyerEmail.isBlank() || !buyerEmail.contains("@")) {
            return Result.failure(Exception("Please provide valid buyer details."))
        }

        // Section 8: Creators receive 100% of the sale minus payment processing fees.
        // Cube PAYMENT commission is 0%.
        // Standard processing fee: 2.9% + 0.30 (or equivalent base)
        val grossAmount = cube.price
        val processingFee = (grossAmount * 0.029) + 0.30
        val netAmount = (grossAmount - processingFee).coerceAtLeast(0.0)

        val transaction = Transaction(
            id = "tx-" + UUID.randomUUID().toString().take(10),
            cubeId = cube.id,
            creatorId = cube.creatorId,
            cubeName = cube.name,
            buyerName = buyerName.trim(),
            buyerEmail = buyerEmail.trim(),
            amount = grossAmount,
            currency = cube.currency,
            processingFee = processingFee,
            netAmount = netAmount,
            status = "COMPLETED",
            paymentMethod = paymentMethod,
            createdAt = System.currentTimeMillis()
        )

        transactionDao.insertTransaction(transaction)

        // Credit creator balance
        creatorDao.addBalance(cube.creatorId, netAmount)

        return Result.success(transaction)
    }

    fun getTransactions(creatorId: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCreator(creatorId)

    // ----------------------------------------------------
    // BANK WITHDRAWALS & PAYOUTS
    // ----------------------------------------------------

    suspend fun requestWithdrawal(creatorId: String, amount: Double): Result<Payout> {
        val creator = creatorDao.getCreatorByIdOnce(creatorId)
            ?: return Result.failure(Exception("Creator account not found."))

        if (amount <= 0.0) {
            return Result.failure(Exception("Withdrawal amount must be greater than 0."))
        }

        if (amount > creator.balance) {
            return Result.failure(Exception("Insufficient balance for this payout. Available: ${creator.balance}"))
        }

        val payout = Payout(
            id = "payout-" + UUID.randomUUID().toString().take(8),
            creatorId = creatorId,
            amount = amount,
            currency = "USD",
            bankSummary = "${creator.bankName} (${creator.bankAccountNumber})",
            status = "COMPLETED",
            referenceCode = "PAYOUT-" + UUID.randomUUID().toString().take(6).uppercase()
        )

        creatorDao.deductBalance(creatorId, amount)
        payoutDao.insertPayout(payout)

        securityLogDao.insertLog(
            SecurityLog(
                id = UUID.randomUUID().toString(),
                creatorId = creatorId,
                eventType = "PAYOUT_REQUEST",
                description = "Direct bank withdrawal of $amount processed to ${creator.bankName}",
                ipLocation = "Verified Device",
                deviceInfo = "Automated Clearing House (ACH)"
            )
        )

        return Result.success(payout)
    }

    fun getPayouts(creatorId: String): Flow<List<Payout>> =
        payoutDao.getPayoutsByCreator(creatorId)

    // ----------------------------------------------------
    // SECURITY & AUDIT
    // ----------------------------------------------------

    fun getSecurityLogs(creatorId: String): Flow<List<SecurityLog>> =
        securityLogDao.getLogsByCreator(creatorId)

    suspend fun toggleTwoFactor(creatorId: String, enable: Boolean) {
        val creator = creatorDao.getCreatorByIdOnce(creatorId) ?: return
        creatorDao.updateCreator(creator.copy(isTwoFactorEnabled = enable))
        securityLogDao.insertLog(
            SecurityLog(
                id = UUID.randomUUID().toString(),
                creatorId = creatorId,
                eventType = "2FA_TOGGLED",
                description = if (enable) "Two-factor authentication enabled" else "Two-factor authentication disabled",
                ipLocation = "Current Device",
                deviceInfo = "Security Center"
            )
        )
    }

    suspend fun verifyEmail(creatorId: String) {
        val creator = creatorDao.getCreatorByIdOnce(creatorId) ?: return
        creatorDao.updateCreator(creator.copy(isEmailVerified = true))
        securityLogDao.insertLog(
            SecurityLog(
                id = UUID.randomUUID().toString(),
                creatorId = creatorId,
                eventType = "EMAIL_VERIFIED",
                description = "Email address successfully verified",
                ipLocation = "Current Device",
                deviceInfo = "Security Center"
            )
        )
    }

    suspend fun changePassword(creatorId: String, oldPass: String, newPass: String): Result<Unit> {
        val creator = creatorDao.getCreatorByIdOnce(creatorId)
            ?: return Result.failure(Exception("Creator not found"))

        if (creator.passwordHash != oldPass) {
            return Result.failure(Exception("Current password does not match."))
        }

        if (newPass.length < 6) {
            return Result.failure(Exception("New password must be at least 6 characters."))
        }

        creatorDao.updateCreator(creator.copy(passwordHash = newPass))
        securityLogDao.insertLog(
            SecurityLog(
                id = UUID.randomUUID().toString(),
                creatorId = creatorId,
                eventType = "PASSWORD_CHANGE",
                description = "Account password updated",
                ipLocation = "Current Device",
                deviceInfo = "Security Center"
            )
        )

        return Result.success(Unit)
    }
}
