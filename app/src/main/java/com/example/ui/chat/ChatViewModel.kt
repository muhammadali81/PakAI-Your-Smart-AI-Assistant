package com.example.ui.chat

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.TaskEntity
import com.example.data.repository.PakAiRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    val repository = PakAiRepository(application)

    // Sessions
    val sessions: StateFlow<List<ChatSessionEntity>> = repository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentSessionId = MutableStateFlow<Long>(1L)
    val currentSessionId: StateFlow<Long> = _currentSessionId.asStateFlow()

    // Messages reactive to currentSessionId
    val messages: StateFlow<List<ChatMessageEntity>> = _currentSessionId
        .flatMapLatest { sessionId ->
            repository.getMessagesForSession(sessionId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _toastNotification = MutableStateFlow<String?>(null)
    val toastNotification: StateFlow<String?> = _toastNotification.asStateFlow()

    private var tts: TextToSpeech? = null
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _selectedModel = MutableStateFlow(repository.getSelectedModel())
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    init {
        tts = TextToSpeech(application, this)
        initDefaultSessionIfNeeded()
    }

    private fun initDefaultSessionIfNeeded() {
        viewModelScope.launch {
            // Check if there are sessions, if not create first session
            sessions.collect { list ->
                if (list.isEmpty()) {
                    val id = repository.createNewSession("Pak AI Assistant")
                    _currentSessionId.value = id
                } else if (_currentSessionId.value == 1L && list.none { it.id == 1L }) {
                    _currentSessionId.value = list.first().id
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.ENGLISH
            applyTtsSettings()
        }
    }

    private fun applyTtsSettings() {
        tts?.setSpeechRate(repository.getTtsSpeed())
        tts?.setPitch(repository.getTtsPitch())
    }

    fun isApiKeyConfigured(): Boolean = repository.isApiKeyConfigured()

    fun setCustomApiKey(key: String) {
        repository.setCustomApiKey(key)
        _toastNotification.value = "Pak AI Engine Key saved!"
    }

    fun setSelectedModel(model: String) {
        repository.setSelectedModel(model)
        _selectedModel.value = model
        _toastNotification.value = "Model set to $model"
    }

    fun clearToastNotification() {
        _toastNotification.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // --- Session Operations ---
    fun startNewSession(title: String = "New Chat") {
        viewModelScope.launch {
            val newId = repository.createNewSession(title)
            _currentSessionId.value = newId
            _toastNotification.value = "New conversation started"
        }
    }

    fun selectSession(sessionId: Long) {
        _currentSessionId.value = sessionId
    }

    fun renameSession(sessionId: Long, title: String) {
        viewModelScope.launch {
            repository.updateSessionTitle(sessionId, title)
            _toastNotification.value = "Chat renamed"
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            val currentList = sessions.value.filter { it.id != sessionId }
            if (currentList.isNotEmpty()) {
                _currentSessionId.value = currentList.first().id
            } else {
                val newId = repository.createNewSession("New Chat")
                _currentSessionId.value = newId
            }
            _toastNotification.value = "Chat deleted"
        }
    }

    // --- Chat Messaging ---
    fun sendMessage(text: String) {
        sendMessageWithAttachment(text = text, attachmentType = null, attachmentName = null, base64Image = null, fileContentSnippet = null)
    }

    fun sendMessageWithAttachment(
        text: String,
        attachmentType: String? = null,
        attachmentName: String? = null,
        base64Image: String? = null,
        fileContentSnippet: String? = null
    ) {
        val trimmed = text.trim()
        if (trimmed.isBlank() && base64Image == null && fileContentSnippet == null) return
        if (_isLoading.value) return

        val sessionId = _currentSessionId.value

        viewModelScope.launch {
            _errorMessage.value = null

            val displayUserText = buildString {
                if (attachmentType == "image") {
                    append("📷 [Photo Attached: ${attachmentName ?: "Image"}]\n")
                } else if (attachmentType == "file") {
                    append("📄 [File Attached: ${attachmentName ?: "Document"}]\n")
                }
                append(trimmed.ifBlank { if (attachmentType == "image") "Analyze this photo" else "Analyze this attached file" })
            }

            // 1. Auto-update session title
            val currentSessions = sessions.value
            val currentSession = currentSessions.find { it.id == sessionId }
            if (currentSession != null && (currentSession.title == "New Chat" || currentSession.title == "Pak AI Assistant")) {
                val autoTitle = if (trimmed.length > 28) trimmed.take(28) + "..." else if (trimmed.isNotBlank()) trimmed else (attachmentName ?: "Attachment Analysis")
                repository.updateSessionTitle(sessionId, autoTitle)
            }

            // 2. Save user message locally
            val userMsg = ChatMessageEntity(
                sessionId = sessionId,
                role = "user",
                content = displayUserText
            )
            repository.saveMessage(userMsg)

            _isLoading.value = true

            // 3. Check for Photo Creation prompt intent or query AI
            val isPhotoCreationIntent = trimmed.startsWith("/image", ignoreCase = true) ||
                    trimmed.startsWith("/photo", ignoreCase = true) ||
                    trimmed.contains("create photo", ignoreCase = true) ||
                    trimmed.contains("generate photo", ignoreCase = true) ||
                    trimmed.contains("generate image", ignoreCase = true) ||
                    trimmed.contains("draw photo", ignoreCase = true) ||
                    trimmed.contains("photo banao", ignoreCase = true) ||
                    trimmed.contains("tasveer banao", ignoreCase = true) ||
                    trimmed.contains("تصویر بنائیں", ignoreCase = true)

            val result = if (base64Image != null) {
                val prompt = trimmed.ifBlank { "Analyze this uploaded photo with detail: identify objects, text (OCR), colors, and provide intelligent insights." }
                val visionResult = repository.analyzeImage(base64Image, prompt)
                if (isPhotoCreationIntent && visionResult.isSuccess) {
                    val cleanPrompt = trimmed.replace(Regex("(?i)^(/image|/photo|create photo|generate photo|generate image|draw photo|photo banao|tasveer banao|تصویر بنائیں)\\s*"), "").trim().ifBlank { "creative photorealistic transformation" }
                    val photoUrl = repository.generatePhotoUrl(cleanPrompt)
                    val combinedReply = buildString {
                        append("🎨 **Photo Analysis & Generation:**\n\n")
                        append(visionResult.getOrNull() ?: "")
                        append("\n\n---\n### 🖼️ Created Photo according to Prompt:\n\n")
                        append("![$cleanPrompt]($photoUrl)\n\n")
                        append("✨ **Prompt:** $cleanPrompt\n")
                        append("📌 *Image rendered live. Tap to view or download.*")
                    }
                    Result.success(combinedReply)
                } else {
                    visionResult
                }
            } else if (isPhotoCreationIntent) {
                val cleanPrompt = trimmed.replace(Regex("(?i)^(/image|/photo|create photo of|create photo|generate photo of|generate photo|generate image of|generate image|draw photo of|draw photo|photo banao|tasveer banao|تصویر بنائیں)\\s*"), "").trim().ifBlank { trimmed }
                val photoUrl = repository.generatePhotoUrl(cleanPrompt)
                val reply = buildString {
                    append("### 🖼️ Created Photo according to Prompt:\n\n")
                    append("![$cleanPrompt]($photoUrl)\n\n")
                    append("✨ **Prompt:** $cleanPrompt  \n")
                    append("🎨 **Engine:** Pak AI Neural Visual Core (1024x1024 Photorealistic)  \n")
                    append("💡 *Tip: You can open this photo in Photo Studio for filters, cropping, and instant export!*")
                }
                Result.success(reply)
            } else {
                val queryText = if (fileContentSnippet != null) {
                    "$trimmed\n\n--- Attached File Content ($attachmentName) ---\n$fileContentSnippet"
                } else {
                    trimmed
                }
                val currentHistory = messages.value
                repository.sendMessage(
                    userMessage = queryText,
                    conversationHistory = currentHistory,
                    overrideModel = _selectedModel.value
                )
            }

            if (result.isSuccess) {
                val botReply = result.getOrNull() ?: "I'm here to help!"
                val botMsg = ChatMessageEntity(
                    sessionId = sessionId,
                    role = "model",
                    content = botReply
                )
                repository.saveMessage(botMsg)
            } else {
                val error = result.exceptionOrNull()?.localizedMessage ?: "Unknown error occurred"
                _errorMessage.value = error
                val errorMsg = ChatMessageEntity(
                    sessionId = sessionId,
                    role = "model",
                    content = "⚠️ $error\n\nPlease check your Pak AI Engine Key in Settings (gear icon) or ensure network connectivity."
                )
                repository.saveMessage(errorMsg)
            }

            _isLoading.value = false
        }
    }

    fun regenerateLastResponse() {
        val currentMsgs = messages.value
        val lastUserMsg = currentMsgs.lastOrNull { it.role == "user" } ?: return
        sendMessage(lastUserMsg.content)
    }

    fun editAndResubmitMessage(messageId: Long, newText: String) {
        viewModelScope.launch {
            repository.updateMessageContent(messageId, newText)
            sendMessage(newText)
        }
    }

    fun setMessageFeedback(messageId: Long, feedback: Int) {
        viewModelScope.launch {
            repository.updateMessageFeedback(messageId, feedback)
            val text = if (feedback > 0) "Thanks for the feedback!" else "Feedback recorded"
            _toastNotification.value = text
        }
    }

    fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
        }
    }

    fun speakText(text: String) {
        if (_isSpeaking.value) {
            tts?.stop()
            _isSpeaking.value = false
        } else {
            applyTtsSettings()
            val cleanSpeech = text.replace(Regex("""[*#_`>~]"""), "")
            tts?.speak(cleanSpeech, TextToSpeech.QUEUE_FLUSH, null, "PakAITTS")
            _isSpeaking.value = true
        }
    }

    fun convertAiMessageToTasks(content: String) {
        viewModelScope.launch {
            val tasks = repository.parseTasksFromAiText(content)
            if (tasks.isNotEmpty()) {
                repository.insertTasks(tasks)
                _toastNotification.value = "Successfully added ${tasks.size} task(s) to Task Manager!"
            } else {
                val summary = if (content.length > 50) content.take(50) + "..." else content
                val singleTask = TaskEntity(
                    title = summary,
                    description = content,
                    category = "General",
                    priority = "Medium",
                    source = "ai_assistant"
                )
                repository.insertTask(singleTask)
                _toastNotification.value = "Added task to Task Manager!"
            }
        }
    }

    fun clearCurrentChat() {
        viewModelScope.launch {
            tts?.stop()
            _isSpeaking.value = false
            repository.deleteMessagesForSession(_currentSessionId.value)
            _toastNotification.value = "Current chat cleared"
        }
    }

    fun clearAllChats() {
        viewModelScope.launch {
            tts?.stop()
            _isSpeaking.value = false
            repository.clearAllSessions()
            val newId = repository.createNewSession("New Chat")
            _currentSessionId.value = newId
            _toastNotification.value = "All chat sessions cleared"
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            repository.clearCompletedTasks()
        }
    }

    fun exportChatHistoryText(): String {
        val currentMsgs = messages.value
        val builder = StringBuilder()
        builder.append("=== Pak AI Chat Export ===\n")
        builder.append("Developer: Muhammad Ali (alimuhammadhvn81@gmail.com)\n")
        builder.append("Model: ${_selectedModel.value}\n")
        builder.append("Exported: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(java.util.Date())}\n\n")

        for (msg in currentMsgs) {
            val sender = if (msg.role == "user") "You" else "Pak AI"
            builder.append("[$sender]:\n${msg.content}\n\n")
        }
        return builder.toString()
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
