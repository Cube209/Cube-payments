package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.CubePaymentViewModel
import com.example.ui.Screen
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

@Composable
fun HomeScreen(
    viewModel: CubePaymentViewModel,
    modifier: Modifier = Modifier
) {
    val currentCreator by viewModel.currentCreator.collectAsState()
    var directLinkInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // App Header Brand
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_cube_logo),
                        contentDescription = "Cube PAYMENT Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Cube",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = " PAYMENT",
                            color = NeonCyan,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "0% Platform Commission",
                        color = CubeEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Quick Store Icon
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceVariantDark)
                    .clickable { viewModel.navigateTo(Screen.Store) }
                    .padding(10.dp)
                    .testTag("home_store_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "In-App Store",
                    tint = CubeGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Hero Banner Illustration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, BorderDark, RoundedCornerShape(22.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.banner_hero_cubes),
                contentDescription = "Cube Payment Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Subtle gradient overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xCC080C15))
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Monetize effortlessly with custom Cubes",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Short App Introduction (Section 1)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Create a Cube. Set a price. Share the link.",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 28.sp
            )
            Text(
                text = "Cube PAYMENT is a frictionless platform where creators receive 100% of the sale minus standard processing fees. Buyers can purchase immediately across any device without creating an account.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        // Feature Highlights
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FeaturePill(
                icon = Icons.Default.Bolt,
                title = "0% Platform Fee",
                desc = "Keep 100% net",
                color = NeonCyan,
                modifier = Modifier.weight(1f)
            )
            FeaturePill(
                icon = Icons.Default.Link,
                title = "Instant Links",
                desc = "No buyer sign-up",
                color = ElectricViolet,
                modifier = Modifier.weight(1f)
            )
            FeaturePill(
                icon = Icons.Default.CreditCard,
                title = "Multi-Currency",
                desc = "Global payouts",
                color = CubeGold,
                modifier = Modifier.weight(1f)
            )
        }

        // Primary Action Cards: Dashboard & Authentication
        if (currentCreator != null) {
            // Already signed in
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceDark)
                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Signed in as Creator",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "@${currentCreator?.username}",
                                color = NeonCyan,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.navigateTo(Screen.Dashboard) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("go_to_dashboard_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dashboard,
                                contentDescription = null,
                                tint = Color(0xFF00363D),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dashboard",
                                color = Color(0xFF00363D),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.Profile) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Profile & Bank", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.Store) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CubeGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("In-App Store", fontSize = 13.sp)
                        }
                    }
                }
            }
        } else {
            // Not signed in
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(Screen.Auth(isSignUp = false)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("home_sign_in_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF00363D)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign in to Creator Dashboard",
                        color = Color(0xFF00363D),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(Screen.Auth(isSignUp = true)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("home_create_account_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(NeonCyan, ElectricViolet)))
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Creator Account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Section 6 & 7: Buyer Link Tester Simulator
        // "Buyers can open links without creating an account"
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
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Buyer Link Simulator",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Experience what buyers see when opening any Cube link. No account required.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = directLinkInput,
                        onValueChange = { directLinkInput = it },
                        placeholder = { Text("e.g. cb-3d-masterclass", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("buyer_link_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderDark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val id = directLinkInput.trim()
                                .removePrefix("https://cubepay.me/c/")
                                .removePrefix("cubepay.me/c/")
                            if (id.isNotBlank()) {
                                viewModel.navigateTo(Screen.BuyerPayment(id))
                            } else {
                                // Default to sample cube 1
                                viewModel.navigateTo(Screen.BuyerPayment("cb-3d-masterclass"))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("open_buyer_link_button")
                    ) {
                        Text("Open", fontWeight = FontWeight.Bold)
                    }
                }

                // Quick buttons for demo cubes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DemoLinkPill(label = "3D Masterclass ($49)", cubeId = "cb-3d-masterclass") {
                        viewModel.navigateTo(Screen.BuyerPayment("cb-3d-masterclass"))
                    }
                    DemoLinkPill(label = "Dev Consult ($150)", cubeId = "cb-dev-consult") {
                        viewModel.navigateTo(Screen.BuyerPayment("cb-dev-consult"))
                    }
                }
            }
        }

        // In-App Store Banner Teaser (Section 9 & 10)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF2E1065), Color(0xFF1E1B4B), SurfaceDark)
                    )
                )
                .border(1.dp, CubeGold.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .clickable { viewModel.navigateTo(Screen.Store) }
                .padding(18.dp)
                .testTag("home_store_banner")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⭐ In-App Store",
                            color = CubeGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Complete Cube Decoration Pack ($100) & Custom Username Colours ($10)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open Store",
                    tint = CubeGold,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun FeaturePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun DemoLinkPill(
    label: String,
    cubeId: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceVariantDark)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = NeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
