package com.example.nextgen.home

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.ActivityScope
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import com.example.nextgen.databinding.ActivityHomeBinding
import com.example.nextgen.databinding.FragmentHomeBinding
import com.example.nextgen.nearby.NearByFragment
import com.example.nextgen.notification.NotificationFragment
import com.example.nextgen.profile.ProfileFragment
import javax.inject.Inject

class HomeActivity : BaseActivity() {
  @Inject
  lateinit var activity: AppCompatActivity

  lateinit var binding: ActivityHomeBinding

  lateinit var viewPagerAdapter: ViewPagerAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

    viewPagerAdapter = ViewPagerAdapter(this)
    binding.viewPager.adapter = viewPagerAdapter


    binding.bottomNavigation.setOnNavigationItemSelectedListener { menu ->
      when (menu.itemId) {
        R.id.home -> {
          binding.viewPager.currentItem = 0
          true
        }
        R.id.nearby -> {
          binding.viewPager.currentItem = 1
          true
        }
        R.id.notification -> {
          binding.viewPager.currentItem = 2
          true
        }
        R.id.profile -> {
          binding.viewPager.currentItem = 3
          true
        }
        else -> false
      }
    }

    binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        when (position) {
          0 -> binding.bottomNavigation.menu.findItem(R.id.home).isChecked = true
          1 -> binding.bottomNavigation.menu.findItem(R.id.nearby).isChecked = true
          2 -> binding.bottomNavigation.menu.findItem(R.id.notification).isChecked = true
          3 -> binding.bottomNavigation.menu.findItem(R.id.profile).isChecked = true
        }
      }
    })

  }

  override fun injectDependencies(activityComponent: ActivityComponent) {
    activityComponent.inject(this)
  }
}
