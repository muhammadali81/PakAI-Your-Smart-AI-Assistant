package com.example.ui

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.chat.ChatScreen
import com.example.ui.chat.ChatViewModel
import com.example.ui.components.ChatDrawerContent
import com.example.ui.components.DeveloperInfoDialog
import com.example.ui.components.PakAITopBar
import com.example.ui.components.SettingsDialog
import com.example.ui.components.GoogleAuthDialog
import com.example.ui.components.EncryptedAdminPortalDialog
import com.example.ui.hub.HubScreen
import com.example.ui.tasks.TaskManagerScreen
import com.example.ui.tasks.TaskViewModel
import com.example.ui.theme.PakNeonGreen
import kotlinx.coroutines.launch

import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import com.example.ui.components.VoiceCommandDialog
import com.example.ui.components.UpgradeDialog
import com.example.ui.tools.StudioTool
import com.example.ui.tools.ToolsStudioScreen

enum class AppTab(val title: String) {
    CHAT("AI Chat"),
    STUDIO("AI Studio"),
    TASKS("Tasks"),
    HUB("Pak AI Hub")
}

@Composable
fun MainScreen(
    chatViewModel: ChatViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var selectedTab by remember { mutableStateOf(AppTab.CHAT) }
    var showDevInfoDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var showUpgradeDialog by remember { mutableStateOf(false) }
    var showGoogleAuthDialog by remember { mutableStateOf(false) }
    var showAdminPortalDialog by remember { mutableStateOf(false) }
    var activeStudioTool by remember { mutableStateOf(StudioTool.PDF_MAKER) }
    var studioInitialTopic by remember { mutableStateOf("") }

    val sessions by chatViewModel.sessions.collectAsState()
    val currentSessionId by chatViewModel.currentSessionId.collectAsState()
    val totalTasks by taskViewModel.totalCount.collectAsState()
    val completedTasks by taskViewModel.completedCount.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = selectedTab == AppTab.CHAT,
        drawerContent = {
            ChatDrawerContent(
                sessions = sessions,
                activeSessionId = currentSessionId,
                onSelectSession = { id ->
                    chatViewModel.selectSession(id)
                },
                onNewChatClick = {
                    chatViewModel.startNewSession()
                },
                onRenameSession = { id, title ->
                    chatViewModel.renameSession(id, title)
                },
                onDeleteSession = { id ->
                    chatViewModel.deleteSession(id)
                },
                onNavigateToTasks = {
                    selectedTab = AppTab.TASKS
                },
                onNavigateToStudio = { tool ->
                    activeStudioTool = tool
                    selectedTab = AppTab.STUDIO
                },
                onOpenVoiceCommand = {
                    showVoiceDialog = true
                },
                onOpenSettings = {
                    showSettingsDialog = true
                },
                onOpenDeveloperInfo = {
                    showDevInfoDialog = true
                },
                onOpenGoogleAuth = {
                    showGoogleAuthDialog = true
                },
                onOpenAdminPortal = {
                    showAdminPortalDialog = true
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            topBar = {
                if (selectedTab != AppTab.CHAT) {
                    PakAITopBar(
                        currentTabTitle = selectedTab.title,
                        onInfoClick = { showDevInfoDialog = true },
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onSettingsClick = { showSettingsDialog = true }
                    )
                }
            },
            floatingActionButton = {
                if (selectedTab != AppTab.CHAT) {
                    FloatingActionButton(
                        onClick = { showVoiceDialog = true },
                        containerColor = PakNeonGreen,
                        contentColor = Color(0xFF00391C),
                        elevation = FloatingActionButtonDefaults.elevation(6.dp),
                        modifier = Modifier.testTag("global_voice_fab")
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice Assistant")
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    // Tab 1: Chat
                    NavigationBarItem(
                        selected = selectedTab == AppTab.CHAT,
                        onClick = { selectedTab = AppTab.CHAT },
                        icon = {
                            Icon(
                                if (selectedTab == AppTab.CHAT) Icons.Filled.Chat else Icons.Outlined.Chat,
                                contentDescription = "AI Chat"
                            )
                        },
                        label = {
                            Text(
                                text = "AI Chat",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == AppTab.CHAT) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00391C),
                            selectedTextColor = PakNeonGreen,
                            indicatorColor = PakNeonGreen,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_chat_tab")
                    )

                    // Tab 2: AI Studio (PDF, PPT, Photo Editor, 100+ Languages)
                    NavigationBarItem(
                        selected = selectedTab == AppTab.STUDIO,
                        onClick = { selectedTab = AppTab.STUDIO },
                        icon = {
                            Icon(
                                if (selectedTab == AppTab.STUDIO) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                contentDescription = "AI Studio"
                            )
                        },
                        label = {
                            Text(
                                text = "AI Studio",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == AppTab.STUDIO) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00391C),
                            selectedTextColor = PakNeonGreen,
                            indicatorColor = PakNeonGreen,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_studio_tab")
                    )

                    // Tab 3: Task Management
                    NavigationBarItem(
                        selected = selectedTab == AppTab.TASKS,
                        onClick = { selectedTab = AppTab.TASKS },
                        icon = {
                            Icon(
                                if (selectedTab == AppTab.TASKS) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                                contentDescription = "Tasks"
                            )
                        },
                        label = {
                            Text(
                                text = "Tasks",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == AppTab.TASKS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00391C),
                            selectedTextColor = PakNeonGreen,
                            indicatorColor = PakNeonGreen,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tasks_tab")
                    )

                    // Tab 4: Pak AI Hub
                    NavigationBarItem(
                        selected = selectedTab == AppTab.HUB,
                        onClick = { selectedTab = AppTab.HUB },
                        icon = {
                            Icon(
                                if (selectedTab == AppTab.HUB) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Pak AI Hub"
                            )
                        },
                        label = {
                            Text(
                                text = "Pak AI Hub",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == AppTab.HUB) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00391C),
                            selectedTextColor = PakNeonGreen,
                            indicatorColor = PakNeonGreen,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_hub_tab")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = selectedTab, label = "TabSwitch") { tab ->
                    when (tab) {
                        AppTab.CHAT -> {
                            ChatScreen(
                                viewModel = chatViewModel,
                                onOpenDrawer = { scope.launch { drawerState.open() } },
                                onOpenSettings = { showSettingsDialog = true }
                            )
                        }
                        AppTab.STUDIO -> {
                            ToolsStudioScreen(
                                repository = chatViewModel.repository,
                                initialTool = activeStudioTool,
                                initialTopic = studioInitialTopic,
                                onSaveTask = { taskTitle ->
                                    taskViewModel.addTask(
                                        title = taskTitle,
                                        description = "Created from 100+ Languages Polyglot Studio",
                                        category = "Study",
                                        priority = "Medium",
                                        dueDate = "Today"
                                    )
                                }
                            )
                        }
                        AppTab.TASKS -> {
                            TaskManagerScreen(viewModel = taskViewModel)
                        }
                        AppTab.HUB -> {
                            HubScreen(
                                isApiKeyConfigured = chatViewModel.isApiKeyConfigured(),
                                totalTasks = totalTasks,
                                completedTasks = completedTasks,
                                onOpenDeveloperDialog = { showDevInfoDialog = true },
                                onOpenSettings = { showSettingsDialog = true },
                                onClearChat = { chatViewModel.clearCurrentChat() },
                                onClearCompletedTasks = { taskViewModel.clearCompletedTasks() },
                                onOpenStudioTool = { tool ->
                                    activeStudioTool = tool
                                    selectedTab = AppTab.STUDIO
                                },
                                onOpenVoiceAssistant = {
                                    showVoiceDialog = true
                                },
                                onOpenUpgrade = {
                                    showUpgradeDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Voice Command Assistant Dialog
    if (showVoiceDialog) {
        VoiceCommandDialog(
            repository = chatViewModel.repository,
            onDismiss = { showVoiceDialog = false },
            onNavigateToPdf = { topic ->
                studioInitialTopic = topic
                activeStudioTool = StudioTool.PDF_MAKER
                selectedTab = AppTab.STUDIO
            },
            onNavigateToPpt = { topic ->
                studioInitialTopic = topic
                activeStudioTool = StudioTool.PPT_MAKER
                selectedTab = AppTab.STUDIO
            },
            onNavigateToPhoto = {
                activeStudioTool = StudioTool.PHOTO_EDITOR
                selectedTab = AppTab.STUDIO
            },
            onNavigateToTranslator = { text ->
                activeStudioTool = StudioTool.POLYGLOT
                selectedTab = AppTab.STUDIO
            }
        )
    }

    // Comprehensive ChatGPT Settings Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            repository = chatViewModel.repository,
            onDismiss = { showSettingsDialog = false },
            onExportHistory = {
                val exportText = chatViewModel.exportChatHistoryText()
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, exportText)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Export Pak AI Chat History"))
            },
            onClearAllChats = {
                chatViewModel.clearAllChats()
            },
            onClearCompletedTasks = {
                taskViewModel.clearCompletedTasks()
            }
        )
    }

    // Developer Muhammad Ali spotlight dialog
    if (showDevInfoDialog) {
        DeveloperInfoDialog(
            isApiKeyConfigured = chatViewModel.isApiKeyConfigured(),
            onSaveCustomKey = { newKey ->
                chatViewModel.setCustomApiKey(newKey)
            },
            onDismiss = { showDevInfoDialog = false }
        )
    }

    // Upgrade Membership Dialog
    if (showUpgradeDialog) {
        UpgradeDialog(
            repository = chatViewModel.repository,
            onDismiss = { showUpgradeDialog = false }
        )
    }

    // Google Authentication Dialog
    if (showGoogleAuthDialog) {
        GoogleAuthDialog(
            repository = chatViewModel.repository,
            onDismiss = { showGoogleAuthDialog = false }
        )
    }

    // End-to-End Encrypted Admin Portal Dialog
    if (showAdminPortalDialog) {
        EncryptedAdminPortalDialog(
            repository = chatViewModel.repository,
            onDismiss = { showAdminPortalDialog = false }
        )
    }
}
