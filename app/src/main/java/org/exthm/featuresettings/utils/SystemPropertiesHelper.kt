package org.exthm.featuresettings.utils

import android.os.SystemProperties

object SystemPropertiesHelper {

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return SystemProperties.getBoolean(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return SystemProperties.getInt(key, defaultValue)
    }

    fun set(key: String, value: String) {
        try {
            SystemProperties.set(key, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}