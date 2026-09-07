package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.ui.TranslationHelper
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Category
import com.example.data.DigestItem
import com.example.data.EditorialItem
import com.example.ui.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToSummary: (Long) -> Unit,
    onNavigateToEditorial: (Long) -> Unit,
    onNavigateToComparison: (Long) -> Unit
) {
    val digestItems by viewModel.filteredHomeItems.collectAsState()
    val editorialItems by viewModel.editorialItemsFlow.collectAsState()
    val activeHomeFilter by viewModel.homeCategoryFilter.collectAsState()
    val activeTopicPreferences by viewModel.topicPreferences.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) } // 0: Today's Digest, 1: Editorial Analysis

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            HeaderRow(
                dateString = viewModel.todayDateString,
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshNews() },
                onDownloadPdf = { viewModel.generateAndSharePdf() }
            )

            // Dynamic Segment/Tab Switcher
            TabSwitcher(
                selectedIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

            if (selectedTabIndex == 0) {
                // TODAY'S DIGEST
                TodayDigestTabContent(
                    viewModel = viewModel,
                    digestItems = digestItems,
                    editorialItems = editorialItems,
                    activeFilter = activeHomeFilter,
                    onNavigateToSummary = onNavigateToSummary,
                    onNavigateToEditorial = onNavigateToEditorial,
                    onNavigateToComparison = onNavigateToComparison
                )
            } else {
                // EDITORIAL ANALYSIS
                EditorialTabContent(
                    viewModel = viewModel,
                    editorialItems = editorialItems,
                    onNavigateToEditorial = onNavigateToEditorial
                )
            }
        }
    }
}

@Composable
fun HeaderRow(
    dateString: String,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onDownloadPdf: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = com.example.utils.DateFormatter.formatIsoDateToHumanReadable(dateString),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = com.example.R.drawable.ic_saar_logo),
                    contentDescription = "SAAR Logo",
                    tint = androidx.compose.ui.graphics.Color.Unspecified,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "SAAR",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Refresh Button
            IconButton(
                onClick = onRefresh,
                enabled = !isRefreshing,
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, shape = CircleShape)
                    .testTag("refresh_news_button")
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh news feed",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Download as PDF Action Button
            IconButton(
                onClick = onDownloadPdf,
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, shape = CircleShape)
                    .testTag("download_pdf_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download today's SAAR as PDF",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TabSwitcher(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), shape = RoundedCornerShape(24.dp))
            .padding(4.dp)
    ) {
        val tabs = listOf("Today's SAAR", "Editorial Analysis")
        tabs.forEachIndexed { index, label ->
            val isSelected = selectedIndex == index
            val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent
            val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(containerColor)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun TodayDigestTabContent(
    viewModel: MainViewModel,
    digestItems: List<DigestItem>,
    editorialItems: List<EditorialItem>,
    activeFilter: Category?,
    onNavigateToSummary: (Long) -> Unit,
    onNavigateToEditorial: (Long) -> Unit,
    onNavigateToComparison: (Long) -> Unit
) {
    // Read statistics based on raw, unfiltered today items
    val rawItems by viewModel.digestItemsFlow.collectAsState()
    val totalToday = rawItems.size
    val readToday = rawItems.count { it.isRead }
    val readFraction = if (totalToday > 0) readToday.toFloat() / totalToday else 1f
    val minReadRemaining = maxOf(1, rawItems.count { !it.isRead })

    val preferredTopics by viewModel.topicPreferences.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    // 1. Partition filtered digest items into Deep Dives and Daily Pulse
    val deepDives = remember(digestItems) { digestItems.filter { !it.isDailyPulse } }
    val dailyPulse = remember(digestItems) { digestItems.filter { it.isDailyPulse } }

    var subTabSelected by remember { mutableStateOf(0) } // 0: Deep Dives, 1: Daily Pulse

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("today_digest_scrollable_feed"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Daily Progress & Weekly Activity Stats Card
        item {
            DailyDashboardTelemetry(
                readCount = readToday,
                totalCount = totalToday,
                progress = readFraction,
                minReadRemaining = minReadRemaining
            )
        }

        // 2. Category/Topic Filters Row
        item {
            HomeCategoryFilterRow(
                selectedCategory = activeFilter,
                preferredTopics = preferredTopics,
                onCategorySelected = { viewModel.homeCategoryFilter.value = it }
            )
        }

        // 3. Sub-Tab Switcher (Deep Dives vs Daily Pulse)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val subTabs = listOf(
                    "Deep Dives (${deepDives.size})" to 0,
                    "Daily Pulse (${dailyPulse.size})" to 1
                )
                subTabs.forEach { (title, index) ->
                    val isSelected = subTabSelected == index
                    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    val borderStroke = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(containerColor)
                            .border(borderStroke, RoundedCornerShape(12.dp))
                            .clickable { subTabSelected = index }
                            .padding(vertical = 12.dp)
                            .testTag("sub_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = TranslationHelper.translate(title, selectedLanguage),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = contentColor
                        )
                    }
                }
            }
        }

        // 4. Section Header Label
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentSectionTitle = if (subTabSelected == 0) "Deep Dives Feed" else "Daily Pulse Feed"
                val currentSectionSize = if (subTabSelected == 0) deepDives.size else dailyPulse.size
                
                Text(
                    text = TranslationHelper.translate(currentSectionTitle, selectedLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "$currentSectionSize " + TranslationHelper.translate("Articles", selectedLanguage),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 5. Section Content
        if (subTabSelected == 0) {
            // --- DEEP DIVES LIST ---
            if (deepDives.isEmpty()) {
                item {
                    CaughtUpState()
                }
            } else {
                items(deepDives, key = { it.id }) { item ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        DigestItemCard(
                            item = item,
                            language = selectedLanguage,
                            onBookmarkToggle = { viewModel.toggleBookmark(item.id, item.isBookmarked) },
                            onClick = {
                                viewModel.markAsRead(item.id)
                                onNavigateToSummary(item.id)
                            },
                            onCompareClick = { onNavigateToComparison(item.id) }
                        )
                    }
                }
            }
        } else {
            // --- DAILY PULSE LIST (DYNAMIC ITERATION) ---
            if (dailyPulse.isEmpty()) {
                item {
                    CaughtUpState()
                }
            } else {
                items(dailyPulse, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 16.dp)
                            .testTag("daily_pulse_card_${item.id}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            // Header: Category
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val categoryLabel = when (item.category) {
                                    Category.Environment -> "Environment"
                                    Category.Economy -> "Economy"
                                    Category.Polity -> "Society"
                                    Category.InternationalRelations -> "Global Frame"
                                    Category.Science -> "Science"
                                    Category.Other -> "Other"
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = categoryLabel.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                }
                                
                                if (item.isRead) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Read",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = TranslationHelper.translate("Read", selectedLanguage),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Headline
                            Text(
                                text = TranslationHelper.translate(item.headline, selectedLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.clickable {
                                    viewModel.markAsRead(item.id)
                                    onNavigateToSummary(item.id)
                                }
                            )
                            
                            // Clean horizontal row of source badges
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
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                sources.forEach { source ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = source,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Conversational Body summary (strict 2-sentence maximum)
                            Text(
                                text = TranslationHelper.translate(item.previewText, selectedLanguage),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 24.sp,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Bottom row of Card actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { /* TTS placeholder */ },
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Listen",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    
                                    IconButton(
                                        onClick = { viewModel.toggleBookmark(item.id, item.isBookmarked) },
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (item.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Bookmark",
                                            tint = if (item.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedButton(
                                        onClick = { onNavigateToComparison(item.id) },
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text(TranslationHelper.translate("Compare Sources", selectedLanguage))
                                    }
                                    
                                    if (!item.isRead) {
                                        Button(
                                            onClick = { viewModel.markAsRead(item.id) },
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier.testTag("mark_read_button_${item.id}")
                                        ) {
                                            Text(TranslationHelper.translate("Got It!", selectedLanguage))
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = { onNavigateToSummary(item.id) },
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Text(TranslationHelper.translate("Read Summary", selectedLanguage))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyDashboardTelemetry(
    readCount: Int,
    totalCount: Int,
    progress: Float,
    minReadRemaining: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .testTag("daily_progress_card")
    ) {
        Text(
            text = "Welcome back.",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 30.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Here is your curated reading for today.",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$readCount of $totalCount summaries read.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            val percentage = (progress * 100).toInt()
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun DailyPackHeroCard(
    editorialItem: EditorialItem,
    language: String,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh_gradient")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f)
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(animatedOffset, animatedOffset)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("daily_pack_hero_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .clickable(onClick = onClick)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "DAILY PACK HERO",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }

                    Text(
                        text = "⏱ 5 min read",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = TranslationHelper.translate(editorialItem.title, language),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        lineHeight = 28.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = TranslationHelper.translate(editorialItem.takeawayText, language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("read_now_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = TranslationHelper.translate("Read Hero Editorial Now", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFilterRow(
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit
) {
    val categories = listOf(null) + Category.values()
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            val label = category?.displayName ?: "All Stories"
            val borderClr = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            val textClr = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            val fontWght = if (isSelected) FontWeight.Bold else FontWeight.Medium

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, borderClr, RoundedCornerShape(16.dp))
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = fontWght),
                    color = textClr
                )
            }
        }
    }
}

@Composable
fun HomeCategoryFilterRow(
    selectedCategory: Category?,
    preferredTopics: Set<String>,
    onCategorySelected: (Category?) -> Unit
) {
    val displayedCategories = remember(preferredTopics) {
        val all = Category.values().toList()
        if (preferredTopics.isEmpty()) {
            listOf(null) + all
        } else {
            listOf(null) + all.filter { preferredTopics.contains(it.name) }
        }
    }

    // Auto-reset category filter if the active filter category gets unchecked/filtered out in settings
    LaunchedEffect(preferredTopics) {
        if (preferredTopics.isNotEmpty() && selectedCategory != null && !preferredTopics.contains(selectedCategory.name)) {
            onCategorySelected(null)
        }
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(displayedCategories) { category ->
            val isSelected = selectedCategory == category
            val label = when (category) {
                null -> "All Feed"
                Category.Environment -> "Environment"
                Category.Economy -> "Economy"
                Category.Polity -> "Society"
                Category.InternationalRelations -> "Global Frame"
                Category.Science -> "Science"
                Category.Other -> "Other"
            }
            
            val icon = when (category) {
                null -> Icons.Default.Apps
                Category.Environment -> Icons.Default.Eco
                Category.Economy -> Icons.Default.TrendingUp
                Category.Polity -> Icons.Default.AccountBalance
                Category.InternationalRelations -> Icons.Default.Public
                Category.Science -> Icons.Default.Science
                Category.Other -> Icons.Default.MoreHoriz
            }
            
            val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            val borderStroke = if (isSelected) null else BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onCategorySelected(category) }
                    .testTag("home_category_tab_${category?.name ?: "all"}"),
                color = containerColor,
                contentColor = contentColor,
                shape = RoundedCornerShape(24.dp),
                border = borderStroke,
                tonalElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun DigestItemCard(
    item: DigestItem,
    language: String,
    onBookmarkToggle: () -> Unit,
    onClick: () -> Unit,
    onCompareClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("digest_item_card_${item.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isRead) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val categoryLabel = when (item.category) {
                    Category.Environment -> "Environment"
                    Category.Economy -> "Economy"
                    Category.Polity -> "Society"
                    Category.InternationalRelations -> "Global Frame"
                    Category.Science -> "Science"
                    Category.Other -> "Other"
                }
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = categoryLabel.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        ),
                        fontSize = 10.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { /* TTS Feature Placeholder */ },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("audio_tts_placeholder_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Listen to Summary",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("bookmark_toggle_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (item.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Toggle Bookmark",
                            tint = if (item.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = TranslationHelper.translate(item.headline, language),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 26.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = TranslationHelper.translate(item.previewText, language),
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(48.dp)
                        .clickable(onClick = onCompareClick)
                        .testTag("compare_link_${item.id}"),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = TranslationHelper.translate("Compare Perspectives", language),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Compare perspectives link",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(48.dp)
                        .clickable(onClick = onClick)
                        .testTag("read_summary_link_${item.id}"),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = TranslationHelper.translate("Read Summary", language),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Read summary link",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CaughtUpState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🧘", fontSize = 40.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "You're caught up for today.",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Spend the rest of your day offline, relaxing, or diving deep into learning. See you tomorrow!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun EditorialTabContent(
    viewModel: MainViewModel,
    editorialItems: List<EditorialItem>,
    onNavigateToEditorial: (Long) -> Unit
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    if (editorialItems.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "No editorials today.")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(editorialItems, key = { it.id }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToEditorial(item.id) }
                        .testTag("editorial_card_${item.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Analysis Badge Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = TranslationHelper.translate("Editorial Analysis", selectedLanguage).uppercase(),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 0.5.sp
                                    ),
                                    fontSize = 10.sp
                                )
                            }

                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Title
                        Text(
                            text = TranslationHelper.translate(item.title, selectedLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                lineHeight = 24.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Takeaway
                        Text(
                            text = TranslationHelper.translate(item.takeawayText, selectedLanguage),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Read link
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = TranslationHelper.translate("Read Summary", selectedLanguage),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Read analysis link",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
