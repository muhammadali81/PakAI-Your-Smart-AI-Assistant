package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.PakAiRepository

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsDialog(
    repository: PakAiRepository,
    onDismiss: () -> Unit,
    onExportHistory: () -> Unit,
    onClearAllChats: () -> Unit,
    onClearCompletedTasks: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // Settings State
    var apiKeyText by remember { mutableStateOf(repository.getApiKey()) }
    var showApiKey by remember { mutableStateOf(false) }
    var selectedModel by remember { mutableStateOf(repository.getSelectedModel()) }
    var temperature by remember { mutableFloatStateOf(repository.getTemperature()) }
    var userProfileText by remember { mutableStateOf(repository.getUserProfile()) }
    var customInstructionsText by remember { mutableStateOf(repository.getCustomInstructions()) }
    var deepThinkEnabled by remember { mutableStateOf(repository.isDeepThinkEnabled()) }
    var webSearchEnabled by remember { mutableStateOf(repository.isWebSearchEnabled()) }
    var ttsSpeed by remember { mutableFloatStateOf(repository.getTtsSpeed()) }
    var ttsPitch by remember { mutableFloatStateOf(repository.getTtsPitch()) }

    var showClearHistoryConfirm by remember { mutableStateOf(false) }

    val tabs = listOf("General", "Personalize", "Model & AI", "Voice", "Data")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp, bottom = 16.dp, start = 12.dp, end = 12.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = Color(0xFF0F141C),
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header (ChatGPT Style)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF141A23))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF00FF88), Color(0xFF007A3D)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = Color(0xFF0F141C),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Settings",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Pak AI • Muhammad Ali",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF00FF88),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                // Tabs (ChatGPT style tabs)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF141A23),
                    contentColor = Color(0xFF00FF88),
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF00FF88),
                            height = 3.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) Color(0xFF00FF88) else Color(0xFF8C9BAE)
                                )
                            }
                        )
                    }
                }

                // Content Scrollable
                Box(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (selectedTab) {
                            0 -> GeneralSettingsTab(
                                repository = repository,
                                onSaveApiKey = { key ->
                                    repository.setCustomApiKey(key)
                                    Toast.makeText(context, "API Key updated successfully!", Toast.LENGTH_SHORT).show()
                                }
                            )
                            1 -> PersonalizationTab(
                                userProfile = userProfileText,
                                onUserProfileChange = { userProfileText = it },
                                customInstructions = customInstructionsText,
                                onCustomInstructionsChange = { customInstructionsText = it },
                                onSave = {
                                    repository.setUserProfile(userProfileText)
                                    repository.setCustomInstructions(customInstructionsText)
                                    Toast.makeText(context, "Personalization instructions saved!", Toast.LENGTH_SHORT).show()
                                }
                            )
                            2 -> ModelIntelligenceTab(
                                selectedModel = selectedModel,
                                onModelChange = {
                                    selectedModel = it
                                    repository.setSelectedModel(it)
                                },
                                temperature = temperature,
                                onTemperatureChange = {
                                    temperature = it
                                    repository.setTemperature(it)
                                },
                                deepThinkEnabled = deepThinkEnabled,
                                onDeepThinkToggle = {
                                    deepThinkEnabled = it
                                    repository.setDeepThinkEnabled(it)
                                },
                                webSearchEnabled = webSearchEnabled,
                                onWebSearchToggle = {
                                    webSearchEnabled = it
                                    repository.setWebSearchEnabled(it)
                                },
                                apiKeyText = apiKeyText,
                                onApiKeyChange = { apiKeyText = it },
                                showApiKey = showApiKey,
                                onToggleShowApiKey = { showApiKey = !showApiKey },
                                onSaveApiKey = {
                                    repository.setCustomApiKey(apiKeyText)
                                    Toast.makeText(context, "API Key saved!", Toast.LENGTH_SHORT).show()
                                }
                            )
                            3 -> VoiceSettingsTab(
                                ttsSpeed = ttsSpeed,
                                onSpeedChange = {
                                    ttsSpeed = it
                                    repository.setTtsSpeed(it)
                                },
                                ttsPitch = ttsPitch,
                                onPitchChange = {
                                    ttsPitch = it
                                    repository.setTtsPitch(it)
                                }
                            )
                            4 -> DataControlsTab(
                                onExportHistory = onExportHistory,
                                onClearAllChatsClick = { showClearHistoryConfirm = true },
                                onClearCompletedTasks = {
                                    onClearCompletedTasks()
                                    Toast.makeText(context, "Completed tasks cleared", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }

                // Footer with Save / Done
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF141A23))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FF88),
                            contentColor = Color(0xFF0A0F14)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showClearHistoryConfirm) {
        AlertDialog(
            onDismissRequest = { showClearHistoryConfirm = false },
            title = { Text("Delete all chats?") },
            text = { Text("This will permanently clear all conversation history and messages across all sessions.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllChats()
                        showClearHistoryConfirm = false
                        Toast.makeText(context, "All chat history deleted", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 0: GENERAL
// -------------------------------------------------------------
@Composable
private fun GeneralSettingsTab(
    repository: PakAiRepository,
    onSaveApiKey: (String) -> Unit
) {
    // User / Account Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E28))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF00FF88), Color(0xFF005A2B)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("MA", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF08120B))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Muhammad Ali",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "alimuhammadhvn81@gmail.com",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8C9BAE))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x2200FF88)
                    ) {
                        Text(
                            text = "PAK AI PRO • UNLIMITED ACCESS",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF00FF88),
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }
    }

    // App Preferences Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E28))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Application Information",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF88)
                )
            )

            SettingRow(
                icon = Icons.Default.Psychology,
                title = "AI Core Engine",
                subtitle = "Pak AI Neural Core 3.5 via REST API"
            )

            Divider(color = Color(0xFF222D3D))

            SettingRow(
                icon = Icons.Default.Code,
                title = "Application Developer",
                subtitle = "Muhammad Ali"
            )

            Divider(color = Color(0xFF222D3D))

            SettingRow(
                icon = Icons.Default.Email,
                title = "Developer Contact",
                subtitle = "alimuhammadhvn81@gmail.com"
            )

            Divider(color = Color(0xFF222D3D))

            SettingRow(
                icon = Icons.Default.Palette,
                title = "Appearance Theme",
                subtitle = "Pak Tech Dark (Emerald & Onyx)"
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 1: PERSONALIZATION (ChatGPT Custom Instructions)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PersonalizationTab(
    userProfile: String,
    onUserProfileChange: (String) -> Unit,
    customInstructions: String,
    onCustomInstructionsChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E28))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00FF88))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Custom Instructions (ChatGPT Style)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
            Text(
                text = "Tell Pak AI about yourself and how you would like it to answer. Pak AI will remember this in every conversation.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8C9BAE))
            )

            // Preset Chips
            Text(
                text = "Quick Presets:",
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF00FF88))
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PresetChip("💻 Software Engineer") {
                    onUserProfileChange("I am a software engineer focused on Android, Kotlin, and modern web development.")
                    onCustomInstructionsChange("Provide clean, concise code blocks with explanations. Prefer production-ready code.")
                }
                PresetChip("📚 Student") {
                    onUserProfileChange("I am a student studying computer science and general sciences.")
                    onCustomInstructionsChange("Explain complex topics simply with analogies, step-by-step breakdowns, and bullet points.")
                }
                PresetChip("🇵🇰 Urdu Bilingual") {
                    onUserProfileChange("I speak both Urdu and English.")
                    onCustomInstructionsChange("Feel free to use friendly Roman Urdu (e.g. 'Bilkul! Main aap ko samjhata hoon') and English naturally.")
                }
                PresetChip("⚡ Direct & Concise") {
                    onCustomInstructionsChange("Keep all responses very concise, bullet-pointed, and strictly avoid filler text or fluff.")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Box 1: What would you like Pak AI to know about you?
            Text(
                text = "What would you like Pak AI to know about you?",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.White)
            )
            OutlinedTextField(
                value = userProfile,
                onValueChange = onUserProfileChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Where you work, your hobbies, subjects you study...", color = Color(0xFF5A697A)) },
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF88),
                    unfocusedBorderColor = Color(0xFF2A3647),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Box 2: How would you like Pak AI to respond?
            Text(
                text = "How would you like Pak AI to respond?",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.White)
            )
            OutlinedTextField(
                value = customInstructions,
                onValueChange = onCustomInstructionsChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Formal or casual tone, short or detailed answers, prefer code in Kotlin...", color = Color(0xFF5A697A)) },
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF88),
                    unfocusedBorderColor = Color(0xFF2A3647),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FF88),
                    contentColor = Color(0xFF0A0F14)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Custom Instructions", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PresetChip(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E2734),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3D4F))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCCD6E0), fontSize = 11.sp)
        )
    }
}

// -------------------------------------------------------------
// TAB 2: MODEL & AI INTELLIGENCE
// -------------------------------------------------------------
@Composable
private fun ModelIntelligenceTab(
    selectedModel: String,
    onModelChange: (String) -> Unit,
    temperature: Float,
    onTemperatureChange: (Float) -> Unit,
    deepThinkEnabled: Boolean,
    onDeepThinkToggle: (Boolean) -> Unit,
    webSearchEnabled: Boolean,
    onWebSearchToggle: (Boolean) -> Unit,
    apiKeyText: String,
    onApiKeyChange: (String) -> Unit,
    showApiKey: Boolean,
    onToggleShowApiKey: () -> Unit,
    onSaveApiKey: () -> Unit
) {
    // Model Selection
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E28))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Model Architecture",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF88)
                )
            )

            ModelOptionItem(
                name = "Pak AI 3.5 Engine",
                badge = "Recommended • Fast",
                description = "Ultra fast, versatile reasoning, ideal for chat, coding, tasks & voice.",
                isSelected = selectedModel == "gemini-3.5-flash",
                onClick = { onModelChange("gemini-3.5-flash") }
            )

            ModelOptionItem(
                name = "Pak AI 3.5 Ultra",
                badge = "Advanced Reasoning",
                description = "Deep analytical capabilities for highly complex coding, documents & Islamic research.",
                isSelected = selectedModel == "gemini-3.5-pro",
                onClick = { onModelChange("gemini-3.5-pro") }
            )
        }
    }

    // Intelligence Parameters
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E28))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Model Parameters",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF88)
                )
            )

            // Temperature Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Creativity (Temperature): ${String.format("%.1f", temperature)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = when {
                            temperature < 0.4f -> "Precise / Factual"
                            temperature < 0.8f -> "Balanced"
                            else -> "Creative"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF00FF88))
                    )
                }

                Slider(
                    value = temperature,
                    onValueChange = onTemperatureChange,
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00FF88),
                        activeTrackColor = Color(0xFF00FF88),
                        inactiveTrackColor = Color(0xFF222D3D)
                    )
                )
            }

            Divider(color = Color(0xFF222D3D))

            // Deep Thinking Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "⚡ Deep Thinking Mode",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Enables step-by-step logical reasoning before producing answers.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8C9BAE), fontSize = 11.sp)
                    )
                }
                Switch(
                    checked = deepThinkEnabled,
                    onCheckedChange = onDeepThinkToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF0F141C),
                        checkedTrackColor = Color(0xFF00FF88)
                    )
                )
            }

            Divider(color = Color(0xFF222D3D))

            // Web Search Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "🌐 Web Knowledge Synthesis",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Incorporates up-to-date real-world facts and structured references.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8C9BAE), fontSize = 11.sp)
                    )
                }
                Switch(
                    checked = webSearchEnabled,
                    onCheckedChange = onWebSearchToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF0F141C),
                        checkedTrackColor = Color(0xFF00FF88)
                    )
                )
            }
        }
    }

    // Pak AI Engine Key Override
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E28))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFF00FF88))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pak AI Engine Key",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Text(
                text = "Loaded from AI Studio build configuration by default. You can also paste your personal AI Engine key here.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8C9BAE), fontSize = 11.sp)
            )

            OutlinedTextField(
                value = apiKeyText,
                onValueChange = onApiKeyChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onToggleShowApiKey) {
                        Icon(
                            imageVector = if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Key",
                            tint = Color(0xFF8C9BAE)
                        )
                    }
                },
                placeholder = { Text("AIzaSy...", color = Color(0xFF5A697A)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF88),
                    unfocusedBorderColor = Color(0xFF2A3647),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedButton(
                onClick = onSaveApiKey,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00FF88)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF88)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save API Key")
            }
        }
    }
}

@Composable
private fun ModelOptionItem(
    name: String,
    badge: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0x2200FF88) else Color(0xFF1E2734),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) Color(0xFF00FF88) else Color(0xFF2A3647)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) Color(0xFF00FF88) else Color(0xFF2E3D4F)
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) Color(0xFF08120B) else Color(0xFFCCD6E0),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF8C9BAE),
                    fontSize = 11.sp
                )
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 3: VOICE SETTINGS (ChatGPT Voice controls)
// -------------------------------------------------------------
@Composable
private fun VoiceSettingsTab(
    ttsSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    ttsPitch: Float,
    onPitchChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E28))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color(0xFF00FF88))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Text-to-Speech (Voice Output)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            // Speed
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Speaking Speed: ${String.format("%.1fx", ttsSpeed)}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                    Text(
                        text = when {
                            ttsSpeed < 0.9f -> "Slow"
                            ttsSpeed > 1.2f -> "Fast"
                            else -> "Normal"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF00FF88))
                    )
                }
                Slider(
                    value = ttsSpeed,
                    onValueChange = onSpeedChange,
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00FF88),
                        activeTrackColor = Color(0xFF00FF88),
                        inactiveTrackColor = Color(0xFF222D3D)
                    )
                )
            }

            Divider(color = Color(0xFF222D3D))

            // Pitch
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Voice Pitch: ${String.format("%.1f", ttsPitch)}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                    Text(
                        text = when {
                            ttsPitch < 0.9f -> "Deeper"
                            ttsPitch > 1.1f -> "Higher"
                            else -> "Natural"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF00FF88))
                    )
                }
                Slider(
                    value = ttsPitch,
                    onValueChange = onPitchChange,
                    valueRange = 0.6f..1.5f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00FF88),
                        activeTrackColor = Color(0xFF00FF88),
                        inactiveTrackColor = Color(0xFF222D3D)
                    )
                )
            }

            Divider(color = Color(0xFF222D3D))

            // Voice Recognition / Mic notes
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Voice Dictation (Input)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Tap the microphone icon in the chat bar to dictate questions hands-free in English or Urdu.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8C9BAE), fontSize = 11.sp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: DATA CONTROLS (ChatGPT Data controls)
// -------------------------------------------------------------
@Composable
private fun DataControlsTab(
    onExportHistory: () -> Unit,
    onClearAllChatsClick: () -> Unit,
    onClearCompletedTasks: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E28))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Data Controls & Privacy",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF88)
                )
            )

            // Export Chats
            OutlinedButton(
                onClick = onExportHistory,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3D4F))
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Conversations (Share Text)")
            }

            // Clear Completed Tasks
            OutlinedButton(
                onClick = onClearCompletedTasks,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3D4F))
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Completed Tasks")
            }

            Divider(color = Color(0xFF222D3D))

            // Delete all chats
            Button(
                onClick = onClearAllChatsClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B151E),
                    contentColor = Color(0xFFFF5252)
                )
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete All Chats", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E2734)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8C9BAE), fontSize = 11.sp)
            )
        }
    }
}
