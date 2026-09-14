package com.example.ui.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

    // Default source is English, target is Urdu
    var sourceLang by remember { mutableStateOf(LanguageList.languages[1]) }
    var targetLang by remember { mutableStateOf(LanguageList.languages[0]) }
    var inputQuery by remember { mutableStateOf("Assalam-o-Alaikum! Welcome to Pak AI by Muhammad Ali.") }
    var isTranslating by remember { mutableStateOf(false) }
    var translationResult by remember { mutableStateOf<TranslationResult?>(null) }

    // Dialog pickers
    var showSourcePicker by remember { mutableStateOf(false) }
    var showTargetPicker by remember { mutableStateOf(false) }

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

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Translated Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun shareTranslation(orig: String, trans: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Original (${sourceLang.name}):\n$orig\n\nTranslation (${targetLang.name}):\n$trans\n\n---\nTranslated by Pak AI Universal Translator"
            )
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Translation"))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C141D))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Studio Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101C27)),
                border = BorderStroke(1.dp, Color(0xFF223548))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
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
                            text = "100+ زبانوں کا عالمی مترجم (Universal Translator)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "جس زبان میں لکھیں اور جس زبان میں ترجمہ چاہیں — فوری و مستند ترجمہ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8DA3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // =========================================================================
        // OPTION 1: THE LANGUAGE IN WHICH TEXT IS WRITTEN (SOURCE LANGUAGE)
        // =========================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131F2C)),
                border = BorderStroke(1.dp, Color(0xFF223548))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("1️⃣ ", fontSize = 14.sp)
                            Column {
                                Text(
                                    text = "آپشن 1: جس زبان میں متن لکھا جا رہا ہے",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Input / Source Language (Currently Writing In)",
                                    fontSize = 10.sp,
                                    color = Color(0xFF8DA3B8)
                                )
                            }
                        }

                        // Change Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1F2F40),
                            modifier = Modifier.clickable { showSourcePicker = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${sourceLang.flag} ${sourceLang.name}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF88)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Select Pills for Option 1
                    Text("فوری انتخاب (Quick Source):", fontSize = 10.sp, color = Color(0xFF8DA3B8))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            LanguageList.languages[1], // English
                            LanguageList.languages[0], // Urdu
                            LanguageList.languages[2], // Arabic
                            LanguageList.languages[4], // Pashto
                            LanguageList.languages[5], // Punjabi
                            LanguageList.languages[6], // Sindhi
                            LanguageList.languages[7], // Balochi
                            LanguageList.languages[8], // Saraiki
                            LanguageList.languages[3]  // Persian
                        ).forEach { lang ->
                            FilterChip(
                                selected = sourceLang == lang,
                                onClick = { sourceLang = lang },
                                label = { Text("${lang.flag} ${lang.name} (${lang.nativeName})", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00FF88),
                                    selectedLabelColor = Color(0xFF08140B),
                                    containerColor = Color(0xFF1B2836),
                                    labelColor = Color(0xFFB0BEC5)
                                )
                            )
                        }
                    }
                }
            }
        }

        // SWAP LANGUAGES BUTTON
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        val temp = sourceLang
                        sourceLang = targetLang
                        targetLang = temp
                    },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF13202E))
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = "Swap", tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("زبانیں تبدیل کریں (Swap ${sourceLang.code.uppercase()} ⇄ ${targetLang.code.uppercase()})", fontSize = 11.sp, color = Color.White)
                }
            }
        }

        // =========================================================================
        // OPTION 2: THE LANGUAGE TO TRANSLATE INTO (TARGET LANGUAGE)
        // =========================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131F2C)),
                border = BorderStroke(1.dp, Color(0xFF223548))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("2️⃣ ", fontSize = 14.sp)
                            Column {
                                Text(
                                    text = "آپشن 2: جس زبان میں ترجمہ کرنا ہے",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF00FF88)
                                )
                                Text(
                                    text = "Output / Target Language (Translate Into)",
                                    fontSize = 10.sp,
                                    color = Color(0xFF8DA3B8)
                                )
                            }
                        }

                        // Change Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1F2F40),
                            modifier = Modifier.clickable { showTargetPicker = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${targetLang.flag} ${targetLang.name}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF88)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Select Pills for Option 2
                    Text("فوری انتخاب (Quick Target):", fontSize = 10.sp, color = Color(0xFF8DA3B8))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            LanguageList.languages[0], // Urdu
                            LanguageList.languages[1], // English
                            LanguageList.languages[2], // Arabic
                            LanguageList.languages[4], // Pashto
                            LanguageList.languages[22], // Turkish
                            LanguageList.languages[13], // French
                            LanguageList.languages[12], // Spanish
                            LanguageList.languages[18], // Chinese
                            LanguageList.languages[17]  // Russian
                        ).forEach { lang ->
                            FilterChip(
                                selected = targetLang == lang,
                                onClick = { targetLang = lang },
                                label = { Text("${lang.flag} ${lang.name} (${lang.nativeName})", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00FF88),
                                    selectedLabelColor = Color(0xFF08140B),
                                    containerColor = Color(0xFF1B2836),
                                    labelColor = Color(0xFFB0BEC5)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Input Text Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131F2C)),
                border = BorderStroke(1.dp, Color(0xFF223548))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "متن درج کریں (${sourceLang.flag} ${sourceLang.name})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (inputQuery.isNotEmpty()) {
                            Text(
                                text = "Clear",
                                fontSize = 11.sp,
                                color = Color(0xFF8DA3B8),
                                modifier = Modifier.clickable { inputQuery = "" }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = { Text("وہ جملہ یا پیراگراف لکھیں جس کا ترجمہ کرنا چاہتے ہیں...", color = Color.Gray, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FF88),
                            unfocusedBorderColor = Color(0xFF2B3C4E),
                            focusedContainerColor = Color(0xFF0E1620),
                            unfocusedContainerColor = Color(0xFF0E1620),
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
                            Text("ترجمہ جاری ہے...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ترجمہ کریں (${sourceLang.name} ➔ ${targetLang.name})", fontWeight = FontWeight.Bold)
                        }
                    }
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
                    border = BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.5f))
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
                                    onClick = { speakText(res.translatedText, targetLang.code) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = { copyToClipboard(res.translatedText) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF8DA3B8), modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = { shareTranslation(inputQuery, res.translatedText) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF8DA3B8), modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Translated Content Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0B141E),
                            border = BorderStroke(1.dp, Color(0xFF1D2E40)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = res.translatedText,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                lineHeight = 26.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }

                        // Romanization if available
                        if (res.romanization.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "🗣️ تلفظ و رومن (Pronunciation):", fontSize = 11.sp, color = Color(0xFF64B5F6), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = res.romanization, fontSize = 12.sp, color = Color(0xFFB0BEC5), lineHeight = 18.sp)
                        }

                        // Grammar or Nuance explanation
                        if (res.grammarExplanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "💡 لسانی نکات (Nuances):", fontSize = 11.sp, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = res.grammarExplanation, fontSize = 12.sp, color = Color(0xFFCFD8DC), lineHeight = 18.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onSaveAsTask("Translation: ${res.translatedText.take(50)}...")
                                Toast.makeText(context, "Saved to Tasks!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2F3F), contentColor = Color(0xFF00FF88)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save to Tasks List", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Source Language Selection Dialog
    if (showSourcePicker) {
        LanguagePickerDialog(
            title = "1️⃣ جس زبان میں متن لکھا جا رہا ہے (Select Source)",
            currentSelection = sourceLang,
            onSelect = {
                sourceLang = it
                showSourcePicker = false
            },
            onDismiss = { showSourcePicker = false }
        )
    }

    // Target Language Selection Dialog
    if (showTargetPicker) {
        LanguagePickerDialog(
            title = "2️⃣ جس زبان میں ترجمہ کرنا ہے (Select Target)",
            currentSelection = targetLang,
            onSelect = {
                targetLang = it
                showTargetPicker = false
            },
            onDismiss = { showTargetPicker = false }
        )
    }
}

@Composable
private fun LanguagePickerDialog(
    title: String,
    currentSelection: SupportedLanguage,
    onSelect: (SupportedLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredLanguages = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            LanguageList.languages
        } else {
            LanguageList.languages.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.nativeName.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF00FF88))
            }
        },
        title = {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search 100+ languages...", color = Color.Gray, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF00FF88)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF88),
                        unfocusedBorderColor = Color(0xFF2B3D50),
                        focusedContainerColor = Color(0xFF0E1620),
                        unfocusedContainerColor = Color(0xFF0E1620),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(filteredLanguages) { lang ->
                        val isSelected = lang.code == currentSelection.code
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(lang) }
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF00FF88).copy(alpha = 0.2f) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(lang.flag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = lang.name,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF00FF88) else Color.White,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${lang.nativeName} (${lang.code.uppercase()})",
                                        fontSize = 11.sp,
                                        color = Color(0xFF8DA3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF131D27),
        shape = RoundedCornerShape(16.dp)
    )
}
