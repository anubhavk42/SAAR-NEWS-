package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DigestItem
import com.example.ui.MainViewModel
import com.example.ui.TranslationHelper

val glossaryTerms = mapOf(
    "DPDP" to "Digital Personal Data Protection Act: A landmark data privacy framework regulating personal digital data processing to balance individual privacy rights with lawful data utilization.",
    "Right to Privacy" to "A fundamental human right protected under Article 21 of the Indian Constitution, affirming individuals' control over their personal data and choices.",
    "Constitutional Provisions" to "Specific articles and sections of the Constitution that establish legal frameworks, powers, and limitations for democratic governance.",
    "Fiscal Deficit" to "The difference between a government's total expenditure and its total non-debt receipts, indicating the borrowing requirements of the government.",
    "Monetary Policy" to "The macroeconomic policy laid down by the central bank to manage money supply and interest rates to control inflation and promote growth.",
    "GDPR" to "General Data Protection Regulation: A comprehensive European Union-wide data protection law enforcing strict individual privacy controls.",
    "federalism" to "A system of government where power is constitutionally divided between a central authority and constituent political units like states.",
    "Federalism" to "A system of government where power is constitutionally divided between a central authority and constituent political units like states.",
    "judicial reform" to "Systemic improvements to the judiciary aiming to reduce case backlogs, enhance transparency, and speed up delivery of justice.",
    "Judicial Reform" to "Systemic improvements to the judiciary aiming to reduce case backlogs, enhance transparency, and speed up delivery of justice.",
    "GST" to "Goods and Services Tax: An indirect, multi-stage, destination-based tax levied on the supply of goods and services, unifying the market.",
    "CapEx" to "Capital Expenditure: Funds used by government or organizations to acquire, upgrade, and maintain physical assets like infrastructure.",
    "Capital Expenditure" to "Capital Expenditure: Funds used by government or organizations to acquire, upgrade, and maintain physical assets like infrastructure.",
    "IBC" to "Insolvency and Bankruptcy Code: A consolidated framework in India aimed at resolving insolvency of corporate persons and firms in a time-bound manner.",
    "Insolvency and Bankruptcy Code" to "Insolvency and Bankruptcy Code: A consolidated framework in India aimed at resolving insolvency of corporate persons and firms in a time-bound manner."
)

fun buildGlossaryAnnotatedString(
    text: String,
    glossaryTerms: Map<String, String>,
    primaryColor: androidx.compose.ui.graphics.Color
): AnnotatedString {
    return buildAnnotatedString {
        val matches = mutableListOf<Triple<Int, Int, String>>()
        
        glossaryTerms.keys.forEach { term ->
            var startIndex = 0
            while (true) {
                val index = text.indexOf(term, startIndex)
                if (index == -1) break
                val end = index + term.length
                
                val isOverlapping = matches.any { (s, e, _) ->
                    (index >= s && index < e) || (end > s && end <= e) || (s >= index && s < end)
                }
                
                if (!isOverlapping) {
                    matches.add(Triple(index, end, term))
                }
                startIndex = index + 1
            }
        }
        
        matches.sortBy { it.first }
        
        var currentIdx = 0
        matches.forEach { (start, end, term) ->
            if (start > currentIdx) {
                append(text.substring(currentIdx, start))
            }
            
            pushStringAnnotation(tag = "GLOSSARY", annotation = term)
            withStyle(
                style = SpanStyle(
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(text.substring(start, end))
            }
            pop()
            
            currentIdx = end
        }
        
        if (currentIdx < text.length) {
            append(text.substring(currentIdx))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryDetailScreen(
    id: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToComparison: (Long) -> Unit,
    onNavigateToSummary: (Long) -> Unit = {}
) {
    val items by viewModel.digestItemsFlow.collectAsState()
    val selectedForPdf by viewModel.selectedForPdfIds.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    var showLanguageSheet by remember { mutableStateOf(false) }
    var activeGlossaryTerm by remember { mutableStateOf<String?>(null) }

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

    val isPdfSelected = selectedForPdf.contains(id)
    val scrollState = rememberScrollState()

    var headlineText by remember(item.headline, selectedLanguage) { mutableStateOf(TranslationHelper.translate(item.headline, selectedLanguage)) }
    var contextText by remember(item.contextText, selectedLanguage) { mutableStateOf(TranslationHelper.translate(item.contextText, selectedLanguage)) }
    var keyPointsText by remember(item.keyPointsText, selectedLanguage) { mutableStateOf(TranslationHelper.translate(item.keyPointsText, selectedLanguage)) }
    var whyItMattersText by remember(item.whyItMattersText, selectedLanguage) { mutableStateOf(TranslationHelper.translate(item.whyItMattersText, selectedLanguage)) }
    var examAngleText by remember(item.examAngleText, selectedLanguage) { mutableStateOf(TranslationHelper.translate(item.examAngleText, selectedLanguage)) }

    var relatedStories by remember(item.id) { mutableStateOf<List<DigestItem>>(emptyList()) }
    androidx.compose.runtime.LaunchedEffect(item.id) {
        relatedStories = viewModel.getRelatedStories(item.category.name, item.date)
    }

    androidx.compose.runtime.LaunchedEffect(item.id, selectedLanguage) {
        if (selectedLanguage != "English") {
            viewModel.translateText(item.headline, selectedLanguage) { headlineText = it }
            viewModel.translateText(item.contextText, selectedLanguage) { contextText = it }
            viewModel.translateText(item.keyPointsText, selectedLanguage) { keyPointsText = it }
            viewModel.translateText(item.whyItMattersText, selectedLanguage) { whyItMattersText = it }
            viewModel.translateText(item.examAngleText, selectedLanguage) { examAngleText = it }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = TranslationHelper.translate(item.category.displayName.uppercase().replace("\n", " "), selectedLanguage),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("summary_back_button")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { /* Audio/TTS feature placeholder */ },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("summary_audio_tts_placeholder")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen to Summary",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { showLanguageSheet = true },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("summary_language_switcher")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Switch Language",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleBookmark(item.id, item.isBookmarked) },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("summary_bookmark_toggle")
                        ) {
                            Icon(
                                imageVector = if (item.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Toggle Bookmark",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            // Sticky Bottom Row Actions
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onNavigateToComparison(item.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(TranslationHelper.translate("Compare Perspectives", selectedLanguage), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { viewModel.togglePdfSelection(item.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPdfSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            contentColor = if (isPdfSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = if (isPdfSelected) TranslationHelper.translate("Added to PDF ✓", selectedLanguage) else TranslationHelper.translate("Add to Today's PDF", selectedLanguage),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
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
                text = headlineText,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    lineHeight = 32.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Dynamic & Elegant Article Header Source Badges Row
            val sources = remember(item) {
                val parsed = listOf(item.sourceAFraming, item.sourceBFraming, item.sourceCFraming)
                    .map { framing ->
                        if (framing.isEmpty()) return@map ""
                        val beforeColon = framing.substringBefore(":")
                        val beforeParen = beforeColon.substringBefore("(").trim()
                        if (beforeParen.length < 40 && beforeParen.isNotEmpty() && beforeParen != framing) {
                            beforeParen
                        } else {
                            ""
                        }
                    }
                    .filter { it.isNotBlank() }
                    .distinct()
                
                if (parsed.isNotEmpty()) parsed else listOf("The Hindu", "Indian Express", "LiveMint")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                sources.forEach { source ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)) // Soft gray background
                            .border(
                                BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("source_badge_$source")
                    ) {
                        Text(
                            text = source,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Crisp neutral typography
                        )
                    }
                }
            }

            // Date
            Text(
                text = "${TranslationHelper.translate("Compiled on", selectedLanguage)} ${com.example.utils.DateFormatter.formatIsoDateToHumanReadable(item.date)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Section 1: Context
            DetailSection(
                title = TranslationHelper.translate("Context", selectedLanguage),
                content = contextText,
                onGlossaryTermClicked = { activeGlossaryTerm = it }
            )

            // Section 2: Key Points
            DetailSection(
                title = TranslationHelper.translate("Key Points", selectedLanguage),
                content = keyPointsText,
                onGlossaryTermClicked = { activeGlossaryTerm = it }
            )

            // Section 3: Why It Matters
            DetailSection(
                title = TranslationHelper.translate("Why It Matters", selectedLanguage),
                content = whyItMattersText,
                onGlossaryTermClicked = { activeGlossaryTerm = it }
            )

            // Section 4: Exam Angle Context Card
            ExamAngleCard(
                content = examAngleText,
                selectedLanguage = selectedLanguage,
                onGlossaryTermClicked = { activeGlossaryTerm = it }
            )

            // Supplementary: earlier stories on the same topic ("Connect the Dots").
            RelatedStoriesSection(
                stories = relatedStories,
                onOpenStory = onNavigateToSummary,
                modifier = Modifier.padding(top = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    val activeGlossaryDefinition = activeGlossaryTerm?.let { glossaryTerms[it] }
    if (activeGlossaryTerm != null && activeGlossaryDefinition != null) {
        AlertDialog(
            onDismissRequest = { activeGlossaryTerm = null },
            confirmButton = {
                TextButton(
                    onClick = { activeGlossaryTerm = null },
                    modifier = Modifier.testTag("glossary_close_button")
                ) {
                    Text("Got It", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Glossary Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = activeGlossaryTerm ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Text(
                    text = activeGlossaryDefinition,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }

    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Title
                Text(
                    text = "Select Language",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Choose your preferred language for article translations.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                Spacer(modifier = Modifier.height(16.dp))

                val languages = listOf("English", "Hindi", "Marathi", "Tamil", "Telugu")
                languages.forEach { language ->
                    val isSelected = selectedLanguage == language || (selectedLanguage.isEmpty() && language == "English")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable {
                                viewModel.setLanguage(language)
                                showLanguageSheet = false
                            }
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = language,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                }
            }
        }
    }
}

@Composable
fun DetailSection(
    title: String,
    content: String,
    onGlossaryTermClicked: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // Section Title
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Section Content Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            val annotated = buildGlossaryAnnotatedString(
                text = content,
                glossaryTerms = glossaryTerms,
                primaryColor = MaterialTheme.colorScheme.primary
            )
            ClickableText(
                text = annotated,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    lineHeight = 26.sp
                ),
                modifier = Modifier.padding(18.dp),
                onClick = { offset ->
                    annotated.getStringAnnotations(tag = "GLOSSARY", start = offset, end = offset)
                        .firstOrNull()?.let { annotation ->
                            onGlossaryTermClicked(annotation.item)
                        }
                }
            )
        }
    }
}

@Composable
fun ExamAngleCard(
    content: String,
    selectedLanguage: String,
    onGlossaryTermClicked: (String) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val examBgColor = if (isDark) Color(0xFF262A24) else Color(0xFFFAF7EE) // Soft contrasting warm cream tint
    val examBorderColor = if (isDark) Color(0xFF3B4138) else Color(0xFFE5DECE)
    val bulletColor = if (isDark) Color(0xFF99B299) else Color(0xFF704214)

    val bullets = remember(content) {
        val cleanText = content
            .removePrefix("Important concepts:")
            .removePrefix("Important concepts :")
            .removePrefix("Focus on")
            .removePrefix("Focus on:")
            .trim()
        cleanText.split(",")
            .map {
                var item = it.trim()
                if (item.startsWith("and ", ignoreCase = true)) {
                    item = item.substring(4)
                } else if (item.startsWith("and", ignoreCase = true)) {
                    item = item.substring(3)
                }
                item.trim().trimEnd('.').replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
            }
            .filter { it.isNotEmpty() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // Section Title
        Text(
            text = TranslationHelper.translate("Exam Angle", selectedLanguage).uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("exam_angle_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = examBgColor),
            border = BorderStroke(1.2.dp, examBorderColor)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header with Badge & Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(bulletColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info, // clean icon badge
                            contentDescription = "Exam Icon",
                            tint = bulletColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "Syllabus Mapping",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Bold Focus Line
                Text(
                    text = "UPSC / Banking & Civil Services Aspirants Focus:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Bulleted syllabus mapping block
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    bullets.forEach { bullet ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = bulletColor,
                                modifier = Modifier.offset(y = (-1).dp)
                            )
                            
                            val annotated = buildGlossaryAnnotatedString(
                                text = bullet,
                                glossaryTerms = glossaryTerms,
                                primaryColor = MaterialTheme.colorScheme.primary
                            )
                            
                            ClickableText(
                                text = annotated,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Serif,
                                    lineHeight = 22.sp
                                ),
                                onClick = { offset ->
                                    annotated.getStringAnnotations(tag = "GLOSSARY", start = offset, end = offset)
                                        .firstOrNull()?.let { annotation ->
                                            onGlossaryTermClicked(annotation.item)
                                        }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
