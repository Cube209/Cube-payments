package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Cube
import com.example.data.model.Currencies
import com.example.data.model.DecorationOptions
import com.example.ui.CubePaymentViewModel
import com.example.ui.Screen
import com.example.ui.components.CubeVisualBox
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CubeGold
import com.example.ui.theme.CubeRose
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateCubeScreen(
    viewModel: CubePaymentViewModel,
    existingCubeId: String? = null,
    modifier: Modifier = Modifier
) {
    val currentCreator by viewModel.currentCreator.collectAsState()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var currencyMenuExpanded by remember { mutableStateOf(false) }
    var selectedColorHex by remember { mutableStateOf("#00F0FF") }

    // Expiration options
    var expirationType by remember { mutableStateOf("NEVER") } // "NEVER" or "CUSTOM"
    var customDurationOption by remember { mutableStateOf("7_DAYS") } // "24_HOURS", "3_DAYS", "7_DAYS", "30_DAYS"

    // Decorations
    var borderStyle by remember { mutableStateOf("DEFAULT") }
    var backgroundStyle by remember { mutableStateOf("SOLID") }
    var fontStyle by remember { mutableStateOf("DEFAULT") }
    var effectStyle by remember { mutableStateOf("NONE") }
    var themeStyle by remember { mutableStateOf("CYBERPUNK") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isExpiredExisting by remember { mutableStateOf(false) }

    val hasDecorationPack = currentCreator?.hasDecorationPack == true

    // Load existing cube if editing
    LaunchedEffect(existingCubeId) {
        if (existingCubeId != null) {
            val existing = viewModel.repository.getCubeOnce(existingCubeId)
            if (existing != null) {
                name = existing.name
                description = existing.description
                priceText = existing.price.toString()
                selectedCurrency = existing.currency
                selectedColorHex = existing.colorHex
                expirationType = existing.expirationType
                borderStyle = existing.borderStyle
                backgroundStyle = existing.backgroundStyle
                fontStyle = existing.fontStyle
                effectStyle = existing.effectStyle
                themeStyle = existing.themeStyle
                isExpiredExisting = existing.isExpired
            }
        }
    }

    // Temporary preview cube object
    val previewCube = remember(
        name, description, priceText, selectedCurrency, selectedColorHex,
        borderStyle, backgroundStyle, fontStyle, effectStyle, themeStyle
    ) {
        Cube(
            id = existingCubeId ?: "preview",
            creatorId = currentCreator?.id ?: "creator",
            name = name.ifBlank { "Sample Cube" },
            description = description.ifBlank { "Custom digital product with instant checkout." },
            colorHex = selectedColorHex,
            price = priceText.toDoubleOrNull() ?: 25.0,
            currency = selectedCurrency,
            status = "PUBLISHED",
            borderStyle = borderStyle,
            backgroundStyle = backgroundStyle,
            fontStyle = fontStyle,
            effectStyle = effectStyle,
            themeStyle = themeStyle
        )
    }

    fun computeExpirationTimestamp(): Long {
        if (expirationType == "NEVER") return 0L
        val now = System.currentTimeMillis()
        val durationMillis = when (customDurationOption) {
            "24_HOURS" -> 86400000L
            "3_DAYS" -> 86400000L * 3
            "7_DAYS" -> 86400000L * 7
            "30_DAYS" -> 86400000L * 30
            else -> 86400000L * 7
        }
        return now + durationMillis
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
            Text(
                text = if (existingCubeId != null) "Edit Cube" else "Create a Cube",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Live Visual Preview
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "LIVE CUBE PREVIEW",
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            CubeVisualBox(cube = previewCube, heightDp = 130, modifier = Modifier.fillMaxWidth())
        }

        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33F43F5E))
                    .border(1.dp, CubeRose.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = CubeRose,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (isExpiredExisting) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33F59E0B))
                    .border(1.dp, CubeGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "⚠️ Notice: This Cube is expired. Per platform rules, expired Cubes cannot be extended or receive new payments.",
                    color = CubeGold,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Core Configuration Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Product Details",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                // Cube Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Cube Name *") },
                    placeholder = { Text("e.g. Masterclass, Digital Template, 1-on-1 Session") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cube_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; errorMessage = null },
                    label = { Text("Description") },
                    placeholder = { Text("Describe what the buyer receives upon payment.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cube_desc_input"),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Price and Currency (Section 4 & 11)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it; errorMessage = null },
                        label = { Text("Price *") },
                        placeholder = { Text("25.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("cube_price_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderDark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Currency Selector Dropdown
                    ExposedDropdownMenuBox(
                        expanded = currencyMenuExpanded,
                        onExpandedChange = { currencyMenuExpanded = !currencyMenuExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedCurrency,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Currency") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyMenuExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("currency_selector"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderDark
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = currencyMenuExpanded,
                            onDismissRequest = { currencyMenuExpanded = false },
                            modifier = Modifier.background(SurfaceDark)
                        ) {
                            Currencies.ALL.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${item.flag} ${item.code} (${item.symbol})", color = TextPrimary)
                                    },
                                    onClick = {
                                        selectedCurrency = item.code
                                        currencyMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Cube Accent Colour (Section 4)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Cube Colour",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DecorationOptions.CUBE_ACCENT_COLORS.forEach { hex ->
                            val isChosen = selectedColorHex.equals(hex, ignoreCase = true)
                            val col = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { NeonCyan }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(col)
                                    .border(
                                        if (isChosen) 3.dp else 1.dp,
                                        if (isChosen) Color.White else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { selectedColorHex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChosen) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (hex == "#00F0FF") Color.Black else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Cube Expiration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Expiration Policy",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Choose whether this Cube remains available indefinitely or auto-expires. Expired Cubes cannot receive purchases, but links remain accessible.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                // Option A: Never Expires
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { expirationType = "NEVER" }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = expirationType == "NEVER",
                        onClick = { expirationType = "NEVER" },
                        colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Never expires (Permanent link)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Link accepts purchases until paused or deleted", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                // Option B: Custom Expiration
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { expirationType = "CUSTOM" }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = expirationType == "CUSTOM",
                        onClick = { expirationType = "CUSTOM" },
                        colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Custom expiration duration", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Auto-closes payment gate when duration expires", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                if (expirationType == "CUSTOM") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        DurationChip(
                            label = "24 Hours",
                            selected = customDurationOption == "24_HOURS"
                        ) { customDurationOption = "24_HOURS" }

                        DurationChip(
                            label = "3 Days",
                            selected = customDurationOption == "3_DAYS"
                        ) { customDurationOption = "3_DAYS" }

                        DurationChip(
                            label = "7 Days",
                            selected = customDurationOption == "7_DAYS"
                        ) { customDurationOption = "7_DAYS" }

                        DurationChip(
                            label = "30 Days",
                            selected = customDurationOption == "30_DAYS"
                        ) { customDurationOption = "30_DAYS" }
                    }
                }
            }
        }

        // Section 10: Cube Decoration Pack Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(
                    1.dp,
                    if (hasDecorationPack) CubeGold.copy(alpha = 0.5f) else BorderDark,
                    RoundedCornerShape(18.dp)
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CubeGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cube Decoration Pack",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (hasDecorationPack) {
                        Text(
                            text = "UNLOCKED ⭐",
                            color = CubeGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF2E1065))
                                .clickable { viewModel.navigateTo(Screen.Store) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Unlock ($100)",
                                color = CubeGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (!hasDecorationPack) {
                    Text(
                        text = "The Complete Decoration Pack unlocks animated backgrounds, glowing/gold/rainbow borders, neon effects, 3D appearances, and seasonal packs in one purchase.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                } else {
                    // Border Style selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Border Style", color = TextSecondary, fontSize = 12.sp)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DecorationOptions.BORDER_STYLES.forEach { (key, label) ->
                                DecorationChip(
                                    label = label,
                                    selected = borderStyle == key
                                ) { borderStyle = key }
                            }
                        }
                    }

                    // Background Style selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Background & Patterns", color = TextSecondary, fontSize = 12.sp)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DecorationOptions.BACKGROUND_STYLES.forEach { (key, label) ->
                                DecorationChip(
                                    label = label,
                                    selected = backgroundStyle == key
                                ) { backgroundStyle = key }
                            }
                        }
                    }

                    // Special Effects
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Special Effects & Seasonal Themes", color = TextSecondary, fontSize = 12.sp)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DecorationOptions.EFFECT_STYLES.forEach { (key, label) ->
                                DecorationChip(
                                    label = label,
                                    selected = effectStyle == key
                                ) { effectStyle = key }
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons: Save as Draft vs Publish (Section 4)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    viewModel.saveCube(
                        id = existingCubeId,
                        name = name,
                        description = description,
                        colorHex = selectedColorHex,
                        price = price,
                        currency = selectedCurrency,
                        isDraft = true,
                        expirationType = expirationType,
                        expiresAt = computeExpirationTimestamp(),
                        borderStyle = borderStyle,
                        backgroundStyle = backgroundStyle,
                        fontStyle = fontStyle,
                        effectStyle = effectStyle,
                        themeStyle = themeStyle,
                        onSuccess = {},
                        onError = { errorMessage = it }
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("save_draft_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save as Draft", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull()
                    if (name.isBlank()) {
                        errorMessage = "Please enter a Cube name."
                        return@Button
                    }
                    if (price == null || price <= 0.0) {
                        errorMessage = "Please enter a valid price."
                        return@Button
                    }

                    viewModel.saveCube(
                        id = existingCubeId,
                        name = name,
                        description = description,
                        colorHex = selectedColorHex,
                        price = price,
                        currency = selectedCurrency,
                        isDraft = false,
                        expirationType = expirationType,
                        expiresAt = computeExpirationTimestamp(),
                        borderStyle = borderStyle,
                        backgroundStyle = backgroundStyle,
                        fontStyle = fontStyle,
                        effectStyle = effectStyle,
                        themeStyle = themeStyle,
                        onSuccess = {},
                        onError = { errorMessage = it }
                    )
                },
                modifier = Modifier
                    .weight(1.3f)
                    .height(52.dp)
                    .testTag("publish_cube_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Publish Cube", color = Color(0xFF00363D), fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun DurationChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) NeonCyan else SurfaceVariantDark)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color(0xFF00363D) else TextSecondary,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun DecorationChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) CubeGold else SurfaceVariantDark)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color(0xFF432C00) else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
