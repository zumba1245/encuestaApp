package com.example.encuestaapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.encuestaapp.ui.screens.admin.AdminNavigation
import com.example.encuestaapp.ui.screens.admin.CreateSurveyScreen
import com.example.encuestaapp.ui.screens.login.LoginScreen
import com.example.encuestaapp.ui.screens.login.LoginViewModel
import com.example.encuestaapp.ui.screens.register.RegisterScreen
import com.example.encuestaapp.ui.screens.register.RegisterViewModel
import com.example.encuestaapp.ui.screens.user.UserNavigation

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { role ->
                    val destination = if (role == "admin") "admin" else "user_main"
                    navController.navigate(destination) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            val registerViewModel: RegisterViewModel = viewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("admin") {
            AdminNavigation(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("admin") { inclusive = true }
                    }
                },
                onCreateSurvey = {
                    navController.navigate("create_survey")
                }
            )
        }
        composable("create_survey") {
            CreateSurveyScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("user_main") {
            UserNavigation(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("user_main") { inclusive = true }
                    }
                }
            )
        }
    }
}
