package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Cube
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CubeGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RainbowGradients
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CubeVisualBox(
    cube: Cube,
    modifier: Modifier = Modifier,
    heightDp: Int = 140
) {
    val accentColor = try {
        Color(android.graphics.Color.parseColor(cube.colorHex))
    } catch (e: Exception) {
        NeonCyan
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Border Brush setup
    val borderBrush: Brush = when (cube.borderStyle) {
        "GLOWING" -> Brush.sweepGradient(listOf(NeonCyan, Color(0xFF38BDF8), NeonCyan))
        "GOLDEN" -> Brush.linearGradient(listOf(Color(0xFFFFDF00), CubeGold, Color(0xFFFDE68A)))
        "RAINBOW" -> Brush.linearGradient(RainbowGradients)
        "NEON" -> Brush.sweepGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFF8B5CF6)))
        else -> Brush.linearGradient(listOf(accentColor.copy(alpha = 0.5f), accentColor.copy(alpha = 0.15f)))
    }
    val borderWidth = when (cube.borderStyle) {
        "DEFAULT" -> 1.dp
        else -> 2.dp
    }

    // Background Brush setup
    val backgroundBrush: Brush = when (cube.backgroundStyle) {
        "GRADIENT_CYBER" -> Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF00384D), Color(0xFF2E1065)))
        "GRADIENT_SUNSET" -> Brush.linearGradient(listOf(Color(0xFF1E1B4B), Color(0xFF701A75), Color(0xFF831843)))
        "GRADIENT_AURORA" -> Brush.linearGradient(listOf(Color(0xFF064E3B), Color(0xFF065F46), Color(0xFF0F172A)))
        "GRADIENT_SPACE" -> Brush.radialGradient(listOf(Color(0xFF312E81), Color(0xFF1E1B4B), BackgroundDark))
        else -> Brush.verticalGradient(listOf(Color(0xFF111827), Color(0xFF0F172A)))
    }

    val cornerRadius = 16.dp

    Box(
        modifier = modifier
            .height(heightDp.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundBrush)
            .border(borderWidth, borderBrush, RoundedCornerShape(cornerRadius))
    ) {
        // Decorative Canvas: Isometric Cube and Grid / Pattern
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val w = size.width
            val h = size.height

            // Background pattern if selected
            if (cube.backgroundStyle == "PATTERN_GRID" || cube.backgroundStyle == "PATTERN_CARBON") {
                val step = 20.dp.toPx()
                var x = 0f
                while (x < w) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1f
                    )
                    x += step
                }
                var y = 0f
                while (y < h) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                    y += step
                }
            } else if (cube.backgroundStyle == "PATTERN_DOTS") {
                val step = 16.dp.toPx()
                var px = step / 2
                while (px < w) {
                    var py = step / 2
                    while (py < h) {
                        drawCircle(
                            color = accentColor.copy(alpha = 0.12f),
                            radius = 1.5.dp.toPx(),
                            center = Offset(px, py)
                        )
                        py += step
                    }
                    px += step
                }
            }

            // Draw center 3D Isometric Cube
            drawIsometricCube(
                centerX = w / 2f,
                centerY = h / 2f,
                cubeSize = 28.dp.toPx(),
                accent = accentColor,
                pulse = if (cube.effectStyle == "NEON_PULSE") pulseAlpha else 1.0f
            )

            // Sparkles or Seasonal details
            if (cube.effectStyle == "SPARKLE" || cube.effectStyle == "SEASONAL_FESTIVE") {
                drawSparkle(this, Offset(w * 0.2f, h * 0.25f), 5.dp.toPx(), Color.White.copy(alpha = pulseAlpha))
                drawSparkle(this, Offset(w * 0.8f, h * 0.35f), 7.dp.toPx(), CubeGold.copy(alpha = pulseAlpha))
                drawSparkle(this, Offset(w * 0.75f, h * 0.75f), 4.dp.toPx(), NeonCyan.copy(alpha = pulseAlpha))
            }
        }

        // Overlay Badge for Seasonal / Special Effect
        val effectLabel = when (cube.effectStyle) {
            "SPARKLE" -> "✨ Sparkle"
            "NEON_PULSE" -> "⚡ Neon Pulse"
            "DEEP_SHADOW" -> "🧊 3D Hologram"
            "SEASONAL_WINTER" -> "❄️ Winter Edition"
            "SEASONAL_CHERRY" -> "🌸 Sakura Pack"
            "SEASONAL_FESTIVE" -> "🎆 Festive Gold"
            else -> null
        }

        if (effectLabel != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = effectLabel,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun DrawScope.drawIsometricCube(
    centerX: Float,
    centerY: Float,
    cubeSize: Float,
    accent: Color,
    pulse: Float
) {
    val r = cubeSize
    val h = cubeSize * 0.58f

    // Top Face
    val topPath = Path().apply {
        moveTo(centerX, centerY - r)
        lineTo(centerX + r * 0.866f, centerY - h / 2f)
        lineTo(centerX, centerY)
        lineTo(centerX - r * 0.866f, centerY - h / 2f)
        close()
    }
    drawPath(topPath, color = accent.copy(alpha = 0.85f * pulse))
    drawPath(topPath, color = Color.White.copy(alpha = 0.5f), style = Stroke(width = 1.5.dp.toPx()))

    // Left Face
    val leftPath = Path().apply {
        moveTo(centerX - r * 0.866f, centerY - h / 2f)
        lineTo(centerX, centerY)
        lineTo(centerX, centerY + r)
        lineTo(centerX - r * 0.866f, centerY + h / 2f)
        close()
    }
    drawPath(leftPath, color = accent.copy(alpha = 0.55f * pulse))
    drawPath(leftPath, color = Color.White.copy(alpha = 0.3f), style = Stroke(width = 1.dp.toPx()))

    // Right Face
    val rightPath = Path().apply {
        moveTo(centerX, centerY)
        lineTo(centerX + r * 0.866f, centerY - h / 2f)
        lineTo(centerX + r * 0.866f, centerY + h / 2f)
        lineTo(centerX, centerY + r)
        close()
    }
    drawPath(rightPath, color = accent.copy(alpha = 0.35f * pulse))
    drawPath(rightPath, color = Color.White.copy(alpha = 0.3f), style = Stroke(width = 1.dp.toPx()))
}

private fun drawSparkle(drawScope: DrawScope, center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        quadraticTo(center.x, center.y, center.x + size, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + size)
        quadraticTo(center.x, center.y, center.x - size, center.y)
        quadraticTo(center.x, center.y, center.x, center.y - size)
        close()
    }
    drawScope.drawPath(path, color = color)
}
