package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Cube
import com.example.data.model.Currencies
import com.example.ui.theme.BorderDark
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
fun CubeCard(
    cube: Cube,
    onCopyLink: () -> Unit,
    onShareLink: () -> Unit,
    onTogglePause: () -> Unit,
    onDelete: () -> Unit,
    onOpenBuyerPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpired = cube.isExpired
    val formattedPrice = Currencies.format(cube.price, cube.currency)

    val expirationText = when {
        isExpired -> "Expired (No longer accepting payments)"
        cube.expirationType == "NEVER" -> "Never expires"
        cube.expiresAt > 0L -> {
            val sdf = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
            "Expires: ${sdf.format(Date(cube.expiresAt))}"
        }
        else -> "Never expires"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("cube_card_${cube.id}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header: Status, Expiration, and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = cube.status, isExpired = isExpired)

                Text(
                    text = formattedPrice,
                    color = NeonCyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Visual preview of Cube
            CubeVisualBox(
                cube = cube,
                heightDp = 110,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenBuyerPage() }
            )

            // Name and description
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = cube.name,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (cube.description.isNotBlank()) {
                    Text(
                        text = cube.description,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }

                Text(
                    text = expirationText,
                    color = if (isExpired) CubeRose else TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = if (isExpired) FontWeight.SemiBold else FontWeight.Normal
                )
            }

            // Link bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariantDark)
                    .clickable { onCopyLink() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = cube.shareableUrl,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy link",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Copy",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Buyer page preview button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF00384D))
                        .clickable { onOpenBuyerPage() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("preview_cube_${cube.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Buyer Checkout",
                            tint = NeonCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Checkout Page",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Right: Action buttons (Pause/Unpause, Share, Delete)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isExpired && cube.status != "DRAFT") {
                        IconButton(
                            onClick = onTogglePause,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (cube.status == "PAUSED") Icons.Default.PlayCircle else Icons.Default.PauseCircle,
                                contentDescription = if (cube.status == "PAUSED") "Unpause Cube" else "Pause Cube",
                                tint = if (cube.status == "PAUSED") CubeGold else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onShareLink,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Cube link",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp),
                        enabled = true
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Cube",
                            tint = CubeRose,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
