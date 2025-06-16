package org.exthm.featuresettings.ui.settings

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.exthm.featuresettings.utils.SystemPropertiesHelper

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    data class AppInfo(val packageName: String,
                       val appName: String)

    companion object {
        private const val LOCKSCREEN_DIM_KEY = "persist.exthm.lockscreendim"
        private const val LAUNCHER_BLUR_KEY = "persist.exthm.launcherblur"
        private const val SCREEN_OCR_KEY = "persist.exthm.screenocr"
        private const val SCREEN_OCR_HIGH_KEY = "persist.exthm.screenocr_high"
        private const val DISABLE_SENSOR_KEY = "persist.exthm.disablesensor"
        private const val DISABLE_SENSOR_APPS_KEY = "persist.exthm.disablesensor.apps"
    }

    private val _lockscreenDimEnabled = MutableStateFlow(false)
    val lockscreenDimEnabled: StateFlow<Boolean> = _lockscreenDimEnabled

    private val _launcherBlurEnabled = MutableStateFlow(false)
    val launcherBlurEnabled: StateFlow<Boolean> = _launcherBlurEnabled

    private val _screenOcrEnabled = MutableStateFlow(false)
    val screenOcrEnabled: StateFlow<Boolean> = _screenOcrEnabled

    private val _screenOcrHighValue = MutableStateFlow(0f)
    val screenOcrHighValue: StateFlow<Float> = _screenOcrHighValue

    private val _disableSensorEnabled = MutableStateFlow(false)
    val disableSensorEnabled: StateFlow<Boolean> = _disableSensorEnabled

    private val _disableSensorApps = MutableStateFlow<Set<String>>(emptySet())
    val disableSensorApps: StateFlow<Set<String>> = _disableSensorApps

    private val _showAppSelectionDialog = MutableStateFlow(false)
    val showAppSelectionDialog: StateFlow<Boolean> = _showAppSelectionDialog

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps

    init {
        loadInitialSettings()
        loadInstalledApps()
    }

    private fun loadInitialSettings() {
        _lockscreenDimEnabled.value = SystemPropertiesHelper.getBoolean(LOCKSCREEN_DIM_KEY, false)
        _launcherBlurEnabled.value = SystemPropertiesHelper.getBoolean(LAUNCHER_BLUR_KEY, false)
        _screenOcrEnabled.value = SystemPropertiesHelper.getBoolean(SCREEN_OCR_KEY, false)
        _screenOcrHighValue.value = SystemPropertiesHelper.getInt(SCREEN_OCR_HIGH_KEY, 6).toFloat()

        _disableSensorEnabled.value = SystemPropertiesHelper.getBoolean(DISABLE_SENSOR_KEY, false)
        val appsString = SystemPropertiesHelper.get(DISABLE_SENSOR_APPS_KEY, "")
        _disableSensorApps.value = if (appsString.isNotBlank()) {
            appsString.split(',').toSet()
        } else {
            emptySet()
        }
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = getApplication<Application>().packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val resolveInfoList = pm.queryIntentActivities(mainIntent, 0)
            val apps = resolveInfoList
                .mapNotNull { it.activityInfo?.applicationInfo }
                .distinctBy { it.packageName }
                .map { appInfo ->
                    AppInfo(
                        packageName = appInfo.packageName,
                        appName = pm.getApplicationLabel(appInfo).toString()
                    )
                }
                .sortedBy { it.appName.lowercase() }

            withContext(Dispatchers.Main) {
                _installedApps.value = apps
            }
        }
    }

    fun onDisableSensorChanged(enabled: Boolean) {
        _disableSensorEnabled.value = enabled
        viewModelScope.launch {
            SystemPropertiesHelper.set(DISABLE_SENSOR_KEY, enabled.toString())
        }
    }

    fun onShowAppSelectionDialog() {
        _showAppSelectionDialog.value = true
    }

    fun onDismissAppSelectionDialog() {
        _showAppSelectionDialog.value = false
    }

    fun onAppSelectionConfirmed(selectedApps: Set<String>) {
        _disableSensorApps.value = selectedApps
        viewModelScope.launch {
            val appsString = selectedApps.joinToString(",")
            SystemPropertiesHelper.set(DISABLE_SENSOR_APPS_KEY, appsString)
        }
        onDismissAppSelectionDialog()
    }


    fun onLockscreenDimChanged(enabled: Boolean) {
        _lockscreenDimEnabled.value = enabled
        viewModelScope.launch {
            SystemPropertiesHelper.set(LOCKSCREEN_DIM_KEY, enabled.toString())
        }
    }

    fun onLauncherBlurChanged(enabled: Boolean) {
        _launcherBlurEnabled.value = enabled
        viewModelScope.launch {
            SystemPropertiesHelper.set(LAUNCHER_BLUR_KEY, enabled.toString())
        }
    }

    fun onScreenOcrChanged(enabled: Boolean) {
        _screenOcrEnabled.value = enabled
        viewModelScope.launch {
            SystemPropertiesHelper.set(SCREEN_OCR_KEY, enabled.toString())
        }
    }

    fun onScreenOcrHighChanged(value: Float) {
        _screenOcrHighValue.value = value
    }

    fun onScreenOcrHighChangeFinished(value: Float) {
        viewModelScope.launch {
            SystemPropertiesHelper.set(SCREEN_OCR_HIGH_KEY, value.toInt().toString())
        }
    }
}