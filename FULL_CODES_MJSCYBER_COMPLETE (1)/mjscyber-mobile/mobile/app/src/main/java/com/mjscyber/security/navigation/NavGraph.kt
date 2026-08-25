package com.mjscyber.security.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mjscyber.security.ui.screens.*

/**
 * NavGraph – Navigation for MJSCYBER mobile app
 * Routes match website sitemap + mobile sitemap (Creately inspired)
 */
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    Log.d("NavGraph", "Starting NavGraph – isLoggedIn check via AuthRepository")

    NavHost(
        navController = navController,
        startDestination = "login" // Will redirect if logged in – handled in LoginScreen
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { role ->
                    Log.d("NavGraph", "Login success role=$role – navigating")
                    if (role == "admin") navController.navigate("admin") {
                        popUpTo("login") { inclusive = true }
                    } else navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { role ->
                    if (role == "admin") navController.navigate("admin") {
                        popUpTo("login") { inclusive = true }
                    } else navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToCourses = { navController.navigate("courses") },
                onNavigateToCertificates = { navController.navigate("certificates") },
                onNavigateToVerify = { navController.navigate("verify") },
                onNavigateToVip = { navController.navigate("vip") },
                onNavigateToSite = { navController.navigate("site") },
                onNavigateToProfile = { navController.navigate("profile") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("courses") {
            CourseListScreen(
                onCourseClick = { courseId ->
                    navController.navigate("course_detail/$courseId")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            "course_detail/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            CourseDetailScreen(
                courseId = courseId,
                onBack = { navController.popBackStack() },
                onEnrolled = { navController.navigate("home") }
            )
        }
        composable("certificates") {
            CertificateScreen(
                onBack = { navController.popBackStack() },
                onVerifyClick = { navController.navigate("verify") }
            )
        }
        composable("verify") {
            VerifyScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("vip") {
            VipRequestScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
        composable("site") {
            SiteRequestScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileSettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("admin") {
            AdminDashboardScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
