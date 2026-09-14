package com.example.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PakAiRepository

enum class StudioTool(val label: String) {
    PDF_MAKER("PDF Maker"),
    PPT_MAKER("PPT Slides"),
    PHOTO_EDITOR("Photo Editor"),
    POLYGLOT("100+ Languages")
}

@Composable
fun ToolsStudioScreen(
    repository: PakAiRepository,
    initialTool: StudioTool = StudioTool.PDF_MAKER,
    initialTopic: String = "",
    onSaveTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(initialTool.ordinal) }
    val tabs = StudioTool.values()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF0F1722),
            contentColor = Color(0xFF00FF88),
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFF00FF88),
                        height = 3.dp
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, tool ->
                val selected = selectedTabIndex == index
                Tab(
                    selected = selected,
                    onClick = { selectedTabIndex = index },
                    icon = {
                        val icon = when (tool) {
                            StudioTool.PDF_MAKER -> Icons.Default.PictureAsPdf
                            StudioTool.PPT_MAKER -> Icons.Default.Slideshow
                            StudioTool.PHOTO_EDITOR -> Icons.Default.Image
                            StudioTool.POLYGLOT -> Icons.Default.Language
                        }
                        Icon(
                            icon,
                            contentDescription = tool.label,
                            tint = if (selected) Color(0xFF00FF88) else Color(0xFF7A8F9F),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    text = {
                        Text(
                            text = tool.label,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) Color(0xFF00FF88) else Color(0xFF7A8F9F)
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (tabs[selectedTabIndex]) {
                StudioTool.PDF_MAKER -> PdfMakerStudio(
                    repository = repository,
                    initialTopic = initialTopic
                )
                StudioTool.PPT_MAKER -> PptMakerStudio(
                    repository = repository,
                    initialTopic = initialTopic
                )
                StudioTool.PHOTO_EDITOR -> PhotoEditorStudio(
                    repository = repository
                )
                StudioTool.POLYGLOT -> UniversalTranslatorStudio(
                    repository = repository,
                    onSaveAsTask = onSaveTask
                )
            }
        }
    }
}
