package com.ptit.signlanguage.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GsonUtils {
    companion object {
        private var gson: Gson? = null

        @Synchronized
        fun getInstance(): Gson? {
            if (gson == null) {
                synchronized(GsonUtils::class.java) { if (gson == null) gson = Gson() }
            }
            return gson
        }

        /**
         * Serialize an object to Json.
         *
         * @param object to serialize.
         */
        fun serialize(`object`: Any?, clazz: Class<*>?): String? {
            return getInstance()!!.toJson(`object`, clazz)
        }

        /**
         * Deserialize a json representation of an object.
         *
         * @param string A json string to deserialize.
         */
        fun <T> deserialize(string: String?, clazz: Class<T>?): T? {
            return getInstance()!!.fromJson(string, clazz)
        }

        fun serializeList(objects: List<Any?>?, clazz: Class<*>?): String? {
            return getInstance()!!.toJson(objects, clazz)
        }

        fun <T> deserializeList(string: String?, clazz: Class<T>?): List<T>? {
            val type = object : TypeToken<T>() {}.type
            return getInstance()!!.fromJson(string, type)
        }
    }

}