package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.TranslationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceComparisonScreen(
    id: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.digestItemsFlow.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val item = items.find { it.id == id }

    if (item == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Article not found.")
        }
        return
    }

    val scrollState = rememberScrollState()

    var headlineText by remember(item.headline, selectedLanguage) { mutableStateOf(TranslationHelper.translate(item.headline, selectedLanguage)) }
    var sourceAFramingText by remember(item.sourceAFraming, selectedLanguage) { mutableStateOf(TranslationHelper.translate(item.sourceAFraming, selectedLanguage)) }
    var sourceBFramingText by remember(item.sourceBFraming, selectedLanguage) { mutableStateOf(TranslationHelper.translate(item.sourceBFraming, selectedLanguage)) }
    var sourceCFramingText by remember(item.sourceCFraming, selectedLanguage) { mutableStateOf(TranslationHelper.translate(item.sourceCFraming, selectedLanguage)) }

    androidx.compose.runtime.LaunchedEffect(item.id, selectedLanguage) {
        if (selectedLanguage != "English") {
            viewModel.translateText(item.headline, selectedLanguage) { headlineText = it }
            viewModel.translateText(item.sourceAFraming, selectedLanguage) { sourceAFramingText = it }
            viewModel.translateText(item.sourceBFraming, selectedLanguage) { sourceBFramingText = it }
            viewModel.translateText(item.sourceCFraming, selectedLanguage) { sourceCFramingText = it }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = TranslationHelper.translate("Perspectives Compared", selectedLanguage).uppercase(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("comparison_back_button")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Context header
            Text(
                text = TranslationHelper.translate("Coverage Comparison", selectedLanguage),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Text(
                text = headlineText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Equal Perspective 1
            PerspectiveCard(
                title = TranslationHelper.translate("Perspective Alpha", selectedLanguage),
                content = sourceAFramingText
            )

            // Equal Perspective 2
            PerspectiveCard(
                title = TranslationHelper.translate("Perspective Beta", selectedLanguage),
                content = sourceBFramingText
            )

            // Equal Perspective 3
            PerspectiveCard(
                title = TranslationHelper.translate("Perspective Gamma", selectedLanguage),
                content = sourceCFramingText
            )

            // Closing info note
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Text(
                    text = TranslationHelper.translate("Note: Media coverage can emphasize specific outcomes, risks, or priorities. We present these side-by-side with equal visual priority to encourage balanced analysis and independent conclusion formulation.", selectedLanguage),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PerspectiveCard(
    title: String,
    content: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        // Neutral Label
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Content
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(18.dp)
            )
        }
    }
}
