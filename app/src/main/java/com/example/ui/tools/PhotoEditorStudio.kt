package com.example.ui.tools

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.repository.PakAiRepository
import com.example.utils.BitmapFilterUtils
import com.example.utils.PhotoFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

@Composable
fun PhotoEditorStudio(
    repository: PakAiRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var displayedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedFilter by remember { mutableStateOf(PhotoFilter.ORIGINAL) }
    var brightness by remember { mutableFloatStateOf(0f) }
    var contrast by remember { mutableFloatStateOf(1.0f) }
    var saturation by remember { mutableFloatStateOf(1.0f) }

    var isAnalyzingVision by remember { mutableStateOf(false) }
    var visionAnalysisResult by remember { mutableStateOf<String?>(null) }

    // Helper to generate a vibrant default canvas if user has not picked a photo yet
    fun createDefaultPlaceholderBitmap(): Bitmap {
        val w = 600
        val h = 400
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val bgPaint = Paint().apply { color = AndroidColor.rgb(16, 26, 38) }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        val circlePaint = Paint().apply { color = AndroidColor.rgb(0, 255, 136) }
        canvas.drawCircle(w / 2f, h / 2f - 20f, 60f, circlePaint)

        val textPaint = Paint().apply {
            color = AndroidColor.rgb(240, 245, 250)
            textSize = 24f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Pak AI Photo Studio", w / 2f, h / 2f + 80f, textPaint)

        val subPaint = Paint().apply {
            color = AndroidColor.rgb(120, 140, 160)
            textSize = 14f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Tap \"Choose Photo\" below to edit any image", w / 2f, h / 2f + 110f, subPaint)

        return bmp
    }

    LaunchedEffect(Unit) {
        if (originalBitmap == null) {
            val defaultBmp = createDefaultPlaceholderBitmap()
            originalBitmap = defaultBmp
            displayedBitmap = defaultBmp
        }
    }

    fun applyEdits() {
        val orig = originalBitmap ?: return
        displayedBitmap = BitmapFilterUtils.applyAdjustmentsAndFilter(
            source = orig,
            filter = selectedFilter,
            brightness = brightness,
            contrast = contrast,
            saturation = saturation
        )
    }

    // Photo picker launcher (complies with Android Photo Picker policy)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                val decoded = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (decoded != null) {
                    originalBitmap = decoded
                    selectedFilter = PhotoFilter.ORIGINAL
                    brightness = 0f
                    contrast = 1.0f
                    saturation = 1.0f
                    visionAnalysisResult = null
                    displayedBitmap = decoded
                    Toast.makeText(context, "Photo loaded successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Could not load image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun shareEditedPhoto(bmp: Bitmap) {
        try {
            val file = BitmapFilterUtils.saveBitmapToCache(context, bmp)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(sendIntent, "Share Edited Photo"))
        } catch (e: Exception) {
            Toast.makeText(context, "Sharing failed: ${e.message}", Toast.LENGTH_SHORT).show()
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Photo Editor & AI Vision Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Real-time filters, adjustments, transformations & AI analysis",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8DA3B8),
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88), contentColor = Color(0xFF08120A)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Choose Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Image Canvas / Display
        item {
            displayedBitmap?.let { bmp ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0D141C))
                        .border(1.dp, Color(0xFF223548), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Edited Photo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Action Toolbar (Rotate, Flip, AI Vision, Share)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Rotate 90
                OutlinedButton(
                    onClick = {
                        originalBitmap?.let { orig ->
                            val rotated = BitmapFilterUtils.rotateBitmap(orig, 90f)
                            originalBitmap = rotated
                            applyEdits()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.RotateRight, contentDescription = "Rotate", modifier = Modifier.size(16.dp), tint = Color(0xFF00FF88))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rotate", fontSize = 11.sp, color = Color.White)
                }

                // Flip Horizontal
                OutlinedButton(
                    onClick = {
                        originalBitmap?.let { orig ->
                            val flipped = BitmapFilterUtils.flipBitmap(orig, horizontal = true)
                            originalBitmap = flipped
                            applyEdits()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Flip, contentDescription = "Flip", modifier = Modifier.size(16.dp), tint = Color(0xFF00FF88))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Flip", fontSize = 11.sp, color = Color.White)
                }

                // AI Vision Analyze
                Button(
                    onClick = {
                        displayedBitmap?.let { bmp ->
                            scope.launch {
                                isAnalyzingVision = true
                                val b64 = BitmapFilterUtils.bitmapToBase64(bmp)
                                val result = repository.analyzeImage(b64, "Analyze this image with precision. Describe the visual content, color aesthetics, composition, and suggestions.")
                                result.onSuccess { text ->
                                    visionAnalysisResult = text
                                }.onFailure { e ->
                                    Toast.makeText(context, "AI Vision error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                                isAnalyzingVision = false
                            }
                        }
                    },
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88), contentColor = Color(0xFF051208))
                ) {
                    if (isAnalyzingVision) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color(0xFF051208))
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Vision", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Share / Save
                IconButton(
                    onClick = { displayedBitmap?.let { shareEditedPhoto(it) } },
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E2E40))
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF00FF88))
                }
            }
        }

        // Filters Strip
        item {
            Text(
                text = "Preset Filters",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF00FF88),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PhotoFilter.values().forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = {
                            selectedFilter = filter
                            applyEdits()
                        },
                        label = { Text(filter.displayName, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00FF88),
                            selectedLabelColor = Color(0xFF08120A)
                        )
                    )
                }
            }
        }

        // Adjustment Sliders
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141E29)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223548))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Fine Adjustments",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Brightness
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Brightness", fontSize = 11.sp, color = Color(0xFF8DA3B8))
                        Text("${brightness.toInt()}", fontSize = 11.sp, color = Color(0xFF00FF88))
                    }
                    Slider(
                        value = brightness,
                        onValueChange = {
                            brightness = it
                            applyEdits()
                        },
                        valueRange = -80f..80f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF00FF88), activeTrackColor = Color(0xFF00FF88))
                    )

                    // Contrast
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Contrast", fontSize = 11.sp, color = Color(0xFF8DA3B8))
                        Text(String.format("%.2fx", contrast), fontSize = 11.sp, color = Color(0xFF00FF88))
                    }
                    Slider(
                        value = contrast,
                        onValueChange = {
                            contrast = it
                            applyEdits()
                        },
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF00FF88), activeTrackColor = Color(0xFF00FF88))
                    )

                    // Saturation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Saturation", fontSize = 11.sp, color = Color(0xFF8DA3B8))
                        Text(String.format("%.2fx", saturation), fontSize = 11.sp, color = Color(0xFF00FF88))
                    }
                    Slider(
                        value = saturation,
                        onValueChange = {
                            saturation = it
                            applyEdits()
                        },
                        valueRange = 0.0f..2.0f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF00FF88), activeTrackColor = Color(0xFF00FF88))
                    )
                }
            }
        }

        // AI Vision Result Card
        visionAnalysisResult?.let { result ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF101C27)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pak AI Vision Insights", fontWeight = FontWeight.Bold, color = Color(0xFF00FF88), fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = result,
                            color = Color(0xFFD6E2EE),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
