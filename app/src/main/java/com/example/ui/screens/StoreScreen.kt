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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Cube
import com.example.data.model.DecorationOptions
import com.example.ui.CubePaymentViewModel
import com.example.ui.components.CubeVisualBox
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CubeEmerald
import com.example.ui.theme.CubeGold
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoreScreen(
    viewModel: CubePaymentViewModel,
    modifier: Modifier = Modifier
) {
    val currentCreator by viewModel.currentCreator.collectAsState()
    val scrollState = rememberScrollState()

    val hasUsernameColor = currentCreator?.hasUsernameColorPack == true
    val hasDecorationPack = currentCreator?.hasDecorationPack == true

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = CubeGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "In-App Store",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Introduction Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF2E1065), Color(0xFF1E1B4B))
                    )
                )
                .border(1.dp, CubeGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Upgrade Your Creator Presence",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Enhance your profile brand and make your Cubes stand out with neon glow effects, 3D appearances, and rainbow prism styling.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Product 1: Complete Cube Decoration Pack ($100)
        // Section 10: "One purchase unlocks the entire decoration collection. No individual decoration purchases."
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(
                    2.dp,
                    if (hasDecorationPack) CubeEmerald else CubeGold,
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
                .testTag("store_decoration_pack_card")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = CubeGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Complete Cube Decoration Pack",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "One purchase unlocks all 14 visual upgrades",
                            color = CubeGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "$100",
                        color = CubeGold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Showcase of preview cube using decorations
                val demoCube = Cube(
                    id = "demo-preview",
                    creatorId = "creator",
                    name = "Ultimate Creator Masterclass",
                    description = "Showcase of animated neon, golden glow, and isometric 3D styling",
                    price = 99.00,
                    currency = "USD",
                    borderStyle = "GOLDEN",
                    backgroundStyle = "GRADIENT_CYBER",
                    fontStyle = "CYBER",
                    effectStyle = "SPARKLE"
                )
                CubeVisualBox(cube = demoCube, heightDp = 120, modifier = Modifier.fillMaxWidth())

                Text(
                    text = "Included Decorations (All unlocked together):",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                // 14 items listed in bullet items / grid
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val items = listOf(
                        "Gradient backgrounds",
                        "Animated backgrounds",
                        "Glowing borders",
                        "Golden borders",
                        "Rainbow borders",
                        "Special fonts",
                        "Patterns",
                        "Shadow effects",
                        "Sparkle effects",
                        "Custom icons",
                        "Premium themes",
                        "3D-style appearance",
                        "Neon effects",
                        "Seasonal decorations"
                    )
                    items.forEach { item ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceVariantDark)
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text("✓ $item", color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }

                if (hasDecorationPack) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x2210B981))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CubeEmerald)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("UNLOCKED ON YOUR ACCOUNT", color = CubeEmerald, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.purchaseStoreItem("DECORATION_PACK", "$100")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CubeGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("buy_decoration_pack_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Unlock Decoration Pack ($100)", color = Color(0xFF432C00), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        // Product 2: Username Colour ($10)
        // Section 9: "Username colour: $10"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(
                    2.dp,
                    if (hasUsernameColor) CubeEmerald else ElectricViolet,
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
                .testTag("store_username_color_card")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ColorLens, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Username Colour",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Custom color for your username on all Cubes & profile",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "$10",
                        color = ElectricViolet,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Interactive Color Palette
                if (hasUsernameColor) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Choose your active Username Colour:",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DecorationOptions.USERNAME_COLORS.forEach { (hex, name) ->
                                val isSelected = currentCreator?.usernameColor?.equals(hex, ignoreCase = true) == true
                                val col = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { NeonCyan }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(col)
                                        .border(
                                            if (isSelected) 3.dp else 1.dp,
                                            if (isSelected) Color.White else Color.Transparent,
                                            CircleShape
                                        )
                                        .clickable { viewModel.setUsernameColor(hex) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (hex == "#00F0FF") Color.Black else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        val activeColor = try {
                            Color(android.graphics.Color.parseColor(currentCreator?.usernameColor ?: "#00F0FF"))
                        } catch (e: Exception) { NeonCyan }

                        Text(
                            text = "Preview: @${currentCreator?.username ?: "creator"}",
                            color = activeColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.purchaseStoreItem("USERNAME_COLOR", "$10")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("buy_username_color_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Unlock Username Colour ($10)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
