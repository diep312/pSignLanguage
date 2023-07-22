package com.ptit.signlanguage.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ptit.signlanguage.ui.main.fragment.AccountFragment
import com.ptit.signlanguage.ui.main.fragment.CourseFragment
import com.ptit.signlanguage.ui.main.fragment.TextToVideoFragment
import com.ptit.signlanguage.ui.main.fragment.VideoToTextFragment
import com.ptit.signlanguage.utils.Constants.PAGE_0
import com.ptit.signlanguage.utils.Constants.PAGE_1
import com.ptit.signlanguage.utils.Constants.PAGE_2
import com.ptit.signlanguage.utils.Constants.PAGE_3

class MainViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    var listPage: MutableList<Fragment> = mutableListOf()

    init {
        listPage.clear()

        listPage.add(VideoToTextFragment())
        listPage.add(TextToVideoFragment())
        listPage.add(CourseFragment())
        listPage.add(AccountFragment())
    }

    override fun getItemCount(): Int {
        return listPage.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            PAGE_0 -> listPage[PAGE_0]
            PAGE_1 -> listPage[PAGE_1]
            PAGE_2 -> listPage[PAGE_2]
            PAGE_3 -> listPage[PAGE_3]
            else -> listPage[PAGE_0]
        }
    }
}