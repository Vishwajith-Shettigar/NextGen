package com.example.nextgen.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.domain.post.PostController
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import com.example.nextgen.databinding.ActivityHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class HomeActivity : BaseActivity() {
  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var firebaseFirestore: FirebaseFirestore

  lateinit var binding: ActivityHomeBinding

  lateinit var viewPagerAdapter: ViewPagerAdapter

  @Inject
  lateinit var postController: PostController

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
