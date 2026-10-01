package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.FitnessViewModel
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.nutrition.NutritionScreen
import com.example.ui.screens.plans.PlanDetailScreen
import com.example.ui.screens.plans.TrainingPlansScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.progress.ProgressScreen
import com.example.ui.screens.workouts.ActiveWorkoutScreen
import com.example.ui.screens.workouts.WorkoutDetailScreen
import com.example.ui.screens.workouts.WorkoutsScreen
import com.example.ui.theme.FlameOrange

@Composable
fun PulseFitApp(
    viewModel: FitnessViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val activeWorkoutState by viewModel.activeWorkout.collectAsStateWithLifecycle()
    val isCurrentlyInActiveWorkoutScreen = currentRoute == Screen.ActiveWorkout.route

    Scaffold(
        bottomBar = {
            Column {
                // If workout is active but user navigated to another screen, show mini-bar
                AnimatedVisibility(
                    visible = activeWorkoutState.isActive && !isCurrentlyInActiveWorkoutScreen,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Screen.ActiveWorkout.route) {
                                    launchSingleTop = true
                                }
                            }
                            .testTag("mini_workout_bar")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = activeWorkoutState.sessionName.ifBlank { "Workout in Progress" },
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    val minutes = activeWorkoutState.elapsedSeconds / 60
                                    val seconds = activeWorkoutState.elapsedSeconds % 60
                                    Text(
                                        text = String.format("%02d:%02d", minutes, seconds),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "RESUME",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }

                // Standard bottom nav
                if (!isCurrentlyInActiveWorkoutScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        bottomNavScreens.forEach { screen ->
                            val selected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    screen.icon?.let { icon ->
                                        Icon(imageVector = icon, contentDescription = screen.title)
                                    }
                                },
                                label = { Text(screen.title) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = FlameOrange,
                                    selectedTextColor = FlameOrange,
                                    indicatorColor = FlameOrange.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_tab_${screen.route}")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToWorkouts = {
                        navController.navigate(Screen.Workouts.route) { launchSingleTop = true }
                    },
                    onNavigateToActiveWorkout = {
                        navController.navigate(Screen.ActiveWorkout.route) { launchSingleTop = true }
                    },
                    onNavigateToNutrition = {
                        navController.navigate(Screen.Nutrition.route) { launchSingleTop = true }
                    },
                    onNavigateToPlans = {
                        navController.navigate(Screen.Plans.route) { launchSingleTop = true }
                    },
                    onNavigateToProgress = {
                        navController.navigate(Screen.Progress.route) { launchSingleTop = true }
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route) { launchSingleTop = true }
                    }
                )
            }

            composable(Screen.Progress.route) {
                ProgressScreen(viewModel = viewModel)
            }

            composable(Screen.Workouts.route) {
                WorkoutsScreen(
                    viewModel = viewModel,
                    onNavigateToActiveWorkout = {
                        navController.navigate(Screen.ActiveWorkout.route) { launchSingleTop = true }
                    },
                    onNavigateToWorkoutDetail = { sessionId ->
                        navController.navigate(Screen.WorkoutDetail.createRoute(sessionId))
                    }
                )
            }

            composable(Screen.Nutrition.route) {
                NutritionScreen(viewModel = viewModel)
            }

            composable(Screen.Plans.route) {
                TrainingPlansScreen(
                    viewModel = viewModel,
                    onNavigateToPlanDetail = { planId ->
                        navController.navigate(Screen.PlanDetail.createRoute(planId))
                    },
                    onStartWorkout = {
                        navController.navigate(Screen.ActiveWorkout.route) { launchSingleTop = true }
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(viewModel = viewModel)
            }

            composable(Screen.ActiveWorkout.route) {
                ActiveWorkoutScreen(
                    viewModel = viewModel,
                    onFinishOrDiscard = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.WorkoutDetail.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
                WorkoutDetailScreen(
                    sessionId = sessionId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.PlanDetail.route,
                arguments = listOf(navArgument("planId") { type = NavType.LongType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getLong("planId") ?: 0L
                PlanDetailScreen(
                    planId = planId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onStartWorkout = {
                        navController.navigate(Screen.ActiveWorkout.route) { launchSingleTop = true }
                    }
                )
            }
        }
    }
}
