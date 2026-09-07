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
fun EditorialDetailScreen(
    id: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.editorialItemsFlow.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val item = items.find { it.id == id }

    if (item == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Editorial not found.")
        }
        return
    }

    val scrollState = rememberScrollState()

    var titleText by remember(item.title, selectedLanguage) { mutableStateOf(TranslationHelper.translate(item.title, selectedLanguage)) }
    var fullAnalysisText by remember(item.fullAnalysisText, selectedLanguage) { mutableStateOf(TranslationHelper.translate(item.fullAnalysisText, selectedLanguage)) }

    androidx.compose.runtime.LaunchedEffect(item.id, selectedLanguage) {
        if (selectedLanguage != "English") {
            viewModel.translateText(item.title, selectedLanguage) { titleText = it }
            viewModel.translateText(item.fullAnalysisText, selectedLanguage) { fullAnalysisText = it }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = TranslationHelper.translate("Editorial Analysis", selectedLanguage).uppercase(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary, // Primary color for high contrast
                            letterSpacing = 1.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("editorial_back_button")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = MaterialTheme.colorScheme.primary // Primary color for high contrast
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
            // Headline
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 30.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Date
            Text(
                text = "Published on ${com.example.utils.DateFormatter.formatIsoDateToHumanReadable(item.date)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )



            // Full Analysis
            Text(
                text = TranslationHelper.translate("Editorial Analysis", selectedLanguage).uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(
                    text = fullAnalysisText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 26.sp // Generous line-height for body reading
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
