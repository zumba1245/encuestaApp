package com.example.encuestaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.encuestaapp.data.preferences.ThemePreferences
import com.example.encuestaapp.ui.navigation.AppNavigation
import com.example.encuestaapp.ui.theme.EncuestaAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val preferences = remember { ThemePreferences(context.applicationContext) }
            
            val dynamicColorEnabled by preferences.dynamicColorEnabled.collectAsState(initial = false)
            val darkModePreference by preferences.darkModeEnabled.collectAsState(initial = null)
            
            val useDarkTheme = when (darkModePreference) {
                true -> true
                false -> false
                null -> isSystemInDarkTheme()
            }

            EncuestaAPPTheme(
                darkTheme = useDarkTheme,
                dynamicColor = dynamicColorEnabled
            ) {
                AppNavigation()
            }
        }
    }
}
