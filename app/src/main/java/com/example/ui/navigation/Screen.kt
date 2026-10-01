package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    data object Workouts : Screen("workouts", "Workouts", Icons.Default.FitnessCenter)
    data object Nutrition : Screen("nutrition", "Nutrition", Icons.Default.Restaurant)
    data object Progress : Screen("progress", "Progress", Icons.Default.TrendingUp)
    data object Plans : Screen("plans", "Plans", Icons.Default.AutoAwesome)
    data object Profile : Screen("profile", "Profile", Icons.Default.Person)

    data object ActiveWorkout : Screen("active_workout", "Active Workout")
    data object WorkoutDetail : Screen("workout_detail/{sessionId}", "Workout Detail") {
        fun createRoute(sessionId: Long) = "workout_detail/$sessionId"
    }
    data object PlanDetail : Screen("plan_detail/{planId}", "Plan Detail") {
        fun createRoute(planId: Long) = "plan_detail/$planId"
    }
}

val bottomNavScreens = listOf(
    Screen.Dashboard,
    Screen.Workouts,
    Screen.Nutrition,
    Screen.Progress,
    Screen.Plans
)
