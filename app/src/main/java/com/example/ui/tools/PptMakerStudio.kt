package com.example.ui.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.Composable
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
import androidx.core.content.FileProvider
import com.example.data.repository.PakAiRepository
import com.example.utils.PptDeckModel
import com.example.utils.PptGeneratorUtils
import com.example.utils.SlideModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PptMakerStudio(
    repository: PakAiRepository,
    initialTopic: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var topicInput by remember { mutableStateOf(if (initialTopic.isNotBlank()) initialTopic else "Next-Gen AI & Automation Framework") }
    var slideCount by remember { mutableStateOf(5) }
    var selectedTheme by remember { mutableStateOf("Emerald") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedDeck by remember { mutableStateOf<PptDeckModel?>(null) }
    var exportedPdfFile by remember { mutableStateOf<File?>(null) }
    var exportedHtmlFile by remember { mutableStateOf<File?>(null) }

    fun shareFile(file: File, mimeType: String) {
        try {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(sendIntent, "Share Presentation Deck"))
        } catch (e: Exception) {
            Toast.makeText(context, "Share error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openFile(file: File, mimeType: String) {
        try {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(viewIntent)
        } catch (e: Exception) {
            shareFile(file, mimeType)
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101A24)),
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
                            Icons.Default.Slideshow,
                            contentDescription = null,
                            tint = Color(0xFF00FF88),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Presentation & PPT Maker",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Generate professional slide decks & 16:9 presentations with Pak AI",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8DA3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item {
            // Slide Count and Theme selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Slide Count",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF00FF88),
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(3, 5, 7, 10).forEach { count ->
                        FilterChip(
                            selected = slideCount == count,
                            onClick = { slideCount = count },
                            label = { Text("$count", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00FF88),
                                selectedLabelColor = Color(0xFF08120A)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Theme Style",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF00FF88),
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Emerald", "Dark", "Gold").forEach { theme ->
                        FilterChip(
                            selected = selectedTheme == theme,
                            onClick = { selectedTheme = theme },
                            label = { Text(theme, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00FF88),
                                selectedLabelColor = Color(0xFF08120A)
                            )
                        )
                    }
                }
            }
        }

        item {
            // Topic Input
            OutlinedTextField(
                value = topicInput,
                onValueChange = { topicInput = it },
                label = { Text("Presentation Topic / Core Message") },
                placeholder = { Text("e.g. Clean Architecture in Kotlin & Jetpack Compose") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
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
                    if (topicInput.isBlank()) {
                        Toast.makeText(context, "Please enter a presentation topic", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        isGenerating = true
                        val result = repository.generateStructuredPresentation(topicInput, slideCount, selectedTheme)
                        result.onSuccess { deck ->
                            generatedDeck = deck
                            exportedPdfFile = PptGeneratorUtils.createPptPdf(context, deck)
                            exportedHtmlFile = PptGeneratorUtils.createHtmlSlideDeck(context, deck)
                            Toast.makeText(context, "Presentation generated successfully!", Toast.LENGTH_SHORT).show()
                        }.onFailure { err ->
                            Toast.makeText(context, "Generation error: ${err.message}", Toast.LENGTH_SHORT).show()
                        }
                        isGenerating = false
                    }
                },
                enabled = !isGenerating && topicInput.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FF88),
                    contentColor = Color(0xFF051208)
                )
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFF051208))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Creating Presentation Slides with AI...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate & Build Slides Deck", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Export Actions
        if (exportedPdfFile != null || exportedHtmlFile != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF15222E)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Presentation Files Ready",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // PDF 16:9 option
                        exportedPdfFile?.let { pdfFile ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1C2B3A))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("16:9 Landscape PDF Slides", fontSize = 12.sp, color = Color.White)
                                }
                                Row {
                                    IconButton(onClick = { openFile(pdfFile, "application/pdf") }) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = "Open", tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { shareFile(pdfFile, "application/pdf") }) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // HTML5 Interactive option
                        exportedHtmlFile?.let { htmlFile ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1C2B3A))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Html, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("HTML5 Interactive Slides", fontSize = 12.sp, color = Color.White)
                                }
                                Row {
                                    IconButton(onClick = { openFile(htmlFile, "text/html") }) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = "Open", tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { shareFile(htmlFile, "text/html") }) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Slide Carousel / Preview
        generatedDeck?.let { deck ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Slides Preview (${deck.slides.size} Slides)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Presenter: ${deck.presenter}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF00FF88)
                    )
                }
            }

            itemsIndexed(deck.slides) { index, slide ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141E29)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223548))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Slide ${index + 1}: ${slide.title}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF88),
                                fontSize = 14.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF223548))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("${index + 1}/${deck.slides.size}", fontSize = 10.sp, color = Color(0xFFBAC5D0))
                            }
                        }

                        if (slide.subtitle.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = slide.subtitle,
                                fontSize = 12.sp,
                                color = Color(0xFF8CA1B5),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        if (slide.bullets.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            slide.bullets.forEach { b ->
                                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text("▸ ", color = Color(0xFF00FF88), fontWeight = FontWeight.Bold)
                                    Text(b, color = Color(0xFFD6DFE8), fontSize = 12.sp)
                                }
                            }
                        }

                        if (slide.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color(0xFF0B1118),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Speaker Notes: ${slide.notes}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF76899E),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
