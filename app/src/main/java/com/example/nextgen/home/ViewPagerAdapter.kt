package com.example.nextgen.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nextgen.nearby.NearByFragment
import com.example.nextgen.notification.NotificationFragment
import com.example.nextgen.profile.ProfileFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
  private val fragmentList = listOf(
    HomeFragment.newInstance(),
    NearByFragment.newInstance(),
    NotificationFragment.newInstance(),
    ProfileFragment.newInstance()
  )

  override fun getItemCount(): Int {
    return fragmentList.size
  }

  override fun createFragment(position: Int): Fragment {
    return fragmentList[position]
  }
}
