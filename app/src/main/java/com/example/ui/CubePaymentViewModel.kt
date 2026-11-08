package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CubeDatabase
import com.example.data.model.Creator
import com.example.data.model.Cube
import com.example.data.model.Payout
import com.example.data.model.SecurityLog
import com.example.data.model.Transaction
import com.example.data.repository.CubePaymentRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface Screen {
    object Home : Screen
    data class Auth(val isSignUp: Boolean = false) : Screen
    object Dashboard : Screen
    data class CreateCube(val existingCubeId: String? = null) : Screen
    data class BuyerPayment(val cubeId: String) : Screen
    object Store : Screen
    object Profile : Screen
    object SecurityCenter : Screen
    data class PaymentReceipt(val transaction: Transaction) : Screen
}

enum class CubeFilter {
    ALL, PUBLISHED, DRAFTS, PAUSED, EXPIRED
}

class CubePaymentViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CubeDatabase.getDatabase(application, viewModelScope)
    val repository = CubePaymentRepository(db)

    // Current navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Navigation backstack
    private val backStack = mutableListOf<Screen>()

    // Current creator ID (defaults to demo creator if available)
    private val _currentCreatorId = MutableStateFlow<String?>("creator-alex-prime")
    val currentCreatorId: StateFlow<String?> = _currentCreatorId.asStateFlow()

    // Current creator
    val currentCreator: StateFlow<Creator?> = _currentCreatorId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else repository.getCreator(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filter
    private val _filter = MutableStateFlow(CubeFilter.ALL)
    val filter: StateFlow<CubeFilter> = _filter.asStateFlow()

    // Cubes for current creator
    val cubes: StateFlow<List<Cube>> = _currentCreatorId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else repository.getCubes(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cube count (for capacity meter 50)
    val cubeCount: StateFlow<Int> = _currentCreatorId.flatMapLatest { id ->
        if (id == null) flowOf(0)
        else repository.getCubeCount(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Transactions for current creator
    val transactions: StateFlow<List<Transaction>> = _currentCreatorId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else repository.getTransactions(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Payouts for current creator
    val payouts: StateFlow<List<Payout>> = _currentCreatorId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else repository.getPayouts(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Security logs
    val securityLogs: StateFlow<List<SecurityLog>> = _currentCreatorId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else repository.getSecurityLogs(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Feedback messages
    private val _snackBarMessage = MutableSharedFlow<String>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    fun navigateTo(screen: Screen) {
        backStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        } else if (_currentScreen.value !is Screen.Home) {
            _currentScreen.value = Screen.Home
            return true
        }
        return false
    }

    fun setFilter(filter: CubeFilter) {
        _filter.value = filter
    }

    fun showMessage(msg: String) {
        viewModelScope.launch {
            _snackBarMessage.emit(msg)
        }
    }

    // ----------------------------------------------------
    // AUTH ACTIONS
    // ----------------------------------------------------

    fun signIn(userOrEmail: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.signIn(userOrEmail, pass)
            result.onSuccess { creator ->
                _currentCreatorId.value = creator.id
                showMessage("Welcome back, @${creator.username}!")
                onSuccess()
                navigateTo(Screen.Dashboard)
            }.onFailure { ex ->
                onError(ex.message ?: "Sign in failed.")
            }
        }
    }

    fun createAccount(
        username: String,
        email: String,
        pass: String,
        displayName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.createAccount(username, email, pass, displayName)
            result.onSuccess { creator ->
                _currentCreatorId.value = creator.id
                showMessage("Account created for @${creator.username}!")
                onSuccess()
                navigateTo(Screen.Dashboard)
            }.onFailure { ex ->
                onError(ex.message ?: "Failed to create account.")
            }
        }
    }

    fun signOut() {
        _currentCreatorId.value = null
        showMessage("Signed out successfully.")
        _currentScreen.value = Screen.Home
        backStack.clear()
    }

    // ----------------------------------------------------
    // CUBE ACTIONS
    // ----------------------------------------------------

    fun saveCube(
        id: String?,
        name: String,
        description: String,
        colorHex: String,
        price: Double,
        currency: String,
        isDraft: Boolean,
        expirationType: String,
        expiresAt: Long,
        borderStyle: String,
        backgroundStyle: String,
        fontStyle: String,
        effectStyle: String,
        themeStyle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val creatorId = _currentCreatorId.value ?: run {
            onError("Please sign in first")
            return
        }

        viewModelScope.launch {
            val cubeId = id ?: ("cb-" + UUID.randomUUID().toString().take(8))
            val status = if (isDraft) "DRAFT" else "PUBLISHED"

            val cube = Cube(
                id = cubeId,
                creatorId = creatorId,
                name = name.trim(),
                description = description.trim(),
                colorHex = colorHex,
                price = price,
                currency = currency,
                status = status,
                expirationType = expirationType,
                expiresAt = expiresAt,
                borderStyle = borderStyle,
                backgroundStyle = backgroundStyle,
                fontStyle = fontStyle,
                effectStyle = effectStyle,
                themeStyle = themeStyle,
                updatedAt = System.currentTimeMillis()
            )

            val result = if (id == null) {
                repository.createCube(cube)
            } else {
                repository.updateCube(cube)
            }

            result.onSuccess {
                showMessage(if (isDraft) "Cube saved as draft" else "Cube published successfully!")
                onSuccess()
                navigateBack()
            }.onFailure { ex ->
                onError(ex.message ?: "Failed to save Cube.")
            }
        }
    }

    fun togglePauseCube(cube: Cube) {
        viewModelScope.launch {
            if (cube.status == "PAUSED") {
                repository.unpauseCube(cube.id)
                showMessage("Cube '${cube.name}' unpaused and active.")
            } else {
                repository.pauseCube(cube.id)
                showMessage("Cube '${cube.name}' paused.")
            }
        }
    }

    fun deleteCube(cube: Cube) {
        viewModelScope.launch {
            repository.deleteCube(cube.id)
            showMessage("Cube deleted. Capacity slot freed up!")
        }
    }

    // ----------------------------------------------------
    // BUYER CHECKOUT ACTIONS
    // ----------------------------------------------------

    fun processBuyerPayment(
        cubeId: String,
        buyerName: String,
        buyerEmail: String,
        paymentMethod: String,
        onSuccess: (Transaction) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.processBuyerPayment(cubeId, buyerName, buyerEmail, paymentMethod)
            result.onSuccess { tx ->
                showMessage("Payment successful! Receipt generated.")
                onSuccess(tx)
                navigateTo(Screen.PaymentReceipt(tx))
            }.onFailure { ex ->
                onError(ex.message ?: "Payment failed.")
            }
        }
    }

    // ----------------------------------------------------
    // IN-APP STORE PURCHASES
    // ----------------------------------------------------

    fun purchaseStoreItem(itemType: String, priceStr: String) {
        val creatorId = _currentCreatorId.value ?: return
        viewModelScope.launch {
            val result = repository.purchaseStoreItem(creatorId, itemType)
            result.onSuccess {
                val label = if (itemType == "DECORATION_PACK") "Complete Cube Decoration Pack ($priceStr)" else "Username Colour Pack ($priceStr)"
                showMessage("🎉 Purchase complete! Unlocked $label.")
            }.onFailure { ex ->
                showMessage(ex.message ?: "Purchase failed")
            }
        }
    }

    fun setUsernameColor(colorHex: String) {
        val creatorId = _currentCreatorId.value ?: return
        viewModelScope.launch {
            repository.updateUsernameColor(creatorId, colorHex)
            showMessage("Username colour updated!")
        }
    }

    // ----------------------------------------------------
    // BANK WITHDRAWALS
    // ----------------------------------------------------

    fun requestWithdrawal(amount: Double, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val creatorId = _currentCreatorId.value ?: return
        viewModelScope.launch {
            val result = repository.requestWithdrawal(creatorId, amount)
            result.onSuccess { payout ->
                showMessage("Direct bank payout of $${String.format("%.2f", amount)} processed to ${payout.bankSummary}!")
                onSuccess()
            }.onFailure { ex ->
                onError(ex.message ?: "Withdrawal failed.")
            }
        }
    }

    // ----------------------------------------------------
    // SECURITY CENTER
    // ----------------------------------------------------

    fun verifyEmail() {
        val creatorId = _currentCreatorId.value ?: return
        viewModelScope.launch {
            repository.verifyEmail(creatorId)
            showMessage("✅ Email verified successfully!")
        }
    }

    fun toggleTwoFactor(enable: Boolean) {
        val creatorId = _currentCreatorId.value ?: return
        viewModelScope.launch {
            repository.toggleTwoFactor(creatorId, enable)
            showMessage(if (enable) "Two-factor authentication enabled." else "Two-factor authentication disabled.")
        }
    }

    fun changePassword(oldPass: String, newPass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val creatorId = _currentCreatorId.value ?: return
        viewModelScope.launch {
            val result = repository.changePassword(creatorId, oldPass, newPass)
            result.onSuccess {
                showMessage("Password updated successfully.")
                onSuccess()
            }.onFailure { ex ->
                onError(ex.message ?: "Failed to change password.")
            }
        }
    }

    fun updateBankDetails(bankName: String, account: String, routing: String) {
        val creator = currentCreator.value ?: return
        viewModelScope.launch {
            repository.updateCreatorProfile(
                creator.copy(
                    bankName = bankName,
                    bankAccountNumber = account,
                    bankRouting = routing
                )
            )
            showMessage("Bank details updated.")
        }
    }
}
