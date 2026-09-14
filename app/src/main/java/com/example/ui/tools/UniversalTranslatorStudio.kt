package com.example.ui.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PakAiRepository
import com.example.data.repository.TranslationResult
import com.example.utils.LanguageList
import com.example.utils.SupportedLanguage
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun UniversalTranslatorStudio(
    repository: PakAiRepository,
    onSaveAsTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var sourceLang by remember { mutableStateOf(LanguageList.languages[1]) } // English
    var targetLang by remember { mutableStateOf(LanguageList.languages[0]) } // Urdu
    var inputQuery by remember { mutableStateOf("Assalam-o-Alaikum! Welcome to Pak AI by Muhammad Ali.") }
    var isTranslating by remember { mutableStateOf(false) }
    var translationResult by remember { mutableStateOf<TranslationResult?>(null) }

    var showSourceDropdown by remember { mutableStateOf(false) }
    var showTargetDropdown by remember { mutableStateOf(false) }
    var sourceSearchQuery by remember { mutableStateOf("") }
    var targetSearchQuery by remember { mutableStateOf("") }

    // TTS engine
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(Unit) {
        val speech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Initialized
            }
        }
        tts = speech
        onDispose {
            speech.stop()
            speech.shutdown()
        }
    }

    fun speakText(text: String, langCode: String) {
        tts?.let { engine ->
            val locale = Locale.forLanguageTag(langCode)
            engine.language = locale
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "translator_tts")
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101C27)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223548))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00FF88).copy(alpha = 0.15f))
                            .border(1.5.dp, Color(0xFF00FF88), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = null,
                            tint = Color(0xFF00FF88),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "100+ Languages Polyglot Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Accurate AI translations, romanization & pronunciation by Muhammad Ali",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8DA3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Language Selectors (Source ⇄ Target)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141E29)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223548))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Source Lang Box
                    Box(modifier = Modifier.weight(1f)) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showSourceDropdown = true }
                                .border(1.dp, Color(0xFF2B3D50), RoundedCornerShape(10.dp)),
                            color = Color(0xFF1A2634)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(sourceLang.flag, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        sourceLang.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(16.dp))
                            }
                        }

                        DropdownMenu(
                            expanded = showSourceDropdown,
                            onDismissRequest = { showSourceDropdown = false }
                        ) {
                            LanguageList.languages.take(30).forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text("${lang.flag} ${lang.name} (${lang.nativeName})") },
                                    onClick = {
                                        sourceLang = lang
                                        showSourceDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Swap Button
                    IconButton(
                        onClick = {
                            val temp = sourceLang
                            sourceLang = targetLang
                            targetLang = temp
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = "Swap Languages",
                            tint = Color(0xFF00FF88)
                        )
                    }

                    // Target Lang Box
                    Box(modifier = Modifier.weight(1f)) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showTargetDropdown = true }
                                .border(1.dp, Color(0xFF2B3D50), RoundedCornerShape(10.dp)),
                            color = Color(0xFF1A2634)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(targetLang.flag, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        targetLang.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00FF88)
                                    )
                                }
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(16.dp))
                            }
                        }

                        DropdownMenu(
                            expanded = showTargetDropdown,
                            onDismissRequest = { showTargetDropdown = false }
                        ) {
                            LanguageList.languages.take(30).forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text("${lang.flag} ${lang.name} (${lang.nativeName})") },
                                    onClick = {
                                        targetLang = lang
                                        showTargetDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Pair Shortcuts
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "English ➔ Urdu" to (LanguageList.languages[1] to LanguageList.languages[0]),
                    "Urdu ➔ English" to (LanguageList.languages[0] to LanguageList.languages[1]),
                    "Arabic ➔ English" to (LanguageList.languages[2] to LanguageList.languages[1]),
                    "Pashto ➔ Urdu" to (LanguageList.languages[4] to LanguageList.languages[0]),
                    "English ➔ Spanish" to (LanguageList.languages[1] to LanguageList.languages[12]),
                    "English ➔ Chinese" to (LanguageList.languages[1] to LanguageList.languages[18])
                ).forEach { (label, pair) ->
                    FilterChip(
                        selected = sourceLang == pair.first && targetLang == pair.second,
                        onClick = {
                            sourceLang = pair.first
                            targetLang = pair.second
                        },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00FF88),
                            selectedLabelColor = Color(0xFF08120A)
                        )
                    )
                }
            }
        }

        // Input Text Field
        item {
            OutlinedTextField(
                value = inputQuery,
                onValueChange = { inputQuery = it },
                label = { Text("Enter text in ${sourceLang.name}") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF88),
                    unfocusedBorderColor = Color(0xFF223548),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (inputQuery.isBlank()) {
                        Toast.makeText(context, "Please enter text to translate", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        isTranslating = true
                        val result = repository.translateText(inputQuery, sourceLang.name, targetLang.name)
                        result.onSuccess { res ->
                            translationResult = res
                        }.onFailure { err ->
                            Toast.makeText(context, "Translation error: ${err.message}", Toast.LENGTH_SHORT).show()
                        }
                        isTranslating = false
                    }
                },
                enabled = !isTranslating && inputQuery.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FF88),
                    contentColor = Color(0xFF051208)
                )
            ) {
                if (isTranslating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFF051208))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Translating Across Languages...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Translate (${sourceLang.code.uppercase()} ➔ ${targetLang.code.uppercase()})", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Translation Result Card
        translationResult?.let { res ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14202D)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${targetLang.flag} ${targetLang.name} Translation",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF88),
                                fontSize = 14.sp
                            )

                            Row {
                                IconButton(
                                    onClick = {
                                        speakText(res.translatedText, targetLang.code)
                                    }
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Pronounce", tint = Color(0xFF00FF88))
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Pak AI Translation", res.translatedText))
                                        Toast.makeText(context, "Copied translation", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF8DA3B8))
                                }

                                IconButton(
                                    onClick = {
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, res.translatedText)
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Translation"))
                                    }
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF8DA3B8))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Translated Text
                        Text(
                            text = res.translatedText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            lineHeight = 24.sp
                        )

                        // Romanization if available
                        if (res.romanization.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color(0xFF0B141C),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Pronunciation / Romanization:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00FF88)
                                    )
                                    Text(
                                        text = res.romanization,
                                        fontSize = 12.sp,
                                        color = Color(0xFFCAD7E4),
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }

                        // Grammar & Linguistic Notes
                        if (res.grammarExplanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Linguistic Notes: ${res.grammarExplanation}",
                                fontSize = 11.sp,
                                color = Color(0xFF7E93A8)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Add to Tasks Button
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1B2B3B))
                                .clickable {
                                    onSaveAsTask("Study / Practice: ${res.translatedText}")
                                    Toast.makeText(context, "Saved to Task Manager", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save to Task Manager", fontSize = 11.sp, color = Color(0xFF00FF88), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
