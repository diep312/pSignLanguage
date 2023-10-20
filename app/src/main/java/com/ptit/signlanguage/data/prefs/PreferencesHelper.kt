package com.ptit.signlanguage.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PreferencesHelper(context: Context) : IPreferencesHelper {
    private var mPrefs: SharedPreferences? = null

    init {
        this.mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object {
        private const val DEFAULT_VALUE_LONG: Long = -1L
        const val DEFAULT_VALUE_INTEGER: Int = -1
        private const val DEFAULT_VALUE_FLOAT: Float = -1F
    }

    override fun save(key: String?, value: Boolean) {
        mPrefs!!.edit().putBoolean(key, value).apply()
    }

    override fun save(key: String?, value: String?) {
        mPrefs!!.edit().putString(key, value).apply()
    }

    override fun save(key: String?, value: Float) {
        mPrefs!!.edit().putFloat(key, value).apply()
    }

    override fun save(key: String?, value: Int) {
        mPrefs!!.edit().putInt(key, value).apply()
    }

    override fun save(key: String?, value: Long) {
        mPrefs!!.edit().putLong(key, value).apply()
    }

    override fun getBoolean(key: String?): Boolean {
        return mPrefs!!.getBoolean(key, false)
    }

    override fun getString(key: String?): String? {
        return mPrefs!!.getString(key, null)
    }

    override fun getLong(key: String?): Long {
        return mPrefs!!.getLong(key, DEFAULT_VALUE_LONG)
    }

    override fun getInt(key: String?): Int {
        return mPrefs!!.getInt(key, DEFAULT_VALUE_INTEGER)
    }

    override fun getFloat(key: String?): Float {
        return mPrefs!!.getFloat(key, DEFAULT_VALUE_FLOAT)
    }

    override fun remove(key: String?) {
        mPrefs!!.edit().remove(key).apply()
    }

}