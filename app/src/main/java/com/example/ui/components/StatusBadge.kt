package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CubeEmerald
import com.example.ui.theme.CubeGold
import com.example.ui.theme.CubeRose
import com.example.ui.theme.NeonCyan

@Composable
fun StatusBadge(
    status: String,
    isExpired: Boolean,
    modifier: Modifier = Modifier
) {
    val (label, bg, stroke, textCol) = when {
        isExpired -> Quad("EXPIRED", Color(0x22F43F5E), CubeRose, CubeRose)
        status == "PUBLISHED" -> Quad("PUBLISHED", Color(0x2210B981), CubeEmerald, CubeEmerald)
        status == "PAUSED" -> Quad("PAUSED", Color(0x22F59E0B), CubeGold, CubeGold)
        status == "DRAFT" -> Quad("DRAFT", Color(0x2238BDF8), NeonCyan, NeonCyan)
        else -> Quad(status, Color(0x2294A3B8), Color(0xFF64748B), Color(0xFF94A3B8))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bg)
            .border(1.dp, stroke.copy(alpha = 0.5f), RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(stroke)
            )
            Text(
                text = " $label",
                color = textCol,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
