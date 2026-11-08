package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Currencies
import com.example.data.model.Transaction
import com.example.ui.CubePaymentViewModel
import com.example.ui.Screen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CubeEmerald
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
fun PaymentReceiptScreen(
    transaction: Transaction,
    viewModel: CubePaymentViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
    val formattedDate = sdf.format(Date(transaction.createdAt))
    val formattedAmount = Currencies.format(transaction.amount, transaction.currency)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Success Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0x2210B981))
                .border(2.dp, CubeEmerald, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = CubeEmerald,
                modifier = Modifier.size(36.dp)
            )
        }

        Text(
            text = "Payment Confirmed!",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )

        Text(
            text = "Your transaction has been securely processed. An official receipt has been delivered to ${transaction.buyerEmail}.",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        // Receipt Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                .padding(20.dp)
                .testTag("payment_receipt_card")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ReceiptRow(label = "Cube Item", value = transaction.cubeName, isBold = true)
                ReceiptRow(label = "Amount Paid", value = formattedAmount, isNeon = true)
                ReceiptRow(label = "Transaction ID", value = transaction.id)
                ReceiptRow(label = "Buyer", value = transaction.buyerName)
                ReceiptRow(label = "Email", value = transaction.buyerEmail)
                ReceiptRow(label = "Payment Method", value = transaction.paymentMethod)
                ReceiptRow(label = "Timestamp", value = formattedDate)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BorderDark)
                )

                ReceiptRow(label = "Platform Commission", value = "0% ($0.00)", isEmerald = true)
                ReceiptRow(label = "Status", value = "COMPLETED ✓", isEmerald = true)
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Payment Receipt", "Receipt for ${transaction.cubeName}: $formattedAmount (ID: ${transaction.id})")
                    clipboard.setPrimaryClip(clip)
                    viewModel.showMessage("Receipt copied to clipboard!")
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Receipt", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Payment Receipt for ${transaction.cubeName}: $formattedAmount (Ref: ${transaction.id})")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share Receipt"))
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share", fontSize = 13.sp)
            }
        }

        Button(
            onClick = { viewModel.navigateTo(Screen.Home) },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("receipt_home_button"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = Color(0xFF00363D))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Back to Home", color = Color(0xFF00363D), fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isNeon: Boolean = false,
    isEmerald: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 13.sp
        )

        val textColor = when {
            isNeon -> NeonCyan
            isEmerald -> CubeEmerald
            isBold -> TextPrimary
            else -> Color.White
        }

        Text(
            text = value,
            color = textColor,
            fontSize = if (isNeon) 16.sp else 13.sp,
            fontWeight = if (isBold || isNeon || isEmerald) FontWeight.Bold else FontWeight.Normal
        )
    }
}
