package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LightPrimaryAccent
import com.example.ui.theme.LightHighlightAccent
import kotlinx.coroutines.launch

data class OnboardingSlide(
    val title: String,
    val description: String,
    val illustration: @Composable () -> Unit,
    val accentText: String
)

@Composable
fun CozyReadingIllustration() {
    Box(
        modifier = Modifier
            .size(180.dp)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glowing background aura
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            LightHighlightAccent.copy(alpha = 0.2f),
                            LightPrimaryAccent.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        // Base canvas for beautiful artistic vector details (plant leaves, stars)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            
            // Draw soft background glowing disk
            drawCircle(
                color = LightHighlightAccent.copy(alpha = 0.15f),
                radius = size.minDimension * 0.28f,
                center = Offset(centerX, centerY - 15f)
            )
            
            // Left organic plant stem
            val pathLeft = Path().apply {
                moveTo(centerX - 65f, centerY + 60f)
                quadraticTo(centerX - 85f, centerY - 10f, centerX - 55f, centerY - 45f)
                quadraticTo(centerX - 45f, centerY - 15f, centerX - 60f, centerY + 55f)
            }
            drawPath(
                path = pathLeft,
                color = LightPrimaryAccent.copy(alpha = 0.25f)
            )

            // Right organic plant stem
            val pathRight = Path().apply {
                moveTo(centerX + 65f, centerY + 60f)
                quadraticTo(centerX + 85f, centerY, centerX + 55f, centerY - 35f)
                quadraticTo(centerX + 45f, centerY - 10f, centerX + 60f, centerY + 55f)
            }
            drawPath(
                path = pathRight,
                color = LightPrimaryAccent.copy(alpha = 0.25f)
            )
            
            // Golden sparkles
            drawCircle(
                color = LightHighlightAccent.copy(alpha = 0.8f),
                radius = 3.5f,
                center = Offset(centerX - 40f, centerY - 65f)
            )
            drawCircle(
                color = LightHighlightAccent.copy(alpha = 0.6f),
                radius = 2.5f,
                center = Offset(centerX + 45f, centerY - 55f)
            )
        }

        // Overlapping layered Compose components representing an Open Book
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Left page
            Box(
                modifier = Modifier
                    .size(width = 46.dp, height = 66.dp)
                    .graphicsLayer {
                        rotationZ = -7f
                        transformOrigin = TransformOrigin(1f, 1f)
                    }
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 2.dp, topEnd = 2.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 2.dp, topEnd = 2.dp)
                    )
                    .padding(6.dp)
            ) {
                // Book lines
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(4) { idx ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (idx == 3) 0.55f else 0.85f)
                                .height(2.dp)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f))
                        )
                    }
                }
            }

            // Right page
            Box(
                modifier = Modifier
                    .size(width = 46.dp, height = 66.dp)
                    .graphicsLayer {
                        rotationZ = 7f
                        transformOrigin = TransformOrigin(0f, 1f)
                    }
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 2.dp, topStart = 2.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 2.dp, topStart = 2.dp)
                    )
                    .padding(6.dp)
            ) {
                // Book lines
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(4) { idx ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (idx == 3) 0.5f else 0.85f)
                                .height(2.dp)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f))
                        )
                    }
                }
            }
        }
        
        // Soft glowing light beam radiating from the book
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.35f }
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val beamPath = Path().apply {
                moveTo(centerX, centerY - 40f)
                lineTo(centerX - 65f, centerY + 70f)
                lineTo(centerX + 65f, centerY + 70f)
                close()
            }
            drawPath(
                path = beamPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LightHighlightAccent.copy(alpha = 0.35f),
                        LightHighlightAccent.copy(alpha = 0.0f)
                    )
                )
            )
        }
    }
}

@Composable
fun BalancedPerspectivesIllustration() {
    Box(
        modifier = Modifier
            .size(180.dp)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Soft background halo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            LightPrimaryAccent.copy(alpha = 0.12f),
                            LightHighlightAccent.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Draw gentle zen water ripples representing information settling into perfect alignment
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            drawCircle(
                color = LightPrimaryAccent.copy(alpha = 0.12f),
                radius = size.minDimension * 0.42f,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )
            drawCircle(
                color = LightPrimaryAccent.copy(alpha = 0.06f),
                radius = size.minDimension * 0.32f,
                center = Offset(centerX, centerY),
                style = Stroke(width = 1.5f)
            )
        }

        // Stack of balanced organic stones/pebbles representing three perspectives
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-5).dp),
            modifier = Modifier.offset(y = 8.dp)
        ) {
            // Top pebble (Golden perspective)
            Box(
                modifier = Modifier
                    .size(width = 38.dp, height = 24.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                LightHighlightAccent,
                                LightHighlightAccent.copy(alpha = 0.85f)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(50)
                    )
            )

            // Middle pebble (Teal/primary perspective)
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 34.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                LightPrimaryAccent,
                                LightPrimaryAccent.copy(alpha = 0.85f)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(50)
                    )
            )

            // Bottom pebble (Grounded slate/grey perspective)
            Box(
                modifier = Modifier
                    .size(width = 92.dp, height = 44.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    .border(
                        width = 1.2.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

@Composable
fun PortabilityIllustration() {
    Box(
        modifier = Modifier
            .size(180.dp)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glowing background halo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            LightHighlightAccent.copy(alpha = 0.12f),
                            LightPrimaryAccent.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Soaring path details
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            
            // Dashed flight loop
            val path = Path().apply {
                moveTo(centerX - 75f, centerY + 45f)
                quadraticTo(centerX - 15f, centerY - 10f, centerX + 55f, centerY - 55f)
            }
            drawPath(
                path = path,
                color = LightHighlightAccent.copy(alpha = 0.35f),
                style = Stroke(
                    width = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                )
            )

            // Minimal star spark
            drawCircle(
                color = LightHighlightAccent,
                radius = 3.5f,
                center = Offset(centerX + 30f, centerY - 40f)
            )
            drawCircle(
                color = LightPrimaryAccent,
                radius = 2.5f,
                center = Offset(centerX - 45f, centerY + 15f)
            )
        }

        // Base document sheet
        Box(
            modifier = Modifier
                .size(76.dp, 102.dp)
                .offset(x = (-16).dp, y = 14.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Document top-row header
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).background(LightHighlightAccent, CircleShape))
                    Box(modifier = Modifier.width(32.dp).height(4.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)))
                }
                
                // Document text representation lines
                repeat(4) { i ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (i == 3) 0.5f else 1.0f)
                            .height(3.dp)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Modern micro-chart
                Row(
                    modifier = Modifier.fillMaxWidth().height(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    repeat(4) { idx ->
                        val ratio = when(idx) {
                            0 -> 0.4f
                            1 -> 0.85f
                            2 -> 0.55f
                            else -> 0.95f
                        }
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight(ratio)
                                .background(LightPrimaryAccent.copy(alpha = 0.65f), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                        )
                    }
                }
            }
        }

        // Origami bird/paper flyer taking flight
        Box(
            modifier = Modifier
                .size(46.dp)
                .offset(x = 36.dp, y = (-26).dp)
                .graphicsLayer {
                    rotationZ = 15f
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                
                // Primary origami wing
                val wingPath1 = Path().apply {
                    moveTo(w * 0.9f, h * 0.1f)
                    lineTo(w * 0.1f, h * 0.45f)
                    lineTo(w * 0.4f, h * 0.6f)
                    close()
                }
                drawPath(
                    path = wingPath1,
                    color = LightPrimaryAccent
                )

                // Secondary highlighted origami wing
                val wingPath2 = Path().apply {
                    moveTo(w * 0.9f, h * 0.1f)
                    lineTo(w * 0.4f, h * 0.6f)
                    lineTo(w * 0.65f, h * 0.85f)
                    close()
                }
                drawPath(
                    path = wingPath2,
                    color = LightHighlightAccent
                )

                // Underbody fold shadow
                val shadowPath = Path().apply {
                    moveTo(w * 0.4f, h * 0.6f)
                    lineTo(w * 0.52f, h * 0.68f)
                    lineTo(w * 0.65f, h * 0.85f)
                    close()
                }
                drawPath(
                    path = shadowPath,
                    color = LightPrimaryAccent.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val slides = listOf(
        OnboardingSlide(
            title = "Calm, Bounded Reading",
            description = "Spend exactly 5 minutes staying informed every day. No infinite scrolling, no feed fatigue, just facts curated for deep analytical understanding.",
            illustration = { CozyReadingIllustration() },
            accentText = "5 Min / Day Limit"
        ),
        OnboardingSlide(
            title = "Neutral, Multi-Perspective",
            description = "Compare coverage of critical issues side-by-side from multiple national and regional perspectives. See how framing shapes the narrative.",
            illustration = { BalancedPerspectivesIllustration() },
            accentText = "3-Source Comparison"
        ),
        OnboardingSlide(
            title = "Offline-First Portability",
            description = "Generate elegant, multi-page PDFs of today's summaries with exam-aligned analytics. Export, print, or share instantly without data connections.",
            illustration = { PortabilityIllustration() },
            accentText = "Download as PDF"
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // 1. Top Bar with Skip Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onFinished) {
                Text(
                    text = "Skip",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // 2. Centered Slide Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Custom soothing pure Compose illustration replacing the static icons!
                    slide.illustration()

                    Spacer(modifier = Modifier.height(24.dp))

                    // Accent tag/badge
                    Surface(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                        contentColor = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = slide.accentText.uppercase(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    // Onboarding horizontal row of progress dots centered above the headline text
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        repeat(slides.size) { index ->
                            val active = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .size(if (active) 10.dp else 8.dp)
                                    .clip(CircleShape)
                                    .then(
                                        if (active) {
                                            Modifier.background(MaterialTheme.colorScheme.primary)
                                        } else {
                                            Modifier
                                                .background(Color.Transparent)
                                                .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), CircleShape)
                                        }
                                    )
                            )
                        }
                    }

                    // Slide Title (Headline) - Increased font size for 'Calm, Bounded Reading' to make it prominent
                    val isFirstSlide = page == 0
                    val titleFontSize = if (isFirstSlide) 36.sp else 28.sp
                    val titleLineHeight = if (isFirstSlide) 44.sp else 36.sp
                    val titleFontWeight = if (isFirstSlide) FontWeight.ExtraBold else FontWeight.Bold

                    Text(
                        text = slide.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = titleFontWeight,
                            fontSize = titleFontSize,
                            lineHeight = titleLineHeight,
                            letterSpacing = if (isFirstSlide) (-0.5).sp else 0.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Slide Description (Body copy)
                    Text(
                        text = slide.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Primary Button at the bottom
        Button(
            onClick = {
                if (pagerState.currentPage < slides.size - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onFinished()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(27.dp)
        ) {
            Text(
                text = if (pagerState.currentPage == slides.size - 1) "Get Started" else "Next",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

