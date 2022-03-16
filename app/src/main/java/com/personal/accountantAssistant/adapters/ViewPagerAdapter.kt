package com.personal.accountantAssistant.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter : FragmentStateAdapter {

    var fragments: List<Fragment> = ArrayList()

    constructor(frag: Fragment, fragments: List<Fragment>) : super(frag) {
        this.fragments = fragments
    }

    constructor(frag: FragmentActivity, fragments: List<Fragment>) : super(frag) {
        this.fragments = fragments
    }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}