package org.exthm.featuresettings.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.exthm.featuresettings.utils.SystemPropertiesHelper

class SettingsViewModel : ViewModel() {

    companion object {
        private const val LOCKSCREEN_DIM_KEY = "persist.exthm.lockscreendim"
        private const val LAUNCHER_BLUR_KEY = "persist.exthm.launcherblur"
        private const val SCREEN_OCR_KEY = "persist.exthm.screenocr"
        private const val SCREEN_OCR_HIGH_KEY = "persist.exthm.screenocr_high"
    }

    private val _lockscreenDimEnabled = MutableStateFlow(false)
    val lockscreenDimEnabled: StateFlow<Boolean> = _lockscreenDimEnabled

    private val _launcherBlurEnabled = MutableStateFlow(false)
    val launcherBlurEnabled: StateFlow<Boolean> = _launcherBlurEnabled

    private val _screenOcrEnabled = MutableStateFlow(false)
    val screenOcrEnabled: StateFlow<Boolean> = _screenOcrEnabled

    private val _screenOcrHighValue = MutableStateFlow(0f)
    val screenOcrHighValue: StateFlow<Float> = _screenOcrHighValue

    init {
        loadInitialSettings()
    }

    private fun loadInitialSettings() {
        _lockscreenDimEnabled.value = SystemPropertiesHelper.getBoolean(LOCKSCREEN_DIM_KEY, false)
        _launcherBlurEnabled.value = SystemPropertiesHelper.getBoolean(LAUNCHER_BLUR_KEY, false)
        _screenOcrEnabled.value = SystemPropertiesHelper.getBoolean(SCREEN_OCR_KEY, false)
        _screenOcrHighValue.value = SystemPropertiesHelper.getInt(SCREEN_OCR_HIGH_KEY, 6).toFloat()
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