package org.exthm.featuresettings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import org.exthm.featuresettings.ui.settings.FeatureSettingsScreen
import org.exthm.featuresettings.ui.settings.SettingsViewModel
import org.exthm.featuresettings.ui.theme.FeatureSettingsTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeatureSettingsTheme {
                FeatureSettingsScreen(viewModel = viewModel)
            }
        }
    }
}