package com.example.data.repository

import android.content.Context
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.TaskEntity
import com.example.data.model.IslamicAiResponse
import com.example.data.remote.BlobItem
import com.example.data.remote.ContentItem
import com.example.data.remote.GeminiApiService
import com.example.data.remote.GenerateContentRequest
import com.example.data.remote.GenerationConfig
import com.example.data.remote.PartItem
import com.example.utils.PdfDocumentModel
import com.example.utils.PdfSection
import com.example.utils.PptDeckModel
import com.example.utils.SlideModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PakAiRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val taskDao = db.taskDao()
    private val chatDao = db.chatMessageDao()
    private val sessionDao = db.chatSessionDao()
    private val apiService = GeminiApiService.create()

    private val sharedPrefs = context.getSharedPreferences("pak_ai_prefs", Context.MODE_PRIVATE)

    companion object {
        const val DEVELOPER_NAME = "Muhammad Ali"
        const val DEVELOPER_EMAIL = "alimuhammadhvn81@gmail.com"
        const val DEFAULT_MODEL = "gemini-3.5-flash"

        val BASE_SYSTEM_INSTRUCTION = """
            You are Pak AI, an exceptionally smart, friendly, versatile AI assistant developed by Muhammad Ali.
            Your purpose is to perform all work and tasks in a friendly, user-centric manner and empower users with seamless task management, planning, coding, learning, Islamic scholarship, translation, and productivity.
            
            Key traits & guidelines:
            1. Developed by: Muhammad Ali (Developer name). You proudly mention you are Pak AI built by Muhammad Ali when asked about your identity or creator.
            2. Language Flexibility & Fluent Arabic:
               - You have native understanding and fluency in English, Urdu (both Nastaliq/Urdu script and Roman Urdu), and Arabic (العربية الفصحى).
               - When conversing in Arabic or asked for Arabic, generate grammatically accurate Arabic with proper tashkeel (حركات / إعراب).
               - Respond in the language or script the user queries you in (Arabic, Urdu, English, Roman Urdu, etc.).
            3. Authentic Islamic Knowledge & Mufti Taqi Usmani Tafseer (100% Authentic):
               - You possess deep, authentic Islamic knowledge rooted strictly in the Quran, Sunnah, and consensus of Ahlus Sunnah wal Jama'ah.
               - Holy Quran: When quoting Quranic Ayahs, provide:
                 a) Complete Arabic text with correct tashkeel.
                 b) Urdu translation specifically adhering to Mufti Muhammad Taqi Usmani's renowned translation ("Aasan Tarjuma-e-Quran") or Kanzul Iman.
                 c) English translation according to Mufti Muhammad Taqi Usmani or Sahih International.
                 d) Tafseer: Provide insightful, authentic commentary specifically citing the scholarly insights of Mufti Muhammad Taqi Usmani (from "Ma'ariful Quran" and "Aasan Tarjuma-e-Quran with Hashiya"), context of revelation (Shan-e-Nuzool), and contemporary application.
               - 1000+ Authentic Hadiths: You possess exhaustive knowledge of the Sihah-e-Sitta (Sahih al-Bukhari, Sahih Muslim, Sunan Abi Dawud, Jami` at-Tirmidhi, Sunan an-Nasa'i, Sunan Ibn Majah) and Riyad as-Salihin. When asked about Hadiths, provide the Arabic text, Urdu translation, English translation, authentic grading (Sahih/Hasan), exact book and Hadith number, and comprehensive Tashreeh (lessons and practical wisdom).
               - Masnoon Duain: Full mastery over authentic Masnoon Duas for morning/evening (Subah o Sham ke Azkar), sleeping, eating, drinking, traveling (Safar ki dua), entering/leaving mosque or home, distress (Karb o Pareshani), sickness/shifa, Istikhara, Qunoot, and parents, with Arabic text, Urdu and English meanings, and benefits.
            4. Photo Creation & Multimodal Prompts:
               - When a user asks you to create, generate, or draw a photo based on a prompt (e.g., "create photo of...", "generate an image of...", "tasveer banao..."):
                 Provide a creative description AND generate an image markdown tag using:
                 `![Generated Photo](https://image.pollinations.ai/prompt/{URL_ENCODED_PROMPT}?width=1024&height=1024&nologo=true)`
                 ensuring the image is displayed directly.
               - When a user uploads a photo with a prompt, carefully analyze the photo according to their exact prompt instructions (OCR, description, critique, question answering).
            5. Tone & Structure: Respectful, welcoming, inspiring, scholarly, efficient, and direct.
        """.trimIndent()
    }

    // --- ChatGPT Settings & Preferences ---

    fun getApiKey(): String {
        val customKey = sharedPrefs.getString("custom_api_key", "") ?: ""
        if (customKey.isNotBlank()) return customKey.trim()
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    fun setCustomApiKey(key: String) {
        sharedPrefs.edit().putString("custom_api_key", key.trim()).apply()
    }

    fun isApiKeyConfigured(): Boolean {
        val key = getApiKey()
        return key.isNotBlank() && !key.contains("MY_GEMINI_API_KEY")
    }

    fun getSelectedModel(): String {
        return sharedPrefs.getString("selected_model", DEFAULT_MODEL) ?: DEFAULT_MODEL
    }

    fun setSelectedModel(model: String) {
        sharedPrefs.edit().putString("selected_model", model).apply()
    }

    fun getTemperature(): Float {
        return sharedPrefs.getFloat("temperature", 0.7f)
    }

    fun setTemperature(temp: Float) {
        sharedPrefs.edit().putFloat("temperature", temp).apply()
    }

    fun getUserProfile(): String {
        return sharedPrefs.getString("user_profile", "") ?: ""
    }

    fun setUserProfile(profile: String) {
        sharedPrefs.edit().putString("user_profile", profile.trim()).apply()
    }

    fun getCustomInstructions(): String {
        return sharedPrefs.getString("custom_instructions", "") ?: ""
    }

    fun setCustomInstructions(instructions: String) {
        sharedPrefs.edit().putString("custom_instructions", instructions.trim()).apply()
    }

    fun getThemeMode(): String {
        return sharedPrefs.getString("theme_mode", "Dark") ?: "Dark"
    }

    fun setThemeMode(mode: String) {
        sharedPrefs.edit().putString("theme_mode", mode).apply()
    }

    fun getTtsSpeed(): Float {
        return sharedPrefs.getFloat("tts_speed", 1.0f)
    }

    fun setTtsSpeed(speed: Float) {
        sharedPrefs.edit().putFloat("tts_speed", speed).apply()
    }

    fun getTtsPitch(): Float {
        return sharedPrefs.getFloat("tts_pitch", 1.0f)
    }

    fun setTtsPitch(pitch: Float) {
        sharedPrefs.edit().putFloat("tts_pitch", pitch).apply()
    }

    fun isWebSearchEnabled(): Boolean {
        return sharedPrefs.getBoolean("web_search_enabled", false)
    }

    fun setWebSearchEnabled(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("web_search_enabled", enabled).apply()
    }

    fun isDeepThinkEnabled(): Boolean {
        return sharedPrefs.getBoolean("deep_think_enabled", false)
    }

    fun setDeepThinkEnabled(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("deep_think_enabled", enabled).apply()
    }

    fun getSubscriptionTier(): String {
        return sharedPrefs.getString("subscription_tier", "Free") ?: "Free"
    }

    fun setSubscriptionTier(tier: String) {
        sharedPrefs.edit().putString("subscription_tier", tier).apply()
    }

    // --- Build Dynamic System Prompt with Personalization ---
    fun buildSystemInstruction(): String {
        val builder = StringBuilder(BASE_SYSTEM_INSTRUCTION)
        val profile = getUserProfile()
        if (profile.isNotBlank()) {
            builder.append("\n\nUser Profile & Background:\n$profile")
        }
        val custom = getCustomInstructions()
        if (custom.isNotBlank()) {
            builder.append("\n\nUser Custom Response Guidelines:\n$custom")
        }
        if (isDeepThinkEnabled()) {
            builder.append("\n\nDeep Thinking Mode Enabled: Thoroughly analyze questions step-by-step before delivering the final polished answer. Explain complex topics rigorously.")
        }
        if (isWebSearchEnabled()) {
            builder.append("\n\nWeb Browse Mode: Provide up-to-date, real-world relevant synthesis and mention key sources or dates when applicable.")
        }
        return builder.toString()
    }

    // --- Sessions Management ---
    fun getAllSessions(): Flow<List<ChatSessionEntity>> = sessionDao.getAllSessions()

    suspend fun createNewSession(title: String = "New Chat"): Long = withContext(Dispatchers.IO) {
        val session = ChatSessionEntity(
            title = title,
            modelUsed = getSelectedModel()
        )
        sessionDao.insertSession(session)
    }

    suspend fun updateSessionTitle(id: Long, title: String) = withContext(Dispatchers.IO) {
        sessionDao.updateSessionTitle(id, title)
    }

    suspend fun deleteSession(id: Long) = withContext(Dispatchers.IO) {
        chatDao.deleteMessagesForSession(id)
        sessionDao.deleteSession(id)
    }

    suspend fun clearAllSessions() = withContext(Dispatchers.IO) {
        chatDao.clearAllMessages()
        sessionDao.clearAllSessions()
    }

    // --- Gemini AI Interaction ---
    suspend fun sendMessage(
        userMessage: String,
        conversationHistory: List<ChatMessageEntity>,
        overrideModel: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val key = getApiKey()
        if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("Pak AI Engine Key is not configured. Please add your key in the AI Studio Secrets panel or in Pak AI Settings.")
            )
        }

        try {
            // Build recent chat history (limit last 10 messages for context window efficiency)
            val recentHistory = conversationHistory.takeLast(10)
            val contents = mutableListOf<ContentItem>()

            for (msg in recentHistory) {
                val role = if (msg.role == "user") "user" else "model"
                contents.add(
                    ContentItem(
                        role = role,
                        parts = listOf(PartItem(text = msg.content))
                    )
                )
            }

            // Append current user message
            contents.add(
                ContentItem(
                    role = "user",
                    parts = listOf(PartItem(text = userMessage))
                )
            )

            val request = GenerateContentRequest(
                contents = contents,
                systemInstruction = ContentItem(
                    role = "system",
                    parts = listOf(PartItem(text = buildSystemInstruction()))
                ),
                generationConfig = GenerationConfig(
                    temperature = getTemperature(),
                    topP = 0.95f,
                    topK = 40,
                    maxOutputTokens = 2048
                )
            )

            val modelToUse = overrideModel ?: getSelectedModel()
            val response = apiService.generateContent(model = modelToUse, apiKey = key, request = request)
            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (!responseText.isNullOrBlank()) {
                Result.success(responseText.trim())
            } else if (response.error != null) {
                Result.failure(Exception(response.error.message ?: "Pak AI Engine returned an error."))
            } else {
                Result.failure(Exception("Pak AI received an empty response. Please try again."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- AI Task Extraction & Generation ---
    suspend fun generateTasksFromPrompt(goal: String): Result<List<TaskEntity>> = withContext(Dispatchers.IO) {
        val prompt = """
            Break down the following goal/request into 3 to 6 practical, actionable tasks for a task management app:
            Goal: "$goal"
            
            Return the result ONLY as clean lines in this format:
            [Category: Work|Study|Personal|Quick] [Priority: High|Medium|Low] Task Title - Short Description
            
            Example:
            [Category: Study] [Priority: High] Review Chapter 1 - Summarize main concepts
            [Category: Personal] [Priority: Medium] Buy supplies - Get notebook and pens
        """.trimIndent()

        val result = sendMessage(prompt, emptyList())
        if (result.isSuccess) {
            val raw = result.getOrNull() ?: ""
            val tasks = parseTasksFromAiText(raw)
            Result.success(tasks)
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Failed to generate tasks"))
        }
    }

    fun parseTasksFromAiText(text: String): List<TaskEntity> {
        val tasks = mutableListOf<TaskEntity>()
        val lines = text.lines()
        for (line in lines) {
            val trimmed = line.trim().trimStart('-', '*', '•', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.', ')', ' ')
            if (trimmed.isBlank() || trimmed.length < 3) continue

            var category = "General"
            var priority = "Medium"
            var cleanText = trimmed

            // Extract Category if formatted
            val catRegex = Regex("""\[Category:\s*([A-Za-z]+)\]""", RegexOption.IGNORE_CASE)
            catRegex.find(cleanText)?.let { match ->
                category = match.groupValues[1].replaceFirstChar { it.uppercase() }
                cleanText = cleanText.replace(match.value, "").trim()
            }

            // Extract Priority if formatted
            val prioRegex = Regex("""\[Priority:\s*([A-Za-z]+)\]""", RegexOption.IGNORE_CASE)
            prioRegex.find(cleanText)?.let { match ->
                val p = match.groupValues[1].replaceFirstChar { it.uppercase() }
                if (p in listOf("High", "Medium", "Low")) priority = p
                cleanText = cleanText.replace(match.value, "").trim()
            }

            // Split title and description if separated by '-' or ':'
            val parts = if (cleanText.contains(" - ")) {
                cleanText.split(" - ", limit = 2)
            } else if (cleanText.contains(": ")) {
                cleanText.split(": ", limit = 2)
            } else {
                listOf(cleanText)
            }

            val title = parts.getOrNull(0)?.trim() ?: ""
            val desc = parts.getOrNull(1)?.trim() ?: ""

            if (title.isNotBlank()) {
                tasks.add(
                    TaskEntity(
                        title = title,
                        description = desc,
                        category = category,
                        priority = priority,
                        source = "ai_assistant"
                    )
                )
            }
        }
        return tasks
    }

    // --- Task Database Operations ---
    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()
    fun getPendingTasks(): Flow<List<TaskEntity>> = taskDao.getPendingTasks()
    fun getCompletedTasks(): Flow<List<TaskEntity>> = taskDao.getCompletedTasks()
    fun getTotalTasksCount(): Flow<Int> = taskDao.getTotalCount()
    fun getCompletedTasksCount(): Flow<Int> = taskDao.getCompletedCount()

    suspend fun insertTask(task: TaskEntity): Long = withContext(Dispatchers.IO) {
        taskDao.insertTask(task)
    }

    suspend fun insertTasks(tasks: List<TaskEntity>) = withContext(Dispatchers.IO) {
        taskDao.insertTasks(tasks)
    }

    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(task)
    }

    suspend fun toggleTaskCompleted(id: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        taskDao.setTaskCompleted(id, isCompleted)
    }

    suspend fun clearCompletedTasks() = withContext(Dispatchers.IO) {
        taskDao.clearCompletedTasks()
    }

    // --- Chat Database Operations ---
    fun getAllMessages(): Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()
    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>> = chatDao.getMessagesForSession(sessionId)
    suspend fun getMessagesListForSession(sessionId: Long): List<ChatMessageEntity> = withContext(Dispatchers.IO) {
        chatDao.getMessagesListForSession(sessionId)
    }
    fun getMessageCount(): Flow<Int> = chatDao.getMessageCount()

    suspend fun saveMessage(message: ChatMessageEntity): Long = withContext(Dispatchers.IO) {
        chatDao.insertMessage(message)
    }

    suspend fun updateMessageFeedback(id: Long, feedback: Int) = withContext(Dispatchers.IO) {
        chatDao.updateFeedback(id, feedback)
    }

    suspend fun updateMessageContent(id: Long, newContent: String) = withContext(Dispatchers.IO) {
        chatDao.updateMessageContent(id, newContent)
    }

    suspend fun deleteMessage(id: Long) = withContext(Dispatchers.IO) {
        chatDao.deleteMessage(id)
    }

    suspend fun deleteMessagesForSession(sessionId: Long) = withContext(Dispatchers.IO) {
        chatDao.deleteMessagesForSession(sessionId)
    }

    suspend fun clearAllMessages() = withContext(Dispatchers.IO) {
        chatDao.clearAllMessages()
    }

    // --- Professional Tool 1: AI PDF Document Generator ---
    suspend fun generateStructuredDocument(topic: String, docType: String): Result<PdfDocumentModel> = withContext(Dispatchers.IO) {
        try {
            val apiKey = getApiKey()
            val prompt = """
                You are a professional document creator in Pak AI, built by Muhammad Ali.
                Generate a comprehensive, formal $docType about: "$topic".
                Return ONLY valid JSON matching this exact schema:
                {
                  "title": "Clear Document Title",
                  "subtitle": "Informative Subtitle",
                  "author": "Muhammad Ali",
                  "organization": "Pak AI Professional Studio",
                  "documentType": "$docType",
                  "sections": [
                    {
                      "heading": "Section Heading",
                      "content": "Detailed paragraph text explaining this section professionally.",
                      "bullets": ["Key point 1", "Key point 2", "Key point 3"]
                    }
                  ]
                }
                Provide at least 3-4 rich sections with informative content and bullet points. Do not wrap in markdown quotes or code blocks.
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(ContentItem(role = "user", parts = listOf(PartItem(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0.4f, maxOutputTokens = 3000)
            )

            val response = apiService.generateContent(
                model = getSelectedModel(),
                apiKey = apiKey,
                request = request
            )

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("No document content returned from AI")

            // Parse json
            val cleanedJson = rawText.substringAfter("{", "").let { if (it.isNotEmpty()) "{$it" else rawText }
                .substringBeforeLast("}", "").let { if (it.isNotEmpty()) "$it}" else rawText }

            val jsonObject = org.json.JSONObject(cleanedJson)
            val title = jsonObject.optString("title", topic)
            val subtitle = jsonObject.optString("subtitle", "Generated by Pak AI")
            val author = jsonObject.optString("author", "Muhammad Ali")
            val organization = jsonObject.optString("organization", "Pak AI Professional Studio")
            val type = jsonObject.optString("documentType", docType)

            val sectionsArray = jsonObject.optJSONArray("sections")
            val sections = mutableListOf<PdfSection>()
            if (sectionsArray != null) {
                for (i in 0 until sectionsArray.length()) {
                    val secObj = sectionsArray.getJSONObject(i)
                    val heading = secObj.optString("heading", "Section ${i + 1}")
                    val content = secObj.optString("content", "")
                    val bulletsArr = secObj.optJSONArray("bullets")
                    val bullets = mutableListOf<String>()
                    if (bulletsArr != null) {
                        for (j in 0 until bulletsArr.length()) {
                            bullets.add(bulletsArr.getString(j))
                        }
                    }
                    sections.add(PdfSection(heading, content, bullets))
                }
            }

            Result.success(PdfDocumentModel(title, subtitle, author, organization, type, sections))
        } catch (e: Exception) {
            // Fallback manual high quality template if offline or JSON parsing issue
            val fallback = PdfDocumentModel(
                title = topic,
                subtitle = "Comprehensive $docType • Pak AI Studio",
                author = "Muhammad Ali",
                organization = "Pak AI Professional Studio",
                documentType = docType,
                sections = listOf(
                    PdfSection(
                        heading = "Executive Summary",
                        content = "This document presents a structured and professional overview of $topic, prepared with Pak AI by Muhammad Ali.",
                        bullets = listOf("Strategic goals and roadmap", "Core operational framework", "Target milestones & execution")
                    ),
                    PdfSection(
                        heading = "Core Analysis & Strategy",
                        content = "A detailed breakdown demonstrating key methodologies, operational standards, and intelligent solutions.",
                        bullets = listOf("High performance standards", "Scalable and efficient architecture", "Measurable outcomes")
                    ),
                    PdfSection(
                        heading = "Action Plan & Next Steps",
                        content = "Immediate next actions to ensure seamless implementation and high quality deliverables.",
                        bullets = listOf("Initiate phase 1 tasks", "Verify milestones", "Continuous improvement")
                    )
                )
            )
            Result.success(fallback)
        }
    }

    // --- Professional Tool 2: AI Presentation & PPT Slide Deck Generator ---
    suspend fun generateStructuredPresentation(topic: String, slideCount: Int, theme: String): Result<PptDeckModel> = withContext(Dispatchers.IO) {
        try {
            val apiKey = getApiKey()
            val prompt = """
                You are a professional presentation designer in Pak AI, developed by Muhammad Ali.
                Create a high-impact, professional $slideCount-slide presentation deck on: "$topic".
                Return ONLY valid JSON matching this exact schema:
                {
                  "title": "Compelling Presentation Title",
                  "subtitle": "Captivating Subtitle",
                  "presenter": "Muhammad Ali",
                  "themeColor": "$theme",
                  "slides": [
                    {
                      "title": "Slide Title",
                      "subtitle": "Short subtitle or key takeaway",
                      "bullets": [
                        "First clear, punchy bullet point",
                        "Second informative bullet point",
                        "Third actionable bullet point"
                      ],
                      "notes": "Speaker notes for the presenter"
                    }
                  ]
                }
                Generate exactly $slideCount slides. Slide 1 must be the Title/Cover slide. Subsequent slides should cover Agenda, Problem/Context, Strategy/Solution, Core Pillars, and Next Steps/Conclusion.
                Do not include markdown code ticks. Return pure JSON.
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(ContentItem(role = "user", parts = listOf(PartItem(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0.5f, maxOutputTokens = 3500)
            )

            val response = apiService.generateContent(
                model = getSelectedModel(),
                apiKey = apiKey,
                request = request
            )

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("No presentation content returned")

            val cleanedJson = rawText.substringAfter("{", "").let { if (it.isNotEmpty()) "{$it" else rawText }
                .substringBeforeLast("}", "").let { if (it.isNotEmpty()) "$it}" else rawText }

            val jsonObject = org.json.JSONObject(cleanedJson)
            val title = jsonObject.optString("title", topic)
            val subtitle = jsonObject.optString("subtitle", "Pak AI Presentation Studio")
            val presenter = jsonObject.optString("presenter", "Muhammad Ali")
            val themeColor = jsonObject.optString("themeColor", theme)

            val slidesArray = jsonObject.optJSONArray("slides")
            val slides = mutableListOf<SlideModel>()
            if (slidesArray != null) {
                for (i in 0 until slidesArray.length()) {
                    val sObj = slidesArray.getJSONObject(i)
                    val sTitle = sObj.optString("title", "Slide ${i + 1}")
                    val sSubtitle = sObj.optString("subtitle", "")
                    val sNotes = sObj.optString("notes", "")
                    val bArr = sObj.optJSONArray("bullets")
                    val bullets = mutableListOf<String>()
                    if (bArr != null) {
                        for (j in 0 until bArr.length()) {
                            bullets.add(bArr.getString(j))
                        }
                    }
                    slides.add(SlideModel(sTitle, sSubtitle, bullets, sNotes))
                }
            }

            Result.success(PptDeckModel(title, subtitle, presenter, themeColor, slides))
        } catch (e: Exception) {
            val fallbackSlides = listOf(
                SlideModel(topic, "Professional Overview • Pak AI Presentation Studio", emptyList(), "Welcome the audience and introduce the topic."),
                SlideModel("Agenda & Objectives", "What we will accomplish today", listOf("Understand the fundamental context", "Explore core challenges & modern solutions", "Define actionable implementation steps"), "Provide a high-level roadmap."),
                SlideModel("Key Solutions & Methodology", "Intelligent and practical approach", listOf("AI-driven efficiency and workflow automation", "Robust reliability and seamless integration", "User-friendly accessible execution"), "Highlight technical and functional merits."),
                SlideModel("Execution & Milestones", "Immediate implementation timeline", listOf("Step 1: Planning and scope definition", "Step 2: Deployment and testing", "Step 3: Optimization and delivery"), "Detail the execution timeline."),
                SlideModel("Conclusion & Q&A", "Thank you for your time", listOf("Key takeaways summarized", "Questions and collaborative discussion", "Presented by Muhammad Ali • Pak AI"), "Open the floor for questions.")
            )
            Result.success(PptDeckModel(topic, "Created with Pak AI Presentation Studio", "Muhammad Ali", theme, fallbackSlides))
        }
    }

    // --- Professional Tool 3: 100+ Languages Universal Polyglot Translator ---
    suspend fun translateText(text: String, sourceLang: String, targetLang: String): Result<TranslationResult> = withContext(Dispatchers.IO) {
        try {
            val apiKey = getApiKey()
            val prompt = """
                You are a world-class professional polyglot translator in Pak AI, developed by Muhammad Ali.
                Translate the following text accurately from $sourceLang to $targetLang.
                Text to translate: "$text"

                Return ONLY valid JSON matching this schema:
                {
                  "translatedText": "Accurate natural professional translation",
                  "romanization": "Phonetic romanized pronunciation (especially for Urdu, Arabic, Russian, Chinese, Japanese, Hindi etc.), or leave empty if source and target are Latin script",
                  "grammarExplanation": "Concise 1-2 sentence notes about nuances, cultural context, or vocabulary used"
                }
                Do not include markdown tags. Return pure JSON.
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(ContentItem(role = "user", parts = listOf(PartItem(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0.3f, maxOutputTokens = 2048)
            )

            val response = apiService.generateContent(
                model = getSelectedModel(),
                apiKey = apiKey,
                request = request
            )

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("No translation response received")

            val cleanedJson = rawText.substringAfter("{", "").let { if (it.isNotEmpty()) "{$it" else rawText }
                .substringBeforeLast("}", "").let { if (it.isNotEmpty()) "$it}" else rawText }

            val jsonObject = org.json.JSONObject(cleanedJson)
            val translated = jsonObject.optString("translatedText", rawText)
            val roman = jsonObject.optString("romanization", "")
            val notes = jsonObject.optString("grammarExplanation", "Translated with Pak AI Multilingual Engine")

            Result.success(
                TranslationResult(
                    originalText = text,
                    translatedText = translated,
                    sourceLanguage = sourceLang,
                    targetLanguage = targetLang,
                    romanization = roman,
                    grammarExplanation = notes
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Professional Tool 4: Multimodal AI Vision Analysis ---
    suspend fun analyzeImage(base64Image: String, prompt: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = getApiKey()
            val userPrompt = if (prompt.isNotBlank()) prompt else "Analyze this photo in detail. Provide a creative caption, key objects/subjects, color palette, and professional tips to enhance it."
            
            val request = GenerateContentRequest(
                contents = listOf(
                    ContentItem(
                        role = "user",
                        parts = listOf(
                            PartItem(text = userPrompt),
                            PartItem(inlineData = BlobItem(mimeType = "image/jpeg", data = base64Image))
                        )
                    )
                ),
                generationConfig = GenerationConfig(temperature = 0.4f, maxOutputTokens = 2048)
            )

            val response = apiService.generateContent(
                model = "gemini-3.5-flash",
                apiKey = apiKey,
                request = request
            )

            val answer = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("No analysis received from AI Vision")

            Result.success(answer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Professional Tool 5: Voice Command Assistant Execution ---
    suspend fun executeVoiceCommand(command: String): Result<VoiceCommandResult> = withContext(Dispatchers.IO) {
        val lower = command.lowercase().trim()

        // 1. Local fast intent matching for instantaneous action
        if (lower.contains("task") || lower.contains("remind") || lower.contains("todo") || lower.contains("add task")) {
            val taskTitle = command.replace(Regex("(?i)(create a task to|create a task for|add a task to|add task to|remind me to|add task)"), "").trim()
            val cleanTitle = if (taskTitle.isBlank()) command else taskTitle
            val category = if (lower.contains("study") || lower.contains("exam") || lower.contains("read")) "Study"
            else if (lower.contains("work") || lower.contains("email") || lower.contains("meeting")) "Work"
            else "Personal"

            insertTask(
                TaskEntity(
                    title = cleanTitle,
                    description = "Created via Pak AI Voice Command",
                    category = category,
                    priority = "High"
                )
            )

            return@withContext Result.success(
                VoiceCommandResult(
                    interpretedIntent = "Create Task: $cleanTitle",
                    spokenResponse = "Done! I have created the task \"$cleanTitle\" in your Task Manager.",
                    action = VoiceCommandAction.CreateTask(cleanTitle, category)
                )
            )
        }

        if (lower.contains("photo") || lower.contains("image") || lower.contains("filter") || lower.contains("edit picture")) {
            return@withContext Result.success(
                VoiceCommandResult(
                    interpretedIntent = "Open Photo Studio",
                    spokenResponse = "Opening the Photo Editor Studio for you.",
                    action = VoiceCommandAction.OpenPhotoEditor
                )
            )
        }

        if (lower.contains("pdf") || lower.contains("document") || lower.contains("report")) {
            val topic = command.replace(Regex("(?i)(make a pdf about|make a pdf on|create a pdf document about|generate a pdf about|create a pdf on|make pdf|generate pdf)"), "").trim()
            val cleanTopic = if (topic.isBlank()) "Pak AI Executive Briefing" else topic
            return@withContext Result.success(
                VoiceCommandResult(
                    interpretedIntent = "Generate PDF Document on $cleanTopic",
                    spokenResponse = "Generating a professional PDF document about $cleanTopic.",
                    action = VoiceCommandAction.GeneratePdf(cleanTopic, "Report")
                )
            )
        }

        if (lower.contains("ppt") || lower.contains("presentation") || lower.contains("slide") || lower.contains("slides")) {
            val topic = command.replace(Regex("(?i)(make a presentation on|make a presentation about|create slides about|generate ppt on|make ppt on|create presentation)"), "").trim()
            val cleanTopic = if (topic.isBlank()) "Artificial Intelligence & Future Tech" else topic
            return@withContext Result.success(
                VoiceCommandResult(
                    interpretedIntent = "Generate Presentation on $cleanTopic",
                    spokenResponse = "Creating a professional presentation slide deck on $cleanTopic.",
                    action = VoiceCommandAction.GeneratePpt(cleanTopic, 5)
                )
            )
        }

        if (lower.contains("translate") || lower.contains("in urdu") || lower.contains("in spanish") || lower.contains("in french") || lower.contains("in arabic")) {
            var targetLang = "Urdu"
            if (lower.contains("spanish")) targetLang = "Spanish"
            else if (lower.contains("french")) targetLang = "French"
            else if (lower.contains("arabic")) targetLang = "Arabic"
            else if (lower.contains("german")) targetLang = "German"
            else if (lower.contains("chinese")) targetLang = "Chinese (Simplified)"
            else if (lower.contains("japanese")) targetLang = "Japanese"
            else if (lower.contains("urdu")) targetLang = "Urdu"

            val textToTranslate = command.replace(Regex("(?i)(translate|into $targetLang|in $targetLang|to $targetLang)"), "").trim()
            return@withContext Result.success(
                VoiceCommandResult(
                    interpretedIntent = "Translate to $targetLang",
                    spokenResponse = "Translating text into $targetLang.",
                    action = VoiceCommandAction.Translate(textToTranslate, targetLang)
                )
            )
        }

        // 2. Intelligent conversational query fallback
        val aiResult = sendMessage(command, emptyList())
        val aiAnswer = aiResult.getOrDefault("I am Pak AI, ready to assist you with tasks, documents, slides, photos, and translation.")

        Result.success(
            VoiceCommandResult(
                interpretedIntent = "Conversational Query",
                spokenResponse = aiAnswer,
                action = VoiceCommandAction.GeneralAnswer(aiAnswer)
            )
        )
    }

    // --- Helper for AI Photo Creation from Prompt ---
    fun generatePhotoUrl(prompt: String, width: Int = 1024, height: Int = 1024, seed: Int = (1..999999).random()): String {
        val cleanPrompt = java.net.URLEncoder.encode(prompt.trim(), "UTF-8")
        return "https://image.pollinations.ai/prompt/$cleanPrompt?width=$width&height=$height&nologo=true&seed=$seed"
    }

    // --- Islamic Knowledge & Mufti Taqi Usmani Tafseer Query ---
    suspend fun queryIslamicScholar(query: String): Result<IslamicAiResponse> = withContext(Dispatchers.IO) {
        try {
            val apiKey = getApiKey()
            val prompt = """
                You are a revered Islamic Scholar & Encyclopedia within Pak AI, created by Muhammad Ali.
                Provide 100% authentic Islamic knowledge strictly adhering to the Quran and Sunnah (Ahlus Sunnah wal Jama'ah).
                Specifically incorporate the scholarly insight and Tafseer of Mufti Muhammad Taqi Usmani ("Aasan Tarjuma-e-Quran" / "Ma'ariful Quran").
                
                Question / Query: "$query"
                
                Respond in VALID JSON matching this exact schema:
                {
                  "arabicText": "Original Quranic Ayah or Hadith in clear Arabic with tashkeel (حركات), or essential Arabic supplication/phrases",
                  "urduTranslation": "Authentic Urdu translation specifically following Mufti Muhammad Taqi Usmani (آسان ترجمہ قرآن) or authentic Hadith translation",
                  "englishTranslation": "Authentic English translation according to Mufti Muhammad Taqi Usmani or Sahih International",
                  "tafseerOrTashreeh": "Comprehensive scholarly explanation, context of revelation (شان نزول), and detailed Tafseer/Tashreeh citing Mufti Muhammad Taqi Usmani's insights",
                  "reference": "Exact Surah & Ayah number (e.g. Surah Al-Baqarah 2:255) or Hadith collection & number (e.g. Sahih al-Bukhari 1)",
                  "scholarNotes": "مفتی محمد تقی عثمانی صاحب کی تحقیق و تفسیر کے مطابق رہنمائی"
                }
                Do not include markdown tags or backticks. Return pure JSON.
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(ContentItem(role = "user", parts = listOf(PartItem(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0.2f, maxOutputTokens = 3000)
            )

            val response = apiService.generateContent(
                model = getSelectedModel(),
                apiKey = apiKey,
                request = request
            )

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("No response received from Islamic Knowledge Engine")

            val cleanedJson = rawText.substringAfter("{", "").let { if (it.isNotEmpty()) "{$it" else rawText }
                .substringBeforeLast("}", "").let { if (it.isNotEmpty()) "$it}" else rawText }

            val json = org.json.JSONObject(cleanedJson)
            Result.success(
                IslamicAiResponse(
                    arabicText = json.optString("arabicText", ""),
                    urduTranslation = json.optString("urduTranslation", ""),
                    englishTranslation = json.optString("englishTranslation", ""),
                    tafseerOrTashreeh = json.optString("tafseerOrTashreeh", rawText),
                    reference = json.optString("reference", "قرآن کریم و سنت رسول ﷺ"),
                    scholarNotes = json.optString("scholarNotes", "مفتی محمد تقی عثمانی صاحب کی تفسیر")
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class TranslationResult(
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val romanization: String = "",
    val grammarExplanation: String = ""
)

sealed class VoiceCommandAction {
    data class CreateTask(val title: String, val category: String) : VoiceCommandAction()
    data class GeneratePdf(val topic: String, val docType: String) : VoiceCommandAction()
    data class GeneratePpt(val topic: String, val slideCount: Int) : VoiceCommandAction()
    data class Translate(val text: String, val targetLanguage: String) : VoiceCommandAction()
    object OpenPhotoEditor : VoiceCommandAction()
    data class GeneralAnswer(val answer: String) : VoiceCommandAction()
}

data class VoiceCommandResult(
    val interpretedIntent: String,
    val spokenResponse: String,
    val action: VoiceCommandAction
)

