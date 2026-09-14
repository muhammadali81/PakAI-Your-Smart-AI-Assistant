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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
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
import com.example.utils.PdfDocumentModel
import com.example.utils.PdfGeneratorUtils
import com.example.utils.PdfSection
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PdfMakerStudio(
    repository: PakAiRepository,
    initialTopic: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var topicInput by remember { mutableStateOf(if (initialTopic.isNotBlank()) initialTopic else "AI Implementation & Strategy Report 2026") }
    var selectedDocType by remember { mutableStateOf("Executive Report") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedDoc by remember { mutableStateOf<PdfDocumentModel?>(null) }
    var lastExportedFile by remember { mutableStateOf<File?>(null) }

    val docTypes = listOf("Executive Report", "Professional Resume", "Project Proposal", "Invoice & Billing", "Meeting Minutes")

    fun shareFile(file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(sendIntent, "Share PDF Document"))
        } catch (e: Exception) {
            Toast.makeText(context, "Sharing failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openFile(file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(viewIntent)
        } catch (e: Exception) {
            // Fallback to chooser
            shareFile(file)
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101923)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F3244))
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
                            Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = Color(0xFF00FF88),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Professional PDF Maker",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "AI-powered documents generated with Muhammad Ali's Pak AI Engine",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8CA1B5),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item {
            // Document Type Chips
            Text(
                text = "Document Template",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF00FF88),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                docTypes.take(3).forEach { type ->
                    FilterChip(
                        selected = selectedDocType == type,
                        onClick = { selectedDocType = type },
                        label = { Text(type, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00FF88),
                            selectedLabelColor = Color(0xFF051208)
                        )
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                docTypes.drop(3).forEach { type ->
                    FilterChip(
                        selected = selectedDocType == type,
                        onClick = { selectedDocType = type },
                        label = { Text(type, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00FF88),
                            selectedLabelColor = Color(0xFF051208)
                        )
                    )
                }
            }
        }

        item {
            // Topic Input
            OutlinedTextField(
                value = topicInput,
                onValueChange = { topicInput = it },
                label = { Text("Document Topic or Description") },
                placeholder = { Text("e.g. Android Development Project Plan") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF88),
                    unfocusedBorderColor = Color(0xFF223244),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (topicInput.isBlank()) {
                        Toast.makeText(context, "Please enter a document topic", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        isGenerating = true
                        val result = repository.generateStructuredDocument(topicInput, selectedDocType)
                        result.onSuccess { doc ->
                            generatedDoc = doc
                            val file = PdfGeneratorUtils.createPdf(context, doc)
                            lastExportedFile = file
                            Toast.makeText(context, "PDF generated successfully!", Toast.LENGTH_SHORT).show()
                        }.onFailure { err ->
                            Toast.makeText(context, "Generation failed: ${err.message}", Toast.LENGTH_SHORT).show()
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
                    Text("Drafting Document with AI...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate & Build PDF", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Export Actions (if file generated)
        lastExportedFile?.let { file ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF15222E)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = file.name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Ready to view • Size: ${file.length() / 1024} KB",
                                    color = Color(0xFF00FF88),
                                    fontSize = 11.sp
                                )
                            }

                            Row {
                                IconButton(onClick = { openFile(file) }) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = "Open", tint = Color(0xFF00FF88))
                                }
                                IconButton(onClick = { shareFile(file) }) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF00FF88))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Preview of Sections
        generatedDoc?.let { doc ->
            item {
                Text(
                    text = "Document Preview: ${doc.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Author: ${doc.author} • ${doc.organization}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8CA1B5)
                )
            }

            itemsIndexed(doc.sections) { index, section ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141E29)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223344))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "${index + 1}. ${section.heading}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FF88),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = section.content,
                            color = Color(0xFFE0E6ED),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                        if (section.bullets.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            section.bullets.forEach { b ->
                                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text("• ", color = Color(0xFF00FF88), fontWeight = FontWeight.Bold)
                                    Text(b, color = Color(0xFFCCD7E2), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
