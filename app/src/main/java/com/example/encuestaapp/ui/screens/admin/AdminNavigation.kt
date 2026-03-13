package com.example.encuestaapp.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.encuestaapp.R
import com.example.encuestaapp.data.preferences.ThemePreferences

sealed class AdminScreen(val route: String, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : AdminScreen("admin_home", R.string.nav_home, Icons.Default.Home)
    object Profile : AdminScreen("admin_profile", R.string.nav_profile, Icons.Default.Person)
}

@Composable
fun AdminNavigation(
    onLogout: () -> Unit,
    onCreateSurvey: () -> Unit = {}
) {
    var selectedScreen by remember { mutableStateOf<AdminScreen>(AdminScreen.Home) }

    BoxWithConstraints {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            bottomBar = {
                if (!useRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        AdminBottomNavigationItems(
                            selectedScreen = selectedScreen,
                            onScreenSelected = { selectedScreen = it }
                        )
                    }
                }
            }
        ) { padding ->
            Row(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
                if (useRail) {
                    NavigationRail(
                        modifier = Modifier.fillMaxHeight(),
                        containerColor = MaterialTheme.colorScheme.surface,
                    ) {
                        AdminRailNavigationItems(
                            selectedScreen = selectedScreen,
                            onScreenSelected = { selectedScreen = it }
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (selectedScreen) {
                        AdminScreen.Home -> AdminHomeScreen(
                            onCreateSurveyClick = onCreateSurvey
                        )
                        AdminScreen.Profile -> {
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
    }
}

@Composable
fun RowScope.AdminBottomNavigationItems(
    selectedScreen: AdminScreen,
    onScreenSelected: (AdminScreen) -> Unit
) {
    val items = listOf(AdminScreen.Home, AdminScreen.Profile)
    items.forEach { screen ->
        NavigationBarItem(
            icon = { Icon(screen.icon, contentDescription = null) },
            label = { Text(stringResource(screen.labelRes)) },
            selected = selectedScreen == screen,
            onClick = { onScreenSelected(screen) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
fun AdminRailNavigationItems(
    selectedScreen: AdminScreen,
    onScreenSelected: (AdminScreen) -> Unit
) {
    val items = listOf(AdminScreen.Home, AdminScreen.Profile)
    items.forEach { screen ->
        NavigationRailItem(
            icon = { Icon(screen.icon, contentDescription = null) },
            label = { Text(stringResource(screen.labelRes)) },
            selected = selectedScreen == screen,
            onClick = { onScreenSelected(screen) }
        )
    }
}

@Composable
fun AdminProfileScreen(
    viewModel: AdminProfileViewModel,
    onLogout: () -> Unit
) {
    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header de Perfil
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = state.email,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = state.role.uppercase(),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sección de Ajustes
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.settings_theme),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                ListItem(
                    headlineContent = { Text(stringResource(R.string.dynamic_color), fontWeight = FontWeight.Medium) },
                    leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                    trailingContent = {
                        Switch(
                            checked = state.dynamicColorEnabled,
                            onCheckedChange = { viewModel.toggleDynamicColor(it) }
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer, 
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.logout), fontWeight = FontWeight.Bold)
        }
    }
}
