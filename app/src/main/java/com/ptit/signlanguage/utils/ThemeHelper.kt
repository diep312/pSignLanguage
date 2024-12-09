package com.ptit.signlanguage.utils

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.ptit.signlanguage.R
import com.ptit.signlanguage.data.prefs.PreferencesHelper

object ThemeHelper {
    private fun isSystemInDarkTheme(): Boolean {
        return Resources.getSystem().configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_YES
    }


    fun applyTheme(prefsHelper: PreferencesHelper) {
        val currentTheme = prefsHelper.getString(Constants.THEME) ?: Constants.LIGHT_THEME
        when (currentTheme) {
            Constants.DARK_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Constants.LIGHT_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    fun updateStatusBar(activity: Activity) {
        val currentTheme = activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val window = activity.window
        val decorView = window.decorView

        if (currentTheme == Configuration.UI_MODE_NIGHT_YES) {
            window.statusBarColor = ContextCompat.getColor(activity, R.color.color_dark_1) // or your dark status bar color
            decorView.systemUiVisibility = decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else {
            window.statusBarColor = ContextCompat.getColor(activity, R.color.color_light_1) // your light status bar color
            decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}