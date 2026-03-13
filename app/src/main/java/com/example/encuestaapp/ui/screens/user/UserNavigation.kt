package com.example.encuestaapp.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.encuestaapp.R
import com.example.encuestaapp.data.preferences.ThemePreferences
import com.example.encuestaapp.ui.screens.admin.AdminProfileScreen
import com.example.encuestaapp.ui.screens.admin.AdminProfileViewModel
import com.example.encuestaapp.ui.screens.admin.AdminProfileViewModelFactory

sealed class UserTab(val route: String, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : UserTab("user_home", R.string.nav_home, Icons.Default.Home)
    object History : UserTab("user_history", R.string.nav_tickets, Icons.AutoMirrored.Filled.List)
    object Profile : UserTab("user_profile", R.string.nav_profile, Icons.Default.Person)
}

@Composable
fun UserNavigation(
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf<UserTab>(UserTab.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val tabs = listOf(UserTab.Home, UserTab.History, UserTab.Profile)
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(stringResource(tab.labelRes)) },
                        icon = { Icon(tab.icon, contentDescription = null) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                UserTab.Home -> UserHomeScreen() 
                UserTab.History -> {
                    val historyViewModel: UserHistoryViewModel = viewModel()
                    UserHistoryScreen(viewModel = historyViewModel)
                }
                UserTab.Profile -> {
                    val context = LocalContext.current
                    val preferences = remember { ThemePreferences(context.applicationContext) }
                    val profileViewModel: AdminProfileViewModel = viewModel(
                        factory = AdminProfileViewModelFactory(preferences)
                    )
                    AdminProfileScreen(
                        viewModel = profileViewModel, 
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}
