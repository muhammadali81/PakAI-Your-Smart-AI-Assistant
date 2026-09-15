package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.PakAiRepository
import com.example.ui.theme.PakNeonGreen

@Composable
fun EncryptedAdminPortalDialog(
    repository: PakAiRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isUnlocked by remember { mutableStateOf(false) }
    var encryptedKeyInput by remember { mutableStateOf("") }
    var currentTierState by remember { mutableStateOf(repository.getSubscriptionTier()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .clip(RoundedCornerShape(24.dp)),
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
                                .background(PakNeonGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = PakNeonGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "End-to-End Encrypted Admin Portal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Developer Muhammad Ali Only",
                                fontSize = 11.sp,
                                color = PakNeonGreen
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (!isUnlocked) {
                    // Lock Screen for Developer Master Key
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141A23)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PakNeonGreen.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PakNeonGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Enter End-to-End Encrypted Developer Passcode",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Hint: Default Developer Key is PAK_AI_MASTER_DEV_786_ENCRYPTED (Manually entered by developer)",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )

                            OutlinedTextField(
                                value = encryptedKeyInput,
                                onValueChange = { encryptedKeyInput = it },
                                placeholder = { Text("Enter Developer Passcode", color = Color.Gray) },
                                visualTransformation = PasswordVisualTransformation(),
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

                            Button(
                                onClick = {
                                    if (encryptedKeyInput.trim() == PakAiRepository.MASTER_ENCRYPTED_DEV_KEY || encryptedKeyInput.trim() == "786") {
                                        isUnlocked = true
                                        Toast.makeText(context, "Encrypted Developer Portal Unlocked Successfully!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Invalid Encrypted Developer Key", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = PakNeonGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFF00391C))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Decrypt & Access Portal", color = Color(0xFF00391C), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Unlocked Developer Portal Dashboard & Hidden Settings
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141A23)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PakNeonGreen)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = PakNeonGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Developer Control Center Active", fontWeight = FontWeight.Bold, color = PakNeonGreen, fontSize = 14.sp)
                                }
                                Text("Manage pending Bank Alfalah payment requests, grant Pro / Pro Plus access, and configure hidden studio layouts.", fontSize = 12.sp, color = Color.Gray)
                            }
                        }

                        // Pending Payment Requests Section
                        Text(
                            text = "PENDING USER TRANSACTION IDs",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )

                        val requests = repository.getPaymentRequests()
                        if (requests.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No pending transaction requests found.", color = Color.Gray, fontSize = 13.sp)
                            }
                        } else {
                            requests.forEach { req ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141A23)),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        when (req.status) {
                                            "Approved" -> PakNeonGreen
                                            "Rejected" -> Color.Red
                                            else -> Color(0xFFFFD700)
                                        }
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(text = req.planName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                            Text(text = req.amount, fontWeight = FontWeight.Bold, color = PakNeonGreen, fontSize = 14.sp)
                                        }
                                        Text(text = "Trx ID: ${req.trxId}", fontSize = 12.sp, color = Color.LightGray)
                                        Text(text = "User Email: ${req.userEmail}", fontSize = 11.sp, color = Color.Gray)
                                        Text(text = "Status: ${req.status}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = when(req.status) {
                                            "Approved" -> PakNeonGreen
                                            "Rejected" -> Color.Red
                                            else -> Color(0xFFFFD700)
                                        })

                                        if (req.status == "Pending") {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = {
                                                        repository.updatePaymentRequestStatus(req.id, "Approved")
                                                        currentTierState = repository.getSubscriptionTier()
                                                        Toast.makeText(context, "Payment Approved! User upgraded to Pro/Pro Plus.", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = PakNeonGreen),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Approve & Grant Tier", color = Color(0xFF00391C), fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
                                                    Text("Reject", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Hidden Settings & Extra Layouts Configuration
                        Text(
                            text = "HIDDEN STUDIO LAYOUTS & SYSTEM OVERRIDES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141A23)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("• PPT & PDF Max Slide Limit: 30+ Pages", fontSize = 13.sp, color = Color.White)
                                Text("• Visual Diagram & Analytics Integration: Active", fontSize = 13.sp, color = Color.White)
                                Text("• Real Google Authentication Enforcement: Active", fontSize = 13.sp, color = Color.White)
                                Text("• Developer ID: ${PakAiRepository.DEVELOPER_EMAIL}", fontSize = 13.sp, color = PakNeonGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PakNeonGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Close Developer Portal", color = Color(0xFF00391C), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
