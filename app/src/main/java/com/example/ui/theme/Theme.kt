package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = PakNeonGreen,
    onPrimary = Color(0xFF00391C),
    primaryContainer = PakGreenDark,
    onPrimaryContainer = Color(0xFF8DF7B4),
    secondary = PakGreenLight,
    onSecondary = Color(0xFF00391A),
    secondaryContainer = PakDarkSurfaceVariant,
    onSecondaryContainer = PakDarkTextSecondary,
    tertiary = PakGoldAccent,
    background = PakDarkBackground,
    onBackground = PakDarkTextPrimary,
    surface = PakDarkSurface,
    onSurface = PakDarkTextPrimary,
    surfaceVariant = PakDarkSurfaceVariant,
    onSurfaceVariant = PakDarkTextSecondary,
    outline = PakDarkSurfaceBorder,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PakGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC7F3D6),
    onPrimaryContainer = Color(0xFF00210E),
    secondary = PakGreenLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7EBDD),
    onSecondaryContainer = Color(0xFF0E3821),
    tertiary = Color(0xFFB8860B),
    background = PakLightBackground,
    onBackground = PakLightTextPrimary,
    surface = PakLightSurface,
    onSurface = PakLightTextPrimary,
    surfaceVariant = PakLightSurfaceVariant,
    onSurfaceVariant = PakLightTextSecondary,
    outline = Color(0xFFB0C4B8),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek tech dark theme as seen in user's UI preview
  dynamicColor: Boolean = false, // Keep consistent branded Pak AI green & dark aesthetic
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

