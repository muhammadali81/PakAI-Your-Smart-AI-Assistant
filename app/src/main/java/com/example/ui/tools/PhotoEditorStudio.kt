package com.example.ui.tools

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.data.repository.PakAiRepository
import com.example.utils.BitmapFilterUtils
import com.example.utils.PhotoFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

@Composable
fun PhotoEditorStudio(
    repository: PakAiRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Create Photo from Prompt, 1 = Edit & Filter Photo

    // ----------------- State for Edit & Filter & Vision -----------------
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var displayedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedFilter by remember { mutableStateOf(PhotoFilter.ORIGINAL) }
    var brightness by remember { mutableFloatStateOf(0f) }
    var contrast by remember { mutableFloatStateOf(1.0f) }
    var saturation by remember { mutableFloatStateOf(1.0f) }

    var visionPrompt by remember { mutableStateOf("Analyze this photo in detail: describe scene, objects, colors, text, and aesthetic evaluation.") }
    var isAnalyzingVision by remember { mutableStateOf(false) }
    var visionAnalysisResult by remember { mutableStateOf<String?>(null) }

    // ----------------- State for Prompt-Based Photo Creation -----------------
    var creationPrompt by remember { mutableStateOf("Majestic Badshahi Mosque in Lahore at sunset, golden hour light, reflecting pool, highly detailed 4K photorealistic") }
    var creationStyle by remember { mutableStateOf("Photorealistic 4K") }
    var generatedPhotoUrl by remember { mutableStateOf<String?>(null) }
    var isGeneratingPhoto by remember { mutableStateOf(false) }

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
        canvas.drawText("Tap \"Choose Photo\" or enter prompt below to create photo", w / 2f, h / 2f + 110f, subPaint)

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

    // Android Photo Picker
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
                    activeTab = 1 // Switch to edit & vision view
                    Toast.makeText(context, "Photo loaded successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Could not load image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun shareBitmap(bmp: Bitmap) {
        try {
            val file = BitmapFilterUtils.saveBitmapToCache(context, bmp)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(sendIntent, "Share Photo"))
        } catch (e: Exception) {
            Toast.makeText(context, "Sharing failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadGeneratedUrlIntoEditor(url: String) {
        scope.launch {
            Toast.makeText(context, "Loading photo into editor...", Toast.LENGTH_SHORT).show()
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()
            val result = withContext(Dispatchers.IO) { loader.execute(request) }
            if (result is SuccessResult) {
                val drawable = result.drawable
                if (drawable is BitmapDrawable) {
                    originalBitmap = drawable.bitmap
                    displayedBitmap = drawable.bitmap
                    selectedFilter = PhotoFilter.ORIGINAL
                    brightness = 0f
                    contrast = 1.0f
                    saturation = 1.0f
                    activeTab = 1
                    Toast.makeText(context, "Photo loaded! You can now edit and add filters.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Could not load photo bitmap. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C141D))
    ) {
        // Studio Mode Tabs
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color(0xFF101923),
            contentColor = Color(0xFF00FF88),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = Color(0xFF00FF88),
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("پرامپٹ سے تصویر بنائیں (Create Photo)", fontSize = 12.sp, fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal)
                    }
                },
                selectedContentColor = Color(0xFF00FF88),
                unselectedContentColor = Color(0xFF8DA3B8)
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ایڈٹ و پرامپٹ تجزیہ (Edit & Vision)", fontSize = 12.sp, fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal)
                    }
                },
                selectedContentColor = Color(0xFF00FF88),
                unselectedContentColor = Color(0xFF8DA3B8)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ============================================================
            // TAB 0: CREATE PHOTO ACCORDING TO PROMPT
            // ============================================================
            if (activeTab == 0) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131E2A)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223548))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00FF88))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "پرامپٹ لکھیں اور نئی تصویر تیار کریں",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Pak AI Neural Visual Core • High Resolution Photorealistic Generation",
                                fontSize = 11.sp,
                                color = Color(0xFF8DA3B8)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = creationPrompt,
                                onValueChange = { creationPrompt = it },
                                placeholder = { Text("مثال: لاہور کی بادشاہی مسجد، کے ٹو پہاڑ کا دلکش نظارہ، پاکستانی ٹرک آرٹ...", color = Color(0xFF6B7E93), fontSize = 12.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
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

                            // Sample Prompt Inspiration Chips
                            Text("💡 تیار شدہ پرامپٹس (Inspiration Ideas):", fontSize = 11.sp, color = Color(0xFF00FF88), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "🕌 Badshahi Mosque Lahore sunset 4K photorealistic" to "Badshahi Mosque in Lahore at sunset with golden lanterns, crystal pool reflection, 4K photorealistic",
                                    "🏔️ K2 mountain peak golden hour" to "Majestic snow-peaked K2 mountain in Pakistan at golden hour, sharp sunlight, dramatic clouds, hyperrealistic",
                                    "🎨 Pakistani Truck Art eagle" to "Intricate Pakistani truck art design of an eagle with vibrant floral patterns and calligraphy, kaleidoscopic vivid colors",
                                    "🏙️ Futuristic Islamabad Cyberpunk" to "Futuristic Islamabad cityscape with Faisal Mosque silhouette and neon cyberpunk skyline, cinematic lighting",
                                    "☕ Northern Pakistan tea dhaba" to "Cozy warm tea dhaba in Hunza valley northern Pakistan, steaming chai cup with snow mountains background, soft warm lighting"
                                ).forEach { (label, fullPrompt) ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF1A2735),
                                        modifier = Modifier.clickable { creationPrompt = fullPrompt }
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            color = Color(0xFFD6E2EE),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Style selector
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Photorealistic 4K", "Cinematic Render", "Islamic Art", "Oil Painting", "3D Digital Art").forEach { style ->
                                    FilterChip(
                                        selected = creationStyle == style,
                                        onClick = { creationStyle = style },
                                        label = { Text(style, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF00FF88),
                                            selectedLabelColor = Color(0xFF08120A),
                                            containerColor = Color(0xFF172433),
                                            labelColor = Color(0xFF8DA3B8)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (creationPrompt.isBlank()) return@Button
                                    scope.launch {
                                        isGeneratingPhoto = true
                                        val finalPrompt = "$creationPrompt, $creationStyle, masterpiece, highly detailed, high quality"
                                        generatedPhotoUrl = repository.generatePhotoUrl(finalPrompt)
                                        isGeneratingPhoto = false
                                        Toast.makeText(context, "Photo created according to prompt!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = !isGeneratingPhoto && creationPrompt.isNotBlank(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00FF88),
                                    contentColor = Color(0xFF081910)
                                )
                            ) {
                                if (isGeneratingPhoto) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF081910), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("تصویر تیار کی جا رہی ہے...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("تصویر بنائیں (Generate Photo)", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Generated Photo Preview Card
                generatedPhotoUrl?.let { url ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141F2B)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🖼️ پرامپٹ کے مطابق تیار شدہ تصویر (Created Photo)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF88),
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    color = Color(0xFF0D141C)
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = creationPrompt,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { loadGeneratedUrlIntoEditor(url) },
                                        modifier = Modifier.weight(1.3f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88), contentColor = Color(0xFF08120A))
                                    ) {
                                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("اسٹوڈیو میں ایڈٹ کریں", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, "Created with Pak AI Photo Studio by Muhammad Ali:\nPrompt: $creationPrompt\n$url")
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(sendIntent, "Share Generated Photo"))
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Share", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ============================================================
            // TAB 1: EDIT & FILTER PHOTO + VISION PROMPT ANALYSIS
            // ============================================================
            if (activeTab == 1) {
                item {
                    // Action Header with Choose Photo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Photo Editor & Adjustments",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Adjust brightness, contrast, filters & prompt vision",
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
                            Text("تصویر منتخب کریں", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Image Canvas
                item {
                    displayedBitmap?.let { bmp ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
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

                // Action Toolbar (Rotate, Flip, Save, Share)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
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
                            Icon(Icons.Default.RotateRight, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF00FF88))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rotate", fontSize = 11.sp, color = Color.White)
                        }

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
                            Icon(Icons.Default.Flip, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF00FF88))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Flip", fontSize = 11.sp, color = Color.White)
                        }

                        Button(
                            onClick = {
                                displayedBitmap?.let { bmp ->
                                    val uri = BitmapFilterUtils.saveBitmapToGallery(context, bmp, "PakAI_Photo_${System.currentTimeMillis()}.jpg")
                                    if (uri != null) {
                                        Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Saved to App Cache", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88), contentColor = Color(0xFF08120A))
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { displayedBitmap?.let { shareBitmap(it) } },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF00FF88))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }

                // AI Vision on Uploaded Photo with Prompt
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141F2B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223548))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "پرامپٹ کے مطابق تصویر کا تجزیہ (AI Vision Lens)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF88),
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = visionPrompt,
                                onValueChange = { visionPrompt = it },
                                placeholder = { Text("تصویر کے متعلق سوال یا پرامپٹ لکھیں...", color = Color.Gray, fontSize = 11.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00FF88),
                                    unfocusedBorderColor = Color(0xFF2B3C4E),
                                    focusedContainerColor = Color(0xFF0E1620),
                                    unfocusedContainerColor = Color(0xFF0E1620),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick Vision Prompts
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "🔍 Extract Text (OCR)" to "Read and transcribe all visible text in this photo with high accuracy.",
                                    "👁️ Identify Objects" to "List and describe every object, person, and focal point in this picture.",
                                    "🌐 Translate to Urdu" to "Detect any text or signs in this photo and translate them into fluent Urdu.",
                                    "🎨 Color Palette" to "Extract the primary color palette, lighting tones, and aesthetic harmony.",
                                    "💡 Improvement Tips" to "Provide 3 professional photography suggestions to improve this shot."
                                ).forEach { (label, full) ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF1C2B3A),
                                        modifier = Modifier.clickable { visionPrompt = full }
                                    ) {
                                        Text(label, fontSize = 10.sp, color = Color(0xFF00FF88), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    displayedBitmap?.let { bmp ->
                                        scope.launch {
                                            isAnalyzingVision = true
                                            val b64 = BitmapFilterUtils.bitmapToBase64(bmp)
                                            val result = repository.analyzeImage(b64, visionPrompt)
                                            result.onSuccess { text ->
                                                visionAnalysisResult = text
                                            }.onFailure { e ->
                                                Toast.makeText(context, "AI Vision error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                            isAnalyzingVision = false
                                        }
                                    }
                                },
                                enabled = !isAnalyzingVision && displayedBitmap != null,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88), contentColor = Color(0xFF08120A))
                            ) {
                                if (isAnalyzingVision) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF08120A), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Analyzing Photo...", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("پرامپٹ کے مطابق تجزیہ کریں (Run Vision Analysis)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
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
                                Text("💡 تجزئیہ اور نتائج (Vision Insights):", fontWeight = FontWeight.Bold, color = Color(0xFF00FF88), fontSize = 12.sp)
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

                // Filters List
                item {
                    Text("10+ Artistic Filters", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
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

                // Adjustments Sliders Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141E29)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223548))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Fine Tuning Adjustments", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                Text(
                                    text = "Reset",
                                    color = Color(0xFF00FF88),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        brightness = 0f
                                        contrast = 1.0f
                                        saturation = 1.0f
                                        selectedFilter = PhotoFilter.ORIGINAL
                                        applyEdits()
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Brightness
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Brightness", fontSize = 11.sp, color = Color(0xFF8DA3B8))
                                Text("${brightness.toInt()}", fontSize = 11.sp, color = Color(0xFF00FF88))
                            }
                            Slider(
                                value = brightness,
                                onValueChange = { brightness = it; applyEdits() },
                                valueRange = -80f..80f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF00FF88), activeTrackColor = Color(0xFF00FF88))
                            )

                            // Contrast
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Contrast", fontSize = 11.sp, color = Color(0xFF8DA3B8))
                                Text(String.format("%.2fx", contrast), fontSize = 11.sp, color = Color(0xFF00FF88))
                            }
                            Slider(
                                value = contrast,
                                onValueChange = { contrast = it; applyEdits() },
                                valueRange = 0.5f..2.0f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF00FF88), activeTrackColor = Color(0xFF00FF88))
                            )

                            // Saturation
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Saturation", fontSize = 11.sp, color = Color(0xFF8DA3B8))
                                Text(String.format("%.2fx", saturation), fontSize = 11.sp, color = Color(0xFF00FF88))
                            }
                            Slider(
                                value = saturation,
                                onValueChange = { saturation = it; applyEdits() },
                                valueRange = 0.0f..2.0f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF00FF88), activeTrackColor = Color(0xFF00FF88))
                            )
                        }
                    }
                }
            }
        }
    }
}
