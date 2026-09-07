package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    object Onboarding : Screen("onboarding", "Onboarding", null)
    object Today : Screen("today", "Today", Icons.Default.Home)
    object Bookmarks : Screen("bookmarks", "Bookmarks", Icons.Default.Bookmark)
    object Quiz : Screen("quiz", "Quiz", Icons.Default.Quiz)
    object Review : Screen("review", "Review", Icons.Default.Replay)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object SummaryDetail : Screen("summary_detail/{id}", "Summary Detail", null)
    object EditorialDetail : Screen("editorial_detail/{id}", "Editorial Detail", null)
    object SourceComparison : Screen("source_comparison/{id}", "Source Comparison", null)
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = viewModel()
) {
    val onboardingSeenState by viewModel.onboardingSeenOrNull.collectAsState()
    val isSeedingState by viewModel.isSeedingActive.collectAsState()

    // Determine startup screen once, but only after the persisted value has actually
    // loaded (non-null) — otherwise a cold start locks onto the onboarding route.
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(onboardingSeenState) {
        val seen = onboardingSeenState
        if (startDestination == null && seen != null) {
            startDestination = if (seen) Screen.Today.route else Screen.Onboarding.route
        }
    }

    if (startDestination == null || isSeedingState) {
        // Seeding database or loading settings -> Show full screen elegant calm splash
        Box(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Preparing Today's SAAR...",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        return
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Tabs that show bottom navigation bar
    val tabRoutes = listOf(Screen.Today.route, Screen.Bookmarks.route, Screen.Quiz.route, Screen.Review.route, Screen.Settings.route)
    val showBottomBar = currentRoute in tabRoutes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val items = listOf(Screen.Today, Screen.Bookmarks, Screen.Quiz, Screen.Review, Screen.Settings)
                            items.forEach { screen ->
                                val selected = currentRoute == screen.route
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable {
                                            if (currentRoute != screen.route) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        }
                                        .padding(vertical = 4.dp)
                                        .testTag("nav_item_${screen.route}"),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    val iconColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    val textColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    val fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium

                                    Icon(
                                        imageVector = screen.icon!!,
                                        contentDescription = screen.title,
                                        tint = iconColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = screen.title,
                                        fontSize = 11.sp,
                                        fontWeight = fontWeight,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination!!,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinished = {
                        viewModel.setOnboardingSeen(true)
                        navController.navigate(Screen.Today.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Today.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToSummary = { id ->
                        navController.navigate("summary_detail/$id")
                    },
                    onNavigateToEditorial = { id ->
                        navController.navigate("editorial_detail/$id")
                    },
                    onNavigateToComparison = { id ->
                        navController.navigate("source_comparison/$id")
                    }
                )
            }

            composable(Screen.Bookmarks.route) {
                BookmarksScreen(
                    viewModel = viewModel,
                    onNavigateToSummary = { id ->
                        navController.navigate("summary_detail/$id")
                    },
                    onNavigateToComparison = { id ->
                        navController.navigate("source_comparison/$id")
                    }
                )
            }

            composable(Screen.Quiz.route) {
                QuizScreen(
                    viewModel = viewModel,
                    onNavigateToSummary = { id ->
                        navController.navigate("summary_detail/$id")
                    }
                )
            }

            composable(Screen.Review.route) {
                ReviewScreen(
                    viewModel = viewModel,
                    onNavigateToSummary = { id ->
                        navController.navigate("summary_detail/$id")
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }

            composable(
                route = Screen.SummaryDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                SummaryDetailScreen(
                    id = id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToComparison = { compId ->
                        navController.navigate("source_comparison/$compId")
                    },
                    onNavigateToSummary = { relatedId ->
                        navController.navigate("summary_detail/$relatedId")
                    }
                )
            }

            composable(
                route = Screen.EditorialDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                EditorialDetailScreen(
                    id = id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.SourceComparison.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                SourceComparisonScreen(
                    id = id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
