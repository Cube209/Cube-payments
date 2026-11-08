package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Currencies
import com.example.ui.CubePaymentViewModel
import com.example.ui.Screen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CubeEmerald
import com.example.ui.theme.CubeGold
import com.example.ui.theme.CubeRose
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: CubePaymentViewModel,
    modifier: Modifier = Modifier
) {
    val currentCreator by viewModel.currentCreator.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val payouts by viewModel.payouts.collectAsState()
    val scrollState = rememberScrollState()

    var showBankEditDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    val creator = currentCreator
    if (creator == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No active creator session.", color = TextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.navigateTo(Screen.Auth(false)) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("Sign In", color = Color(0xFF00363D))
            }
        }
        return
    }

    val usernameColor = try {
        if (creator.hasUsernameColorPack && creator.usernameColor.isNotBlank()) {
            Color(android.graphics.Color.parseColor(creator.usernameColor))
        } else {
            NeonCyan
        }
    } catch (e: Exception) {
        NeonCyan
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Creator Profile",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { viewModel.signOut() },
                modifier = Modifier.testTag("sign_out_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Sign Out",
                    tint = CubeRose
                )
            }
        }

        // Profile Identity Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(SurfaceVariantDark)
                        .border(2.dp, usernameColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = creator.username.take(1).uppercase(),
                        color = usernameColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = creator.displayName,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "@${creator.username}",
                            color = usernameColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (creator.isEmailVerified) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = CubeEmerald,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(start = 4.dp)
                            )
                        }
                    }
                    Text(
                        text = creator.email,
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                if (creator.bio.isNotBlank()) {
                    Text(
                        text = creator.bio,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Direct Bank Payout Settings (Section 8)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Direct Bank Payouts",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(onClick = { showBankEditDialog = true }) {
                        Text("Edit", color = NeonCyan, fontSize = 13.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariantDark)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Linked Destination Bank", color = TextTertiary, fontSize = 11.sp)
                        Text(creator.bankName, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text("Account: ${creator.bankAccountNumber} • Routing: ${creator.bankRouting}", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Commission: 0% Platform Fee Guarantee", color = CubeEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Available to Withdraw", color = TextSecondary, fontSize = 12.sp)
                        Text("$${String.format("%.2f", creator.balance)}", color = NeonCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Button(
                        onClick = { showWithdrawDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Withdraw Funds", color = Color(0xFF00363D), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Security Center Quick Access (Section 13)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                .clickable { viewModel.navigateTo(Screen.SecurityCenter) }
                .padding(18.dp)
                .testTag("open_security_center_button")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = CubeEmerald, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Security & Audit Center",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "2FA, email verification, login audit history",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextSecondary)
            }
        }

        // Transaction History (Section 8)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.History, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Transaction History",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "${transactions.size} Sales",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                if (transactions.isEmpty()) {
                    Text(
                        text = "No payments received yet. Share your Cube links to start receiving sales.",
                        color = TextTertiary,
                        fontSize = 13.sp
                    )
                } else {
                    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    transactions.forEach { tx ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceVariantDark)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tx.cubeName,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${tx.buyerName} • ${sdf.format(Date(tx.createdAt))}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "+${Currencies.format(tx.netAmount, tx.currency)}",
                                        color = CubeEmerald,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Fee: $${String.format("%.2f", tx.processingFee)} (0% comm)",
                                        color = TextTertiary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Bank Dialog
    if (showBankEditDialog) {
        var bankNameInput by remember { mutableStateOf(creator.bankName) }
        var accountInput by remember { mutableStateOf(creator.bankAccountNumber) }
        var routingInput by remember { mutableStateOf(creator.bankRouting) }

        AlertDialog(
            onDismissRequest = { showBankEditDialog = false },
            title = { Text("Bank Account Settings", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bankNameInput,
                        onValueChange = { bankNameInput = it },
                        label = { Text("Bank Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = BorderDark)
                    )
                    OutlinedTextField(
                        value = accountInput,
                        onValueChange = { accountInput = it },
                        label = { Text("Account Number") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = BorderDark)
                    )
                    OutlinedTextField(
                        value = routingInput,
                        onValueChange = { routingInput = it },
                        label = { Text("Routing Number / SWIFT") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = BorderDark)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateBankDetails(bankNameInput, accountInput, routingInput)
                        showBankEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Save", color = Color(0xFF00363D))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBankEditDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Withdraw Dialog
    if (showWithdrawDialog) {
        var withdrawAmountText by remember { mutableStateOf("") }
        var withdrawError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("Direct Bank Withdrawal", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Transfer to: ${creator.bankName} (${creator.bankAccountNumber})", color = TextSecondary, fontSize = 13.sp)
                    Text("Available: $${String.format("%.2f", creator.balance)}", color = NeonCyan, fontSize = 13.sp)
                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it; withdrawError = null },
                        label = { Text("Amount ($ USD)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = BorderDark)
                    )
                    if (withdrawError != null) {
                        Text(text = withdrawError ?: "", color = CubeRose, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = withdrawAmountText.toDoubleOrNull()
                        if (amt == null || amt <= 0.0) {
                            withdrawError = "Invalid amount"
                        } else {
                            viewModel.requestWithdrawal(
                                amount = amt,
                                onSuccess = { showWithdrawDialog = false },
                                onError = { withdrawError = it }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Withdraw", color = Color(0xFF00363D))
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
