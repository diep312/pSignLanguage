package com.ptit.signlanguage.data.prefs

interface IPreferencesHelper {
    fun save(key: String?, value: Boolean)

    fun save(key: String?, value: String?)

    fun save(key: String?, value: Float)

    fun save(key: String?, value: Int)

    fun save(key: String?, value: Long)

    fun getBoolean(key: String?): Boolean

    fun getString(key: String?): String?

    fun getLong(key: String?): Long

    fun getInt(key: String?): Int

    fun getFloat(key: String?): Float

    fun remove(key: String?)
}