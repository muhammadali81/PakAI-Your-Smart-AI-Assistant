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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.IslamicKnowledgeBase
import com.example.data.model.HadithModel
import com.example.data.model.IslamicAiResponse
import com.example.data.model.MasnoonDuaModel
import com.example.data.model.QuranAyahModel
import com.example.data.repository.PakAiRepository
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun IslamicStudio(
    repository: PakAiRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "قرآن و تفسیر" to Icons.Default.Book,
        "احادیث مبارکہ" to Icons.Default.Mosque,
        "مسنون دعائیں" to Icons.Default.Favorite,
        "مفتی تقی عثمانی فتاویٰ و ریسرچ" to Icons.Default.Psychology
    )

    // TTS Engine
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(Unit) {
        val speech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ar")
            }
        }
        tts = speech
        onDispose {
            speech.stop()
            speech.shutdown()
        }
    }

    fun speak(text: String, isArabic: Boolean = true) {
        tts?.let {
            it.language = if (isArabic) Locale("ar") else Locale("ur")
            it.speak(text, TextToSpeech.QUEUE_FLUSH, null, "IslamicTTS")
        }
    }

    fun copyText(label: String, content: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, content)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "کاپی ہو گیا (Copied)", Toast.LENGTH_SHORT).show()
    }

    fun shareText(title: String, content: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_TEXT, "$title\n\n$content\n\n---\nشیئر بذریعہ پاک اے آئی (Pak AI by Muhammad Ali)")
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Islamic Knowledge"))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C141D))
    ) {
        // Islamic Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F261E), Color(0xFF0C141D))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        Icons.Default.Mosque,
                        contentDescription = null,
                        tint = Color(0xFF00FF88),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "اسلامک انسائیکلوپیڈیا • Islamic Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "100% مستند قرآن، 1000+ احادیث، مسنون دعائیں اور مفتی تقی عثمانی صاحب کی تفسیر",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8DA3B8),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF101923),
            contentColor = Color(0xFF00FF88),
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF00FF88),
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, (title, icon) ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    },
                    selectedContentColor = Color(0xFF00FF88),
                    unselectedContentColor = Color(0xFF7A8B9E)
                )
            }
        }

        // Body Content
        when (selectedTab) {
            0 -> QuranTafseerTab(
                onSpeak = { text -> speak(text, isArabic = true) },
                onCopy = ::copyText,
                onShare = ::shareText
            )
            1 -> HadithTab(
                onSpeak = { text -> speak(text, isArabic = true) },
                onCopy = ::copyText,
                onShare = ::shareText
            )
            2 -> MasnoonDuainTab(
                onSpeak = { text -> speak(text, isArabic = true) },
                onCopy = ::copyText,
                onShare = ::shareText
            )
            3 -> IslamicAiScholarTab(
                repository = repository,
                onSpeak = { text -> speak(text, isArabic = false) },
                onCopy = ::copyText,
                onShare = ::shareText
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 1: QURAN & MUFTI TAQI USMANI TAFSEER
// -------------------------------------------------------------
@Composable
private fun QuranTafseerTab(
    onSpeak: (String) -> Unit,
    onCopy: (String, String) -> Unit,
    onShare: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val ayahs = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            IslamicKnowledgeBase.quranAyahs
        } else {
            IslamicKnowledgeBase.quranAyahs.filter {
                it.surahNameUrdu.contains(searchQuery, ignoreCase = true) ||
                        it.surahNameEnglish.contains(searchQuery, ignoreCase = true) ||
                        it.urduTranslation.contains(searchQuery, ignoreCase = true) ||
                        it.topic.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("سورہ یا عنوان تلاش کریں (مثلاً: الفاتحہ، آیت الکرسی، صبر، دعا)", color = Color(0xFF6B7E93), fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF00FF88)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF88),
                    unfocusedBorderColor = Color(0xFF223548),
                    focusedContainerColor = Color(0xFF141E29),
                    unfocusedContainerColor = Color(0xFF141E29),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
        }

        items(ayahs) { ayah ->
            QuranAyahCard(
                ayah = ayah,
                onSpeak = { onSpeak(ayah.arabicText) },
                onCopy = {
                    val full = "${ayah.surahNameUrdu} (${ayah.surahNameEnglish})\n\n${ayah.arabicText}\n\nاردو ترجمہ (مفتی تقی عثمانی):\n${ayah.urduTranslation}\n\nEnglish:\n${ayah.englishTranslation}\n\nتفسیر:\n${ayah.tafseerTaqiUsmani}"
                    onCopy(ayah.surahNameUrdu, full)
                },
                onShare = {
                    val full = "${ayah.arabicText}\n\nاردو ترجمہ (مفتی تقی عثمانی):\n${ayah.urduTranslation}\n\nEnglish:\n${ayah.englishTranslation}\n\nتفسیر مفتی تقی عثمانی:\n${ayah.tafseerTaqiUsmani}"
                    onShare("${ayah.surahNameUrdu} - آیت ${ayah.ayahNumber}", full)
                }
            )
        }
    }
}

@Composable
private fun QuranAyahCard(
    ayah: QuranAyahModel,
    onSpeak: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    var isTafseerExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141F2B)),
        border = BorderStroke(1.dp, Color(0xFF203244))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Surah Info + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = ayah.surahNameUrdu,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00FF88)
                    )
                    Text(
                        text = "${ayah.surahNameEnglish} • آیت ${ayah.ayahNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8DA3B8),
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onSpeak, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF8DA3B8), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF8DA3B8), modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic Text with Tashkeel
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0B121A),
                border = BorderStroke(1.dp, Color(0xFF1C2C3D))
            ) {
                Text(
                    text = ayah.arabicText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE8F5E9),
                    textAlign = TextAlign.Right,
                    lineHeight = 36.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Urdu Translation (Mufti Taqi Usmani)
            Text(
                text = "اردو ترجمہ (مفتی محمد تقی عثمانی صاحب):",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF00FF88),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = ayah.urduTranslation,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Right,
                color = Color(0xFFE0E6ED)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // English Translation
            Text(
                text = "English Translation (Mufti Taqi Usmani):",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64B5F6),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = ayah.englishTranslation,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFFB0BEC5)
            )

            // Mufti Taqi Usmani Tafseer Block
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFF223548))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isTafseerExpanded = !isTafseerExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📖 ", fontSize = 14.sp)
                    Text(
                        text = "تفسیر و تشریح (حضرت مفتی محمد تقی عثمانی دامت برکاتہم)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                }
                Text(
                    text = if (isTafseerExpanded) "▲ بند کریں" else "▼ پڑھیں",
                    fontSize = 11.sp,
                    color = Color(0xFF8DA3B8)
                )
            }

            if (isTafseerExpanded) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF19241E),
                    border = BorderStroke(1.dp, Color(0xFF274332))
                ) {
                    Text(
                        text = ayah.tafseerTaqiUsmani,
                        fontSize = 13.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Right,
                        color = Color(0xFFD6EAD8),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: AUTHENTIC HADITHS (1000+ Collection)
// -------------------------------------------------------------
@Composable
private fun HadithTab(
    onSpeak: (String) -> Unit,
    onCopy: (String, String) -> Unit,
    onShare: (String, String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("سب (All)") }
    val categories = listOf("سب (All)", "اخلاص اور نیت", "اخلاق اور معاشرت", "طہارت اور ذکر الٰہی", "توکل اور دعا", "تعلیم قرآن")

    val filteredList = remember(selectedCategory) {
        if (selectedCategory == "سب (All)") {
            IslamicKnowledgeBase.authenticHadiths
        } else {
            IslamicKnowledgeBase.authenticHadiths.filter { it.category == selectedCategory }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00FF88),
                            selectedLabelColor = Color(0xFF06140D),
                            containerColor = Color(0xFF162330),
                            labelColor = Color(0xFF8DA3B8)
                        )
                    )
                }
            }
        }

        items(filteredList) { hadith ->
            HadithCard(
                hadith = hadith,
                onSpeak = { onSpeak(hadith.arabicText) },
                onCopy = {
                    val full = "${hadith.book} (${hadith.hadithNumber})\nراوی: ${hadith.narrator}\n\n${hadith.arabicText}\n\nاردو ترجمہ:\n${hadith.urduTranslation}\n\nEnglish:\n${hadith.englishTranslation}\n\nتشریح:\n${hadith.tashreeh}"
                    onCopy(hadith.book, full)
                },
                onShare = {
                    val full = "${hadith.arabicText}\n\nاردو ترجمہ:\n${hadith.urduTranslation}\n\nEnglish:\n${hadith.englishTranslation}\n\nتشریح: ${hadith.tashreeh}\nحوالہ: ${hadith.book} (${hadith.grading})"
                    onShare("${hadith.book} - ${hadith.hadithNumber}", full)
                }
            )
        }
    }
}

@Composable
private fun HadithCard(
    hadith: HadithModel,
    onSpeak: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141F2B)),
        border = BorderStroke(1.dp, Color(0xFF203244))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = hadith.book,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FF88)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF00FF88).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = hadith.grading,
                                fontSize = 10.sp,
                                color = Color(0xFF00FF88),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "راوی: ${hadith.narrator} • ${hadith.hadithNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8DA3B8),
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onSpeak, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF8DA3B8), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF8DA3B8), modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0B121A),
                border = BorderStroke(1.dp, Color(0xFF1C2C3D))
            ) {
                Text(
                    text = hadith.arabicText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE8F5E9),
                    textAlign = TextAlign.Right,
                    lineHeight = 32.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Urdu
            Text(text = "اردو ترجمہ:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00FF88), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = hadith.urduTranslation, fontSize = 14.sp, lineHeight = 22.sp, textAlign = TextAlign.Right, color = Color(0xFFE0E6ED))

            Spacer(modifier = Modifier.height(8.dp))

            // English
            Text(text = "English:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64B5F6), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = hadith.englishTranslation, fontSize = 12.sp, lineHeight = 18.sp, color = Color(0xFFB0BEC5))

            // Tashreeh
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF162330)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "💡 فہم الحدیث و تشریح:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = hadith.tashreeh,
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Right,
                        color = Color(0xFFC7D5E4)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: MASNOON DUAIN
// -------------------------------------------------------------
@Composable
private fun MasnoonDuainTab(
    onSpeak: (String) -> Unit,
    onCopy: (String, String) -> Unit,
    onShare: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(IslamicKnowledgeBase.masnoonDuain) { dua ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141F2B)),
                border = BorderStroke(1.dp, Color(0xFF203244))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = dua.titleUrdu,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF88)
                            )
                            Text(
                                text = "${dua.titleEnglish} • ${dua.occasion}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF8DA3B8),
                                fontSize = 11.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onSpeak(dua.arabicText) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = {
                                val full = "${dua.titleUrdu}\n\n${dua.arabicText}\n\nاردو:\n${dua.urduTranslation}\n\nEnglish:\n${dua.englishTranslation}\n\nفضیلت: ${dua.fazeelat}\nحوالہ: ${dua.reference}"
                                onCopy(dua.titleUrdu, full)
                            }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF8DA3B8), modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = {
                                val full = "${dua.titleUrdu}\n\n${dua.arabicText}\n\nاردو ترجمہ:\n${dua.urduTranslation}\n\nفضیلت:\n${dua.fazeelat}\nحوالہ: ${dua.reference}"
                                onShare(dua.titleUrdu, full)
                            }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF8DA3B8), modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0B121A),
                        border = BorderStroke(1.dp, Color(0xFF1C2C3D))
                    ) {
                        Text(
                            text = dua.arabicText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE8F5E9),
                            textAlign = TextAlign.Right,
                            lineHeight = 32.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "اردو ترجمہ:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00FF88), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = dua.urduTranslation, fontSize = 13.sp, lineHeight = 20.sp, textAlign = TextAlign.Right, color = Color(0xFFE0E6ED))

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "English:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64B5F6), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = dua.englishTranslation, fontSize = 12.sp, lineHeight = 18.sp, color = Color(0xFFB0BEC5))

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1A2620),
                        border = BorderStroke(1.dp, Color(0xFF274332))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "✨ فضیلت و حوالہ (${dua.reference}):", fontSize = 11.sp, color = Color(0xFF81C784), fontWeight = FontWeight.Bold)
                            Text(text = dua.fazeelat, fontSize = 11.sp, color = Color(0xFFC8E6C9), lineHeight = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: ISLAMIC AI SCHOLAR (MUFTI TAQI USMANI RESEARCH)
// -------------------------------------------------------------
@Composable
private fun IslamicAiScholarTab(
    repository: PakAiRepository,
    onSpeak: (String) -> Unit,
    onCopy: (String, String) -> Unit,
    onShare: (String, String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var userQuery by remember { mutableStateOf("مفتی تقی عثمانی صاحب کی تفسیر کے مطابق سورۃ العصر کا مفہوم اور انسان کے خسارے سے بچنے کے اصول کیا ہیں؟") }
    var isLoading by remember { mutableStateOf(false) }
    var scholarResponse by remember { mutableStateOf<IslamicAiResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun executeQuery() {
        if (userQuery.isBlank()) return
        isLoading = true
        errorMessage = null
        scope.launch {
            val result = repository.queryIslamicScholar(userQuery)
            if (result.isSuccess) {
                scholarResponse = result.getOrNull()
            } else {
                errorMessage = result.exceptionOrNull()?.localizedMessage ?: "سوال کا جواب حاصل نہیں ہو سکا۔ برائے مہربانی نیٹ ورک چیک کریں۔"
            }
            isLoading = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D28)),
                border = BorderStroke(1.dp, Color(0xFF223548))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF00FF88))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مفتی محمد تقی عثمانی صاحب کے علمی نقطہ نظر سے سوال پوچھیں",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = userQuery,
                        onValueChange = { userQuery = it },
                        placeholder = { Text("قرآنی آیت، حدیث، فقہی مسئلہ، یا معاشی رہنمائی درج کریں...", color = Color(0xFF6B7E93), fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FF88),
                            unfocusedBorderColor = Color(0xFF223548),
                            focusedContainerColor = Color(0xFF0E1620),
                            unfocusedContainerColor = Color(0xFF0E1620),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Suggested Questions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "سورۃ الانشراح کی تفسیر",
                            "رزق میں برکت کی دعائیں",
                            "اسلام میں سودی نظام کا متبادل",
                            "حدیث: اعمال کا دارومدار نیتوں پر ہے"
                        ).forEach { suggestion ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1B2735),
                                modifier = Modifier.clickable {
                                    userQuery = suggestion
                                    executeQuery()
                                }
                            ) {
                                Text(
                                    text = suggestion,
                                    fontSize = 11.sp,
                                    color = Color(0xFF00FF88),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { executeQuery() },
                        enabled = !isLoading && userQuery.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FF88),
                            contentColor = Color(0xFF081910)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF081910), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("علمی تحقیق جاری ہے...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جواب حاصل کریں (Ask Scholar)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (errorMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF331518),
                    border = BorderStroke(1.dp, Color(0xFFE53935))
                ) {
                    Text(
                        text = "⚠️ $errorMessage",
                        color = Color(0xFFFF8A80),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        scholarResponse?.let { resp ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141F2B)),
                    border = BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎓 ", fontSize = 16.sp)
                                Text(
                                    text = resp.scholarNotes,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF88)
                                )
                            }

                            Row {
                                IconButton(onClick = {
                                    val full = "${resp.arabicText}\n\nاردو:\n${resp.urduTranslation}\n\nEnglish:\n${resp.englishTranslation}\n\nتفسیر:\n${resp.tafseerOrTashreeh}\n\nحوالہ: ${resp.reference}"
                                    onCopy("Islamic Scholar Answer", full)
                                }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF8DA3B8), modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = {
                                    val full = "${resp.arabicText}\n\nاردو:\n${resp.urduTranslation}\n\nEnglish:\n${resp.englishTranslation}\n\nتفسیر:\n${resp.tafseerOrTashreeh}\n\nحوالہ: ${resp.reference}"
                                    onShare("تحقیق مفتی تقی عثمانی صاحب", full)
                                }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF8DA3B8), modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        if (resp.arabicText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0B121A)
                            ) {
                                Text(
                                    text = resp.arabicText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFE8F5E9),
                                    textAlign = TextAlign.Right,
                                    lineHeight = 32.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        if (resp.urduTranslation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "اردو ترجمہ (مفتی تقی عثمانی صاحب):", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00FF88), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = resp.urduTranslation, fontSize = 13.sp, lineHeight = 22.sp, textAlign = TextAlign.Right, color = Color(0xFFE0E6ED))
                        }

                        if (resp.englishTranslation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "English Translation:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64B5F6), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = resp.englishTranslation, fontSize = 12.sp, lineHeight = 18.sp, color = Color(0xFFB0BEC5))
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFF223548))
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "📖 تفصیلی تشریح و افاداتِ مفتی تقی عثمانی:", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF18261F),
                            border = BorderStroke(1.dp, Color(0xFF274332))
                        ) {
                            Text(
                                text = resp.tafseerOrTashreeh,
                                fontSize = 13.sp,
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Right,
                                color = Color(0xFFD6EAD8),
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        if (resp.reference.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "حوالہ: ${resp.reference}", fontSize = 11.sp, color = Color(0xFF8DA3B8))
                        }
                    }
                }
            }
        }
    }
}
