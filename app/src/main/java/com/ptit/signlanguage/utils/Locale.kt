package com.ptit.signlanguage.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext


enum class Language(val s : String){
    VI("vi"),
    EN("en")
}

object Locale {

    fun setLocale(context: Context, language: Language){
        val locale: java.util.Locale = java.util.Locale(language.s)
        java.util.Locale.setDefault(locale)

        val config: Configuration = Configuration()
        config.setLocale(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}