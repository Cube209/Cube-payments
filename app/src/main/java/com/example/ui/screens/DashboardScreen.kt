package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Cube
import com.example.data.model.Currencies
import com.example.ui.CubeFilter
import com.example.ui.CubePaymentViewModel
import com.example.ui.Screen
import com.example.ui.components.CubeCard
import com.example.ui.components.SlotMeter
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CubeEmerald
import com.example.ui.theme.CubeGold
import com.example.ui.theme.CubeRose
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun DashboardScreen(
    viewModel: CubePaymentViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentCreator by viewModel.currentCreator.collectAsState()
    val allCubes by viewModel.cubes.collectAsState()
    val cubeCount by viewModel.cubeCount.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var showWithdrawalDialog by remember { mutableStateOf(false) }
    var cubeToDelete by remember { mutableStateOf<Cube?>(null) }

    // Filter cubes
    val filteredCubes = remember(allCubes, filter) {
        when (filter) {
            CubeFilter.ALL -> allCubes
            CubeFilter.PUBLISHED -> allCubes.filter { it.status == "PUBLISHED" && !it.isExpired }
            CubeFilter.DRAFTS -> allCubes.filter { it.status == "DRAFT" }
            CubeFilter.PAUSED -> allCubes.filter { it.status == "PAUSED" }
            CubeFilter.EXPIRED -> allCubes.filter { it.isExpired }
        }
    }

    val usernameColor = try {
        if (currentCreator?.hasUsernameColorPack == true && currentCreator?.usernameColor?.isNotBlank() == true) {
            Color(android.graphics.Color.parseColor(currentCreator?.usernameColor))
        } else {
            NeonCyan
        }
    } catch (e: Exception) {
        NeonCyan
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (cubeCount >= 50) {
                        viewModel.showMessage("Maximum limit of 50 Cubes reached! Delete an unused Cube to free up a slot.")
                    } else {
                        viewModel.navigateTo(Screen.CreateCube())
                    }
                },
                containerColor = NeonCyan,
                contentColor = Color(0xFF00363D),
                modifier = Modifier.testTag("create_cube_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Create Cube")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Cube", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = BackgroundDark,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { viewModel.navigateTo(Screen.Profile) }
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariantDark)
                                .border(1.5.dp, usernameColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentCreator?.username?.take(1)?.uppercase() ?: "C",
                                color = usernameColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "@${currentCreator?.username ?: "creator"}",
                                    color = usernameColor,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (currentCreator?.hasDecorationPack == true) {
                                    Text(text = " ⭐", fontSize = 14.sp)
                                }
                            }
                            Text(
                                text = "Creator Dashboard",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Store) },
                            modifier = Modifier.testTag("dashboard_store_icon")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "In-App Store",
                                tint = CubeGold
                            )
                        }

                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.SecurityCenter) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security Center",
                                tint = TextSecondary
                            )
                        }
                    }
                }
            }

            // Stats Cards: Revenue & Bank Balance
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Balance card with direct withdrawal
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF00384D), SurfaceDark)
                                )
                            )
                            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "AVAILABLE BALANCE",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )

                            Text(
                                text = String.format("$%.2f", currentCreator?.balance ?: 0.0),
                                color = NeonCyan,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonCyan)
                                    .clickable { showWithdrawalDialog = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("withdraw_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = Color(0xFF00363D),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Withdraw",
                                        color = Color(0xFF00363D),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Total sales count
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurfaceDark)
                            .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "TOTAL SALES",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )

                            Text(
                                text = "${transactions.size}",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )

                            Text(
                                text = "0% Platform fee",
                                color = CubeEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Section 3 & 12: Account Limits - Slot Meter (Max 50 Cubes)
            item {
                SlotMeter(cubeCount = cubeCount, maxLimit = 50)
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CubeFilter.values()) { f ->
                        val isSelected = filter == f
                        val label = when (f) {
                            CubeFilter.ALL -> "All (${allCubes.size})"
                            CubeFilter.PUBLISHED -> "Published (${allCubes.count { it.status == "PUBLISHED" && !it.isExpired }})"
                            CubeFilter.DRAFTS -> "Drafts (${allCubes.count { it.status == "DRAFT" }})"
                            CubeFilter.PAUSED -> "Paused (${allCubes.count { it.status == "PAUSED" }})"
                            CubeFilter.EXPIRED -> "Expired (${allCubes.count { it.isExpired }})"
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setFilter(f) },
                            label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan,
                                selectedLabelColor = Color(0xFF00363D),
                                containerColor = SurfaceVariantDark,
                                labelColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(100.dp)
                        )
                    }
                }
            }

            // Cube Cards
            if (filteredCubes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurfaceDark)
                            .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "No Cubes in this category",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Create a new Cube to start selling and receiving direct payments.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredCubes, key = { it.id }) { cube ->
                    CubeCard(
                        cube = cube,
                        onCopyLink = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Cube Payment Link", cube.shareableUrl)
                            clipboard.setPrimaryClip(clip)
                            viewModel.showMessage("Copied link to clipboard: ${cube.shareableUrl}")
                        },
                        onShareLink = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Pay for ${cube.name}: ${cube.shareableUrl}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Cube Link"))
                        },
                        onTogglePause = {
                            viewModel.togglePauseCube(cube)
                        },
                        onDelete = {
                            cubeToDelete = cube
                        },
                        onOpenBuyerPage = {
                            viewModel.navigateTo(Screen.BuyerPayment(cube.id))
                        }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog (Frees up slot!)
    if (cubeToDelete != null) {
        val cube = cubeToDelete!!
        AlertDialog(
            onDismissRequest = { cubeToDelete = null },
            title = { Text("Delete Cube?", color = TextPrimary) },
            text = {
                Text(
                    "Are you sure you want to delete '${cube.name}'? This will free up 1 of your 50 capacity slots. Existing buyers will no longer be able to access it.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCube(cube)
                        cubeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CubeRose)
                ) {
                    Text("Delete (Free Slot)", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { cubeToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Withdrawal Dialog
    if (showWithdrawalDialog) {
        var withdrawAmountText by remember { mutableStateOf("") }
        var withdrawError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showWithdrawalDialog = false },
            title = {
                Text("Direct Bank Withdrawal", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Transfer earnings directly to your verified bank account with zero Cube PAYMENT commission.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceVariantDark)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Payout Destination:",
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${currentCreator?.bankName} (${currentCreator?.bankAccountNumber})",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Available: $${String.format("%.2f", currentCreator?.balance ?: 0.0)}",
                                color = NeonCyan,
                                fontSize = 12.sp
                            )
                        }
                    }

                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it; withdrawError = null },
                        label = { Text("Amount ($ USD)") },
                        placeholder = { Text("e.g. 50.00") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderDark
                        ),
                        modifier = Modifier.fillMaxWidth()
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
                            withdrawError = "Please enter a valid amount."
                        } else {
                            viewModel.requestWithdrawal(
                                amount = amt,
                                onSuccess = { showWithdrawalDialog = false },
                                onError = { withdrawError = it }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Confirm Payout", color = Color(0xFF00363D), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawalDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
