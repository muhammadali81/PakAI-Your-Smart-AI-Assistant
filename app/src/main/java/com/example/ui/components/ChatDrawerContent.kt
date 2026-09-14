package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatSessionEntity

import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Slideshow
import com.example.ui.tools.StudioTool

@Composable
fun ChatDrawerContent(
    sessions: List<ChatSessionEntity>,
    activeSessionId: Long,
    onSelectSession: (Long) -> Unit,
    onNewChatClick: () -> Unit,
    onRenameSession: (Long, String) -> Unit,
    onDeleteSession: (Long) -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToStudio: (StudioTool) -> Unit = {},
    onOpenVoiceCommand: () -> Unit = {},
    onOpenSettings: () -> Unit,
    onOpenDeveloperInfo: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var sessionToRename by remember { mutableStateOf<ChatSessionEntity?>(null) }
    var renameInput by remember { mutableStateOf("") }
    var sessionToDelete by remember { mutableStateOf<ChatSessionEntity?>(null) }

    val filteredSessions = remember(sessions, searchQuery) {
        if (searchQuery.isBlank()) sessions
        else sessions.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp),
        color = Color(0xFF0F141C),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 28.dp, bottom = 16.dp, start = 12.dp, end = 12.dp)
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF00FF88), Color(0xFF007A3D)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("PA", fontWeight = FontWeight.Bold, color = Color(0xFF0A1015), fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pak AI",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "by Muhammad Ali",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF00FF88),
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                IconButton(onClick = onCloseDrawer) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close drawer", tint = Color(0xFF8C9BAE))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // "+ New Chat" button (ChatGPT hallmark)
            Button(
                onClick = {
                    onNewChatClick()
                    onCloseDrawer()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF19222E),
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A394C))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF00FF88),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "New chat",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Filter
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = { Text("Search conversations...", fontSize = 12.sp, color = Color(0xFF5A6B7E)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF5A6B7E),
                        modifier = Modifier.size(16.dp)
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF88),
                    unfocusedBorderColor = Color(0xFF232D3B),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Section Label
            Text(
                text = "CONVERSATIONS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF5A6B7E),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            // Conversation list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (filteredSessions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isBlank()) "No conversations yet" else "No matching chats",
                                color = Color(0xFF5A6B7E),
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    items(filteredSessions, key = { it.id }) { session ->
                        val isSelected = session.id == activeSessionId
                        SessionItemRow(
                            session = session,
                            isSelected = isSelected,
                            onSelect = {
                                onSelectSession(session.id)
                                onCloseDrawer()
                            },
                            onRename = {
                                sessionToRename = session
                                renameInput = session.title
                            },
                            onDelete = {
                                sessionToDelete = session
                            }
                        )
                    }
                }
            }

            Divider(color = Color(0xFF1E2836), modifier = Modifier.padding(vertical = 10.dp))

            // Voice Command Assistant Trigger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF00FF88).copy(alpha = 0.1f))
                    .border(1.dp, Color(0xFF00FF88).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .clickable {
                        onOpenVoiceCommand()
                        onCloseDrawer()
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Voice Assistant",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FF88)
                        )
                    )
                    Text("Speak commands & tasks", fontSize = 10.sp, color = Color(0xFF8DA3B8))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Studio Shortcut: PDF Maker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        onNavigateToStudio(StudioTool.PDF_MAKER)
                        onCloseDrawer()
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "PDF Maker Studio",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, color = Color.White)
                )
            }

            // Studio Shortcut: PPT Maker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        onNavigateToStudio(StudioTool.PPT_MAKER)
                        onCloseDrawer()
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Slideshow,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Presentation Slides Maker",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, color = Color.White)
                )
            }

            // Studio Shortcut: Photo Editor
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        onNavigateToStudio(StudioTool.PHOTO_EDITOR)
                        onCloseDrawer()
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Photo Editor & AI Vision",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, color = Color.White)
                )
            }

            // Studio Shortcut: 100+ Languages
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        onNavigateToStudio(StudioTool.POLYGLOT)
                        onCloseDrawer()
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "100+ Languages Studio",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, color = Color.White)
                )
            }

            // Quick Shortcut: Tasks Manager
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        onNavigateToTasks()
                        onCloseDrawer()
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Checklist,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Tasks & Plan Manager",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                )
            }

            // Quick Shortcut: Developer Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        onOpenDeveloperInfo()
                        onCloseDrawer()
                    }
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF00FF88),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "About Muhammad Ali",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                )
            }

            Divider(color = Color(0xFF1E2836), modifier = Modifier.padding(vertical = 8.dp))

            // User Profile / Settings Row (ChatGPT Bottom Footer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF141C26))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onOpenSettings()
                            onCloseDrawer()
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF00FF88), Color(0xFF005A2B)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("MA", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF08120B))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Muhammad Ali",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Pak AI Pro Tier",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF00FF88),
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = {
                        onOpenSettings()
                        onCloseDrawer()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFFCCD6E0),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Rename Dialog
    sessionToRename?.let { session ->
        AlertDialog(
            onDismissRequest = { sessionToRename = null },
            title = { Text("Rename Chat") },
            text = {
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    singleLine = true,
                    label = { Text("Chat Title") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (renameInput.isNotBlank()) {
                            onRenameSession(session.id, renameInput.trim())
                        }
                        sessionToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88), contentColor = Color(0xFF0F141C))
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToRename = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Session Confirm Dialog
    sessionToDelete?.let { session ->
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = { Text("Delete Chat?") },
            text = { Text("Are you sure you want to delete \"${session.title}\"? Messages will be removed.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSession(session.id)
                        sessionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SessionItemRow(
    session: ChatSessionEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onSelect),
        color = if (isSelected) Color(0xFF1D2836) else Color.Transparent,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFF00FF88) else Color(0xFF6B7C8E),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isSelected) Color.White else Color(0xFFBAC5D0),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSelected) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onRename,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Rename",
                            tint = Color(0xFF8C9BAE),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
