package com.personal.accountantAssistant.bases.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.personal.accountantAssistant.bases.BaseFragment

class ViewPagerAdapter(
    frag: FragmentActivity, var fragments: List<BaseFragment<*>>
) : FragmentStateAdapter(frag) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}