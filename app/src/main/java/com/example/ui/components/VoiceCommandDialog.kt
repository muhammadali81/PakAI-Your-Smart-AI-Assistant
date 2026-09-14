package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.PakAiRepository
import com.example.data.repository.VoiceCommandAction
import com.example.data.repository.VoiceCommandResult
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun VoiceCommandDialog(
    repository: PakAiRepository,
    onDismiss: () -> Unit,
    onNavigateToPdf: (String) -> Unit,
    onNavigateToPpt: (String) -> Unit,
    onNavigateToPhoto: () -> Unit,
    onNavigateToTranslator: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var spokenText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var commandResult by remember { mutableStateOf<VoiceCommandResult?>(null) }

    // TTS engine
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(Unit) {
        val speech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Ready
            }
        }
        tts = speech
        onDispose {
            speech.stop()
            speech.shutdown()
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "voice_cmd_tts")
    }

    fun handleVoiceInput(input: String) {
        spokenText = input
        scope.launch {
            isProcessing = true
            val result = repository.executeVoiceCommand(input)
            result.onSuccess { res ->
                commandResult = res
                speak(res.spokenResponse)
            }.onFailure { err ->
                Toast.makeText(context, "Voice command error: ${err.message}", Toast.LENGTH_SHORT).show()
            }
            isProcessing = false
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                handleVoiceInput(spoken)
            }
        }
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Pak AI is listening... Speak your command")
        }
        try {
            isListening = true
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            isListening = false
            Toast.makeText(context, "Speech recognizer unavailable on this device", Toast.LENGTH_SHORT).show()
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1822)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00FF88).copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pak AI Voice Assistant", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8CA1B5))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Pulsing Mic Button
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(if (isListening || isProcessing) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(Color(0xFF00FF88).copy(alpha = 0.15f))
                        .border(2.dp, Color(0xFF00FF88), CircleShape)
                        .clickable { startListening() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(40.dp), color = Color(0xFF00FF88), strokeWidth = 3.dp)
                    } else {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Tap to Speak",
                            tint = Color(0xFF00FF88),
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isListening) "Listening to your voice..."
                    else if (isProcessing) "Interpreting command with AI..."
                    else "Tap the microphone to speak",
                    color = if (isListening || isProcessing) Color(0xFF00FF88) else Color(0xFF8DA3B8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                if (spokenText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color(0xFF162330),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"$spokenText\"",
                            color = Color.White,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Command Outcome
                commandResult?.let { res ->
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF15222F)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Action: ${res.interpretedIntent}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF88),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = res.spokenResponse,
                                color = Color(0xFFD6E2EE),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            // Quick Jump button for tools
                            when (val action = res.action) {
                                is VoiceCommandAction.GeneratePdf -> {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            onDismiss()
                                            onNavigateToPdf(action.topic)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88), contentColor = Color(0xFF051208)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Open PDF Maker Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                is VoiceCommandAction.GeneratePpt -> {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            onDismiss()
                                            onNavigateToPpt(action.topic)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88), contentColor = Color(0xFF051208)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Open PPT Slide Maker", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                is VoiceCommandAction.OpenPhotoEditor -> {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            onDismiss()
                                            onNavigateToPhoto()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88), contentColor = Color(0xFF051208)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Open Photo Editor Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                is VoiceCommandAction.Translate -> {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            onDismiss()
                                            onNavigateToTranslator(action.text)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88), contentColor = Color(0xFF051208)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Open 100+ Languages Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Suggestion chips for voice
                Text(
                    text = "Try saying:",
                    fontSize = 11.sp,
                    color = Color(0xFF7A8F9E),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "• \"Make a PDF report about Mobile AI\"",
                        fontSize = 10.sp,
                        color = Color(0xFF00FF88),
                        modifier = Modifier.clickable { handleVoiceInput("Make a PDF report about Mobile AI") }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "• \"Create slides about Clean Architecture\"",
                        fontSize = 10.sp,
                        color = Color(0xFF00FF88),
                        modifier = Modifier.clickable { handleVoiceInput("Create slides about Clean Architecture") }
                    )
                }
            }
        }
    }
}
