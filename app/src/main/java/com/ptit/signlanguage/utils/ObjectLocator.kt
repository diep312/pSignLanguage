package com.ptit.signlanguage.utils

import com.ptit.signlanguage.network.model.response.Course
import com.ptit.signlanguage.network.model.response.Label

object ObjectLocator {
    val recentCourses = mutableListOf<Int>()
    val savedLabels = mutableSetOf<String>()
}