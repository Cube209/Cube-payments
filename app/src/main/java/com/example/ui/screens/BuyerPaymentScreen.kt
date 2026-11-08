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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Creator
import com.example.data.model.Cube
import com.example.data.model.Currencies
import com.example.ui.CubePaymentViewModel
import com.example.ui.components.CubeVisualBox
import com.example.ui.components.StatusBadge
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

@Composable
fun BuyerPaymentScreen(
    cubeId: String,
    viewModel: CubePaymentViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var cube by remember { mutableStateOf<Cube?>(null) }
    var creator by remember { mutableStateOf<Creator?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Checkout Form state
    var buyerName by remember { mutableStateOf("") }
    var buyerEmail by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("Credit Card") } // "Credit Card", "Google Pay"
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(cubeId) {
        isLoading = true
        val c = viewModel.repository.getCubeOnce(cubeId)
        cube = c
        if (c != null) {
            creator = viewModel.repository.getCreatorOnce(c.creatorId)
        }
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NeonCyan)
        }
        return
    }

    val loadedCube = cube
    if (loadedCube == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = CubeRose, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cube Link Not Found", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "The link you opened does not match an existing published Cube.",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.navigateBack() },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("Return Home", color = Color(0xFF00363D))
            }
        }
        return
    }

    val isExpired = loadedCube.isExpired
    val isPaused = loadedCube.status == "PAUSED"
    val isDraft = loadedCube.status == "DRAFT"
    val canPurchase = loadedCube.canReceivePurchases && !isPaused && !isDraft

    val formattedPrice = Currencies.format(loadedCube.price, loadedCube.currency)

    val creatorNameColor = try {
        if (creator?.hasUsernameColorPack == true && creator?.usernameColor?.isNotBlank() == true) {
            Color(android.graphics.Color.parseColor(creator?.usernameColor))
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
        verticalArrangement = Arrangement.spacedBy(18.dp)
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

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color(0xFF00363D))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Direct Checkout • 0% Commission", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section 7: Buyer payment page displays
        // Cube name, picture, description, price, currency, creator information, purchase button

        // Visual Cube Box with custom styling & animations
        CubeVisualBox(cube = loadedCube, heightDp = 150, modifier = Modifier.fillMaxWidth())

        // Product Details Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(status = loadedCube.status, isExpired = isExpired)

                    Text(
                        text = formattedPrice,
                        color = NeonCyan,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    text = loadedCube.name,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                if (loadedCube.description.isNotBlank()) {
                    Text(
                        text = loadedCube.description,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                // Creator Information
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariantDark)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F172A))
                                .border(1.dp, creatorNameColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = creator?.username?.take(1)?.uppercase() ?: "C",
                                color = creatorNameColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "@${creator?.username ?: "creator"}",
                                    color = creatorNameColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (creator?.isEmailVerified == true) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified Creator",
                                        tint = CubeEmerald,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .padding(start = 4.dp)
                                    )
                                }
                            }
                            Text(
                                text = creator?.bio?.ifBlank { "Verified Cube PAYMENT creator" } ?: "Verified Creator",
                                color = TextTertiary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Expiration Warning Banner if expired
        if (isExpired) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x33F43F5E))
                    .border(1.dp, CubeRose, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = CubeRose, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "This Cube has Expired",
                            color = CubeRose,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Per platform policy, expired links remain visible for reference but can no longer receive purchases.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else if (isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x33F59E0B))
                    .border(1.dp, CubeGold, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = CubeGold, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Cube Currently Paused",
                            color = CubeGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "The creator has temporarily paused purchases for this Cube.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Buyer Checkout Form (No account required!)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Instant Checkout",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "No account needed",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (errorMessage != null) {
                    Text(text = errorMessage ?: "", color = CubeRose, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = buyerName,
                    onValueChange = { buyerName = it; errorMessage = null },
                    label = { Text("Your Full Name *") },
                    placeholder = { Text("e.g. Jordan Smith") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TextSecondary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("buyer_name_input"),
                    singleLine = true,
                    enabled = canPurchase,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = buyerEmail,
                    onValueChange = { buyerEmail = it; errorMessage = null },
                    label = { Text("Email (Receipt delivered here) *") },
                    placeholder = { Text("jordan@example.com") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = TextSecondary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("buyer_email_input"),
                    singleLine = true,
                    enabled = canPurchase,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick test card autofill
                if (canPurchase) {
                    TextButton(
                        onClick = {
                            buyerName = "Jordan Smith"
                            buyerEmail = "jordan.smith@gmail.com"
                            cardNumber = "4242 •••• •••• 4242"
                            expiry = "12/28"
                            cvc = "382"
                            errorMessage = null
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Auto-fill Test Card", color = NeonCyan, fontSize = 12.sp)
                    }
                }

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("Card Number") },
                    placeholder = { Text("4242 •••• •••• 4242") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, tint = TextSecondary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = canPurchase,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = { expiry = it },
                        label = { Text("MM/YY") },
                        placeholder = { Text("12/28") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        enabled = canPurchase,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderDark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = cvc,
                        onValueChange = { cvc = it },
                        label = { Text("CVC") },
                        placeholder = { Text("123") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        enabled = canPurchase,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderDark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Zero Commission Transparency Notice (Section 8)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceVariantDark)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", color = TextSecondary, fontSize = 12.sp)
                            Text(formattedPrice, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Cube PAYMENT Commission", color = CubeEmerald, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("0% ($0.00)", color = CubeEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Purchase Button (Section 7 & 8)
                Button(
                    onClick = {
                        if (!canPurchase) return@Button
                        if (buyerName.isBlank()) {
                            errorMessage = "Please enter your name."
                            return@Button
                        }
                        if (buyerEmail.isBlank() || !buyerEmail.contains("@")) {
                            errorMessage = "Please enter a valid email for your receipt."
                            return@Button
                        }

                        isProcessing = true
                        viewModel.processBuyerPayment(
                            cubeId = loadedCube.id,
                            buyerName = buyerName,
                            buyerEmail = buyerEmail,
                            paymentMethod = selectedMethod,
                            onSuccess = { isProcessing = false },
                            onError = {
                                isProcessing = false
                                errorMessage = it
                            }
                        )
                    },
                    enabled = canPurchase && !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("purchase_cube_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        disabledContainerColor = SurfaceVariantDark
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF00363D))
                    } else {
                        Text(
                            text = if (canPurchase) "Pay $formattedPrice" else "Unavailable (Expired or Paused)",
                            color = if (canPurchase) Color(0xFF00363D) else TextTertiary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
