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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PaymentRequest
import com.example.data.repository.PakAiRepository
import com.example.ui.theme.PakNeonGreen

@Composable
fun UpgradeDialog(
    repository: PakAiRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var currentTier by remember { mutableStateOf(repository.getSubscriptionTier()) }

    var selectedPlanForPayment by remember { mutableStateOf<Pair<String, String>?>(null) } // (PlanName, Amount)
    var trxIdInput by remember { mutableStateOf("") }
    var showAdminPanel by remember { mutableStateOf(false) }
    var showAdminLogin by remember { mutableStateOf(false) }
    var adminPinInput by remember { mutableStateOf("") }

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

                Spacer(modifier = Modifier.height(16.dp))

                // Admin Panel Access Button (Restricted strictly to Developer Muhammad Ali)
                OutlinedButton(
                    onClick = {
                        val devEmail = PakAiRepository.DEVELOPER_EMAIL
                        if (devEmail == "alimuhammadhvn81@gmail.com") { // Developer check
                            showAdminLogin = true
                        } else {
                            Toast.makeText(context, "Access Denied: Admin Portal is strictly for Developer Muhammad Ali (alimuhammadhvn81@gmail.com).", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PakNeonGreen),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PakNeonGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Admin Portal (Developer Only)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                        selectedPlanForPayment = Pair("Pro Plan", "3,000 PKR")
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
                        selectedPlanForPayment = Pair("Pro Plus Plan", "6,000 PKR")
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

    // Bank Alfalah Payment Dialog when a Pro/Pro Plus plan is selected
    if (selectedPlanForPayment != null) {
        val plan = selectedPlanForPayment!!
        AlertDialog(
            onDismissRequest = { selectedPlanForPayment = null },
            containerColor = Color(0xFF141A23),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = PakNeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Bank Alfalah Payment Gateway", color = Color.White, fontSize = 16.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Transfer ${plan.second} for ${plan.first} using any payment method (JazzCash, EasyPaisa, Bank Transfer, Raast, ATM, or Card) to our official Bank Alfalah account:",
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F141C)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PakNeonGreen.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            PaymentDetailRow("Bank Name:", PakAiRepository.BANK_NAME, clipboardManager, context)
                            PaymentDetailRow("Account Title:", PakAiRepository.ACCOUNT_TITLE, clipboardManager, context)
                            PaymentDetailRow("Account Number:", PakAiRepository.ACCOUNT_NUMBER, clipboardManager, context)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter Transaction ID (Trx ID) or Reference Number after payment:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PakNeonGreen
                    )

                    OutlinedTextField(
                        value = trxIdInput,
                        onValueChange = { trxIdInput = it },
                        placeholder = { Text("e.g. TRX123456789", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PakNeonGreen,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (trxIdInput.isBlank()) {
                            Toast.makeText(context, "Please enter Transaction ID", Toast.LENGTH_SHORT).show()
                        } else {
                            repository.submitPaymentRequest(plan.first, plan.second, trxIdInput)
                            Toast.makeText(context, "Payment submitted! Admin notification sent.", Toast.LENGTH_LONG).show()
                            trxIdInput = ""
                            selectedPlanForPayment = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PakNeonGreen)
                ) {
                    Text("Submit for Verification", color = Color(0xFF00391C), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { selectedPlanForPayment = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Admin Passcode Dialog
    if (showAdminLogin) {
        AlertDialog(
            onDismissRequest = { showAdminLogin = false; adminPinInput = "" },
            containerColor = Color(0xFF141A23),
            title = { Text("Admin Panel Login", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter Admin PIN (Default: 786):", color = Color.LightGray, fontSize = 13.sp)
                    OutlinedTextField(
                        value = adminPinInput,
                        onValueChange = { adminPinInput = it },
                        placeholder = { Text("PIN", color = Color.Gray) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PakNeonGreen,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (adminPinInput == "786") {
                            showAdminLogin = false
                            adminPinInput = ""
                            showAdminPanel = true
                        } else {
                            Toast.makeText(context, "Incorrect PIN. Try 786", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PakNeonGreen)
                ) {
                    Text("Login", color = Color(0xFF00391C), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showAdminLogin = false; adminPinInput = "" }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Admin Panel Dialog to review & approve/reject payments
    if (showAdminPanel) {
        val requests = repository.getPaymentRequests()
        Dialog(
            onDismissRequest = { showAdminPanel = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 24.dp)
                    .clip(RoundedCornerShape(24.dp)),
                color = Color(0xFF141A23),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = PakNeonGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Admin Payment Requests Panel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        IconButton(onClick = { showAdminPanel = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Review incoming Bank Alfalah payments and grant 30-day Pro/Pro Plus access:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (requests.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No payment requests pending.", color = Color.Gray, fontSize = 14.sp)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .weight(weight = 1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            requests.forEach { req ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F141C)),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        when (req.status) {
                                            "Approved" -> PakNeonGreen
                                            "Rejected" -> Color.Red
                                            else -> Color(0xFFFFD700)
                                        }
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(text = req.planName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                            Text(text = req.amount, fontWeight = FontWeight.Bold, color = PakNeonGreen, fontSize = 14.sp)
                                        }
                                        Text(text = "Trx ID: ${req.trxId}", fontSize = 12.sp, color = Color.LightGray)
                                        Text(text = "User: ${req.userEmail}", fontSize = 11.sp, color = Color.Gray)
                                        Text(text = "Status: ${req.status}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = when(req.status) {
                                            "Approved" -> PakNeonGreen
                                            "Rejected" -> Color.Red
                                            else -> Color(0xFFFFD700)
                                        })

                                        if (req.status == "Pending") {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = {
                                                        repository.updatePaymentRequestStatus(req.id, "Approved")
                                                        currentTier = repository.getSubscriptionTier()
                                                        Toast.makeText(context, "Payment Approved & Access Granted!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = PakNeonGreen),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Approve", color = Color(0xFF00391C), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }
                                                OutlinedButton(
                                                    onClick = {
                                                        repository.updatePaymentRequestStatus(req.id, "Rejected")
                                                        Toast.makeText(context, "Payment Rejected", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Reject", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showAdminPanel = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PakNeonGreen)
                    ) {
                        Text("Close Admin Panel", color = Color(0xFF00391C), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentDetailRow(label: String, value: String, clipboardManager: ClipboardManager, context: android.content.Context) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, fontSize = 11.sp, color = Color.Gray)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        IconButton(
            onClick = {
                clipboardManager.setText(AnnotatedString(value))
                Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = PakNeonGreen, modifier = Modifier.size(16.dp))
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
                        text = "Pay via Bank Alfalah for $title",
                        color = if (accentColor == Color(0xFFFFD700)) Color.Black else Color(0xFF00391C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
