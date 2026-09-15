package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.PakAiRepository
import com.example.ui.theme.PakNeonGreen

@Composable
fun UpgradeDialog(
    repository: PakAiRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentTier by remember { mutableStateOf(repository.getSubscriptionTier()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.925f)
                .padding(vertical = 24.dp)
                .clip(RoundedCornerShape(28.dp)),
            color = Color(0xFF0F141C),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF00FF88), Color(0xFF007A3D)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color(0xFF0F141C),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Pak AI Membership",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Current Plan: $currentTier",
                                fontSize = 12.sp,
                                color = PakNeonGreen
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Free Plan Card
                PlanCard(
                    title = "Free Plan",
                    price = "0 PKR",
                    period = "Forever Free",
                    features = listOf(
                        "Standard AI Chat & Assistant",
                        "5 Daily PDF & PPT Generations",
                        "Standard Photo Analysis",
                        "Task & Islamic Studies Hub"
                    ),
                    isCurrent = currentTier == "Free",
                    accentColor = Color.Gray,
                    onSelect = {
                        repository.setSubscriptionTier("Free")
                        currentTier = "Free"
                        Toast.makeText(context, "Switched to Free Plan", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pro Plan Card (3000 PKR / month)
                PlanCard(
                    title = "Pro Plan",
                    price = "3,000 PKR",
                    period = "per month",
                    features = listOf(
                        "Unlimited AI Chat & Sessions",
                        "Unlimited PDF & PPT Maker Studio",
                        "Advanced Vision Photo Analysis",
                        "Deep Think AI Mode Enabled",
                        "100+ Languages Universal Translator",
                        "Priority AI Response Speed"
                    ),
                    isCurrent = currentTier == "Pro",
                    accentColor = PakNeonGreen,
                    onSelect = {
                        repository.setSubscriptionTier("Pro")
                        currentTier = "Pro"
                        Toast.makeText(context, "Successfully Upgraded to Pro Plan!", Toast.LENGTH_LONG).show()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pro Plus Plan Card (6000 PKR / month)
                PlanCard(
                    title = "Pro Plus Plan",
                    price = "6,000 PKR",
                    period = "per month",
                    features = listOf(
                        "Everything in Pro Plan Included",
                        "Custom Fine-Tuned AI Models",
                        "Custom API Key Integration Support",
                        "Unlimited Voice Assistant & Transcription",
                        "Priority 24/7 VIP Developer Support",
                        "Offline AI Model Caching & Backup"
                    ),
                    isCurrent = currentTier == "Pro Plus",
                    accentColor = Color(0xFFFFD700),
                    onSelect = {
                        repository.setSubscriptionTier("Pro Plus")
                        currentTier = "Pro Plus"
                        Toast.makeText(context, "Successfully Upgraded to Pro Plus VIP!", Toast.LENGTH_LONG).show()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PakNeonGreen)
                ) {
                    Text(
                        text = "Done",
                        color = Color(0xFF00391C),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    period: String,
    features: List<String>,
    isCurrent: Boolean,
    accentColor: Color,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isCurrent) 2.dp else 1.dp,
                color = if (isCurrent) accentColor else Color.DarkGray,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141A23)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = price,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = period,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!isCurrent) {
                Button(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(
                        text = "Upgrade to $title",
                        color = if (accentColor == Color(0xFFFFD700)) Color.Black else Color(0xFF00391C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
