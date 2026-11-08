package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.CubePaymentViewModel
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
fun SecurityCenterScreen(
    viewModel: CubePaymentViewModel,
    modifier: Modifier = Modifier
) {
    val currentCreator by viewModel.currentCreator.collectAsState()
    val securityLogs by viewModel.securityLogs.collectAsState()
    val scrollState = rememberScrollState()

    var showPasswordDialog by remember { mutableStateOf(false) }

    val creator = currentCreator ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
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
            Spacer(modifier = Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = CubeEmerald)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Security Center",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Security Status Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, CubeEmerald.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x2210B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = CubeEmerald, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Security Health: Excellent",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Zero suspicious events detected. Bank payouts protected by cryptographic tokens.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section 13 Feature 1: Email Verification
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Mail, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Email Verification", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = if (creator.isEmailVerified) "Verified: ${creator.email}" else "Unverified: ${creator.email}",
                        color = if (creator.isEmailVerified) CubeEmerald else CubeGold,
                        fontSize = 12.sp
                    )
                }

                if (!creator.isEmailVerified) {
                    Button(
                        onClick = { viewModel.verifyEmail() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Verify Email", color = Color(0xFF00363D), fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CubeEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verified", color = CubeEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 13 Feature 2: Two-Factor Authentication (2FA)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Two-Factor Authentication (2FA)", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "Require biometric or authenticator code for login and withdrawals",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Switch(
                    checked = creator.isTwoFactorEnabled,
                    onCheckedChange = { viewModel.toggleTwoFactor(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00363D),
                        checkedTrackColor = NeonCyan
                    )
                )
            }
        }

        // Section 13 Feature 3: Password Recovery / Change
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Password & Recovery", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "Update login credential or setup recovery phrase",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { showPasswordDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Change", color = NeonCyan)
                }
            }
        }

        // Section 13 Feature 4: Login History Audit Log
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Login History & Activity",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (securityLogs.isEmpty()) {
                    Text("No login events recorded yet.", color = TextSecondary, fontSize = 13.sp)
                } else {
                    val sdf = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
                    securityLogs.take(5).forEach { log ->
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
                                        text = log.description,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${log.deviceInfo} • ${log.ipLocation}",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }

                                Text(
                                    text = sdf.format(Date(log.timestamp)),
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

    // Password Change Dialog
    if (showPasswordDialog) {
        var oldPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var passError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Change Password", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it; passError = null },
                        label = { Text("Current Password") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = BorderDark)
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it; passError = null },
                        label = { Text("New Password (min 6 characters)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = BorderDark)
                    )
                    if (passError != null) {
                        Text(text = passError ?: "", color = CubeRose, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.changePassword(
                            oldPass = oldPassword,
                            newPass = newPassword,
                            onSuccess = { showPasswordDialog = false },
                            onError = { passError = it }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Update", color = Color(0xFF00363D))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
