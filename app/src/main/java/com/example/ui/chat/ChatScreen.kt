package com.example.ui.chat

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material.icons.filled.VolumeUp
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
import com.example.ui.components.MarkdownText
import com.example.ui.theme.PakBotBubbleDark
import com.example.ui.theme.PakDarkBackground
import com.example.ui.theme.PakDarkSurface
import com.example.ui.theme.PakDarkSurfaceBorder
import com.example.ui.theme.PakGoldAccent
import com.example.ui.theme.PakGreenDark
import com.example.ui.theme.PakNeonGreen
import com.example.ui.theme.PakUserBubbleDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val toastNotification by viewModel.toastNotification.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var showModelMenu by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    // Edit message dialog state
    var messageToEdit by remember { mutableStateOf<ChatMessageEntity?>(null) }
    var editMessageInput by remember { mutableStateOf("") }

    // Photo and File attachment state
    var attachedPhotoUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var attachedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var attachedFileName by remember { mutableStateOf<String?>(null) }
    var attachedFileContent by remember { mutableStateOf<String?>(null) }
    var showAttachmentMenu by remember { mutableStateOf(false) }

    // Photo picker launcher (Android Photo Picker)
    val chatPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: android.net.Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val decoded = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (decoded != null) {
                    attachedPhotoUri = it
                    attachedBitmap = decoded
                    attachedFileName = "photo_${System.currentTimeMillis()}.jpg"
                    attachedFileContent = null
                    Toast.makeText(context, "Photo attached", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Could not attach photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Document file picker launcher
    val chatFilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            try {
                val fileName = it.lastPathSegment?.substringAfterLast('/') ?: "document.txt"
                val inputStream = context.contentResolver.openInputStream(it)
                val text = inputStream?.bufferedReader()?.use { reader -> reader.readText() } ?: ""
                val snippet = if (text.length > 3000) text.take(3000) + "\n...[truncated]" else text
                attachedFileName = fileName
                attachedFileContent = snippet
                attachedPhotoUri = null
                attachedBitmap = null
                Toast.makeText(context, "File attached: $fileName", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Could not read file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Speech to text launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val spokenText = matches[0]
                inputText = if (inputText.isBlank()) spokenText else "$inputText $spokenText"
                Toast.makeText(context, "Voice input recognized", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Pak AI...")
            }
            try {
                speechRecognizerLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Voice recognition service unavailable", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Microphone permission is required for voice dictation", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(toastNotification) {
        toastNotification?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastNotification()
        }
    }

    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        // --- ChatGPT Header Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF101620))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sidebar menu button (ChatGPT sidebar drawer)
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Sidebar",
                    tint = Color.White
                )
            }

            // Model Selection Pill (ChatGPT Model dropdown)
            Box {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showModelMenu = true }
                        .border(1.dp, Color(0xFF253346), RoundedCornerShape(20.dp)),
                    color = Color(0xFF182230)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedModel.contains("pro", ignoreCase = true)) "Pak AI 3.5 Pro" else "Pak AI 3.5 Flash",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Model",
                            tint = Color(0xFF00FF88),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showModelMenu,
                    onDismissRequest = { showModelMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("Pak AI 3.5 Flash", fontWeight = FontWeight.Bold)
                                Text("High speed, recommended for everyday tasks", fontSize = 11.sp, color = Color.Gray)
                            }
                        },
                        onClick = {
                            viewModel.setSelectedModel("gemini-3.5-flash")
                            showModelMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("Pak AI 3.5 Pro", fontWeight = FontWeight.Bold)
                                Text("Complex reasoning and advanced logic", fontSize = 11.sp, color = Color.Gray)
                            }
                        },
                        onClick = {
                            viewModel.setSelectedModel("gemini-3.5-pro")
                            showModelMenu = false
                        }
                    )
                }
            }

            // Right actions: New Chat & Overflow Menu
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.startNewSession() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = Color(0xFF00FF88)
                    )
                }

                Box {
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFFBAC5D0)
                        )
                    }

                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings (ChatGPT options)") },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF00FF88)) },
                            onClick = {
                                showOverflowMenu = false
                                onOpenSettings()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export Chat Text") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            onClick = {
                                showOverflowMenu = false
                                val exportText = viewModel.exportChatHistoryText()
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, exportText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Export Pak AI Chat")
                                context.startActivity(shareIntent)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear Current Chat", color = Color(0xFFFF5252)) },
                            leadingIcon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFFF5252)) },
                            onClick = {
                                showOverflowMenu = false
                                viewModel.clearCurrentChat()
                            }
                        )
                    }
                }
            }
        }

        // --- Chat Messages Area ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty()) {
                EmptyChatWelcome(
                    onSelectPrompt = { prompt ->
                        viewModel.sendMessage(prompt)
                    }
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatGPTMessageItem(
                            message = msg,
                            isSpeaking = isSpeaking,
                            onCopy = { text ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Pak AI", text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            onSpeak = { text -> viewModel.speakText(text) },
                            onRegenerate = { viewModel.regenerateLastResponse() },
                            onThumbsUp = { viewModel.setMessageFeedback(msg.id, 1) },
                            onThumbsDown = { viewModel.setMessageFeedback(msg.id, -1) },
                            onShare = { text ->
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, text)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Pak AI message"))
                            },
                            onConvertToTask = { content -> viewModel.convertAiMessageToTasks(content) },
                            onEdit = {
                                messageToEdit = msg
                                editMessageInput = msg.content
                            },
                            onDelete = { viewModel.deleteMessage(msg.id) }
                        )
                    }

                    if (isLoading) {
                        item {
                            AiTypingIndicator()
                        }
                    }
                }
            }
        }

        // Quick Suggestion Chips (when chatting)
        if (messages.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickChip("📅 Plan my day") { viewModel.sendMessage("Plan my daily tasks step by step") }
                QuickChip("📝 Break down a task") { viewModel.sendMessage("Give me an actionable breakdown of a study routine") }
                QuickChip("🇵🇰 اردو میں جواب دیں") { viewModel.sendMessage("مجھے آج کا ایک اچھا موٹیویشنل مشورہ اردو میں دیں") }
                QuickChip("💡 Coding Tips") { viewModel.sendMessage("Share 3 clean code tips for Android Jetpack Compose") }
            }
        }

        // --- Bottom ChatGPT Input Bar ---
        Surface(
            color = Color(0xFF101620),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // --- Attachment Preview Bar ---
                AnimatedVisibility(visible = attachedBitmap != null || attachedFileName != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF182230))
                            .border(1.dp, Color(0xFF00FF88).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (attachedBitmap != null) {
                            Image(
                                bitmap = attachedBitmap!!.asImageBitmap(),
                                contentDescription = "Attached Photo",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = attachedFileName ?: "Photo attached",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Ready for AI Vision & OCR",
                                    fontSize = 10.sp,
                                    color = Color(0xFF00FF88)
                                )
                            }
                        } else if (attachedFileName != null) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Attached Document",
                                tint = Color(0xFF00FF88),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = attachedFileName ?: "Document",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Text(
                                    text = "Document attached for context",
                                    fontSize = 10.sp,
                                    color = Color(0xFF8DA3B8)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                attachedPhotoUri = null
                                attachedBitmap = null
                                attachedFileName = null
                                attachedFileContent = null
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove attachment",
                                tint = Color(0xFFFF6B6B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attachment (+) Button with Dropdown
                    Box {
                        IconButton(
                            onClick = { showAttachmentMenu = true },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1B2430))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Upload Photo or File",
                                tint = Color(0xFF00FF88),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showAttachmentMenu,
                            onDismissRequest = { showAttachmentMenu = false },
                            modifier = Modifier.background(Color(0xFF141D28))
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Upload Photo", color = Color.White, fontSize = 13.sp)
                                    }
                                },
                                onClick = {
                                    showAttachmentMenu = false
                                    chatPhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Upload File / Document", color = Color.White, fontSize = 13.sp)
                                    }
                                },
                                onClick = {
                                    showAttachmentMenu = false
                                    chatFilePickerLauncher.launch("*/*")
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Voice Dictation Mic Button (ChatGPT Mic)
                    IconButton(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B2430))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Dictation",
                            tint = Color(0xFF00FF88),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Text Input
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                "Message Pak AI...",
                                fontSize = 14.sp,
                                color = Color(0xFF6B7E93)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input"),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FF88),
                            unfocusedBorderColor = Color(0xFF222E3E),
                            focusedContainerColor = Color(0xFF161E28),
                            unfocusedContainerColor = Color(0xFF161E28),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    val canSend = (inputText.isNotBlank() || attachedBitmap != null || attachedFileName != null) && !isLoading

                    // Send Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (canSend) Color(0xFF00FF88) else Color(0x3300FF88))
                            .clickable(enabled = canSend) {
                                val text = inputText
                                var b64: String? = null
                                if (attachedBitmap != null) {
                                    val stream = java.io.ByteArrayOutputStream()
                                    attachedBitmap?.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, stream)
                                    b64 = android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.NO_WRAP)
                                }
                                viewModel.sendMessageWithAttachment(
                                    text = text,
                                    attachmentType = if (attachedBitmap != null) "image" else if (attachedFileName != null) "file" else null,
                                    attachmentName = attachedFileName,
                                    base64Image = b64,
                                    fileContentSnippet = attachedFileContent
                                )
                                inputText = ""
                                attachedPhotoUri = null
                                attachedBitmap = null
                                attachedFileName = null
                                attachedFileContent = null
                            }
                            .testTag("send_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF0A1015)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Message",
                                tint = Color(0xFF0A1015),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit message prompt dialog
    messageToEdit?.let { msg ->
        AlertDialog(
            onDismissRequest = { messageToEdit = null },
            title = { Text("Edit & Resubmit") },
            text = {
                OutlinedTextField(
                    value = editMessageInput,
                    onValueChange = { editMessageInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newText = editMessageInput.trim()
                        if (newText.isNotBlank()) {
                            viewModel.editAndResubmitMessage(msg.id, newText)
                        }
                        messageToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88), contentColor = Color(0xFF0A1015))
                ) {
                    Text("Send", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { messageToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChatGPTMessageItem(
    message: ChatMessageEntity,
    isSpeaking: Boolean,
    onCopy: (String) -> Unit,
    onSpeak: (String) -> Unit,
    onRegenerate: () -> Unit,
    onThumbsUp: () -> Unit,
    onThumbsDown: () -> Unit,
    onShare: (String) -> Unit,
    onConvertToTask: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isUser = message.role == "user"
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF00FF88), Color(0xFF006633)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("PA", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF06100A))
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            Column(
                modifier = Modifier.widthIn(max = 330.dp),
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                Surface(
                    color = if (isUser) Color(0xFF1E2838) else Color(0xFF151C26),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isUser) Color(0xFF2C3C50) else Color(0xFF202A38)
                    ),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        if (isUser) {
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFF0F4F8),
                                lineHeight = 21.sp
                            )
                        } else {
                            // Rich Markdown & Code-block rendering like ChatGPT
                            MarkdownText(
                                text = message.content,
                                textColor = Color(0xFFE4E9EF)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }

                // Action Bar for User message (Edit, Copy, Delete)
                if (isUser) {
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit message", tint = Color(0xFF7A8B9E), modifier = Modifier.size(13.dp))
                        }
                        IconButton(onClick = { onCopy(message.content) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy message", tint = Color(0xFF7A8B9E), modifier = Modifier.size(13.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFF7A8B9E), modifier = Modifier.size(13.dp))
                        }
                    }
                }

                // Action Bar for AI message (ChatGPT standard actions: Copy, Read Aloud, Regenerate, Thumbs, Share, Add to Task)
                if (!isUser && message.content.isNotBlank()) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp, start = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onCopy(message.content) }, modifier = Modifier.size(26.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy message", tint = Color(0xFF8C9BAE), modifier = Modifier.size(15.dp))
                        }

                        IconButton(onClick = { onSpeak(message.content) }, modifier = Modifier.size(26.dp)) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                contentDescription = "Read Aloud",
                                tint = if (isSpeaking) Color(0xFF00FF88) else Color(0xFF8C9BAE),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        IconButton(onClick = onRegenerate, modifier = Modifier.size(26.dp)) {
                            Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = Color(0xFF8C9BAE), modifier = Modifier.size(15.dp))
                        }

                        IconButton(onClick = onThumbsUp, modifier = Modifier.size(26.dp)) {
                            Icon(
                                imageVector = if (message.feedback > 0) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                                contentDescription = "Good response",
                                tint = if (message.feedback > 0) Color(0xFF00FF88) else Color(0xFF8C9BAE),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        IconButton(onClick = onThumbsDown, modifier = Modifier.size(26.dp)) {
                            Icon(
                                imageVector = if (message.feedback < 0) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                                contentDescription = "Bad response",
                                tint = if (message.feedback < 0) Color(0xFFFF5252) else Color(0xFF8C9BAE),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        IconButton(onClick = { onShare(message.content) }, modifier = Modifier.size(26.dp)) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF8C9BAE), modifier = Modifier.size(15.dp))
                        }

                        // Superpower: Save to Tasks
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x2200FF88))
                                .clickable { onConvertToTask(message.content) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Task", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF00FF88))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyChatWelcome(onSelectPrompt: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Futuristic Glow Logo
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF00FF88), Color(0xFF006633)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.SmartToy,
                contentDescription = null,
                tint = Color(0xFF08120B),
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "السلام علیکم! What can I help with?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Pak AI • Developed by Muhammad Ali",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF00FF88),
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ChatGPT-style Prompt Starter Grid
        val promptSuggestions = listOf(
            "🚀 Plan a project roadmap step by step",
            "💻 Help me write clean Kotlin code",
            "🇵🇰 اردو میں بات چیت اور مدد کریں",
            "📚 Create an effective study schedule"
        )

        promptSuggestions.forEach { suggestion ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectPrompt(suggestion) }
                    .border(1.dp, Color(0xFF222E3E), RoundedCornerShape(14.dp)),
                color = Color(0xFF141D28)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFF00FF88),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = suggestion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCCD7E2)
                    )
                }
            }
        }
    }
}

@Composable
fun AiTypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF00FF88), Color(0xFF006633)))),
            contentAlignment = Alignment.Center
        ) {
            Text("PA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF08120B))
        }
        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            color = Color(0xFF151C26),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF202A38))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF00FF88)
                )
                Text(
                    text = "Pak AI is thinking...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF00FF88)
                )
            }
        }
    }
}

@Composable
fun QuickChip(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(1.dp, Color(0xFF00FF88).copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        color = Color(0xFF151D28)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFFD6DFE8),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
