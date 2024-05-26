package com.example.nextgen.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.domain.post.PostController
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import com.example.nextgen.databinding.ActivityHomeBinding
import com.example.nextgen.nearby.NearByFragment
import com.example.nextgen.notification.NotificationFragment
import com.example.nextgen.profile.ProfileFragment
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

    loadFragment(HomeFragment.newInstance(), HomeFragment.TAG)

    binding.bottomNavigation.setOnNavigationItemSelectedListener { menu ->
      when (menu.itemId) {
        R.id.home -> {
          loadFragment(HomeFragment.newInstance(), HomeFragment.TAG)
          true
        }
        R.id.nearby -> {
          loadFragment(NearByFragment.newInstance(), NearByFragment.TAG)
          true
        }
        R.id.notification -> {
          loadFragment(NotificationFragment.newInstance(), NotificationFragment.TAG)
          true
        }
        R.id.profile -> {
          loadFragment(ProfileFragment.newInstance(), ProfileFragment.TAG)
          true
        }
        else -> false
      }
    }

  }


  private fun loadFragment(fragment: Fragment, tag: String) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()

    val currentFragment = supportFragmentManager.primaryNavigationFragment
    if (currentFragment != null) {
      fragmentTransaction.hide(currentFragment)
    }

    var fragmentTemp = supportFragmentManager.findFragmentByTag(tag)
    if (fragmentTemp == null) {
      fragmentTemp = fragment
      fragmentTransaction.add(R.id.frame_layout, fragmentTemp, tag)
    } else {
      fragmentTransaction.show(fragmentTemp)
    }

    fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
    fragmentTransaction.setReorderingAllowed(true)
    fragmentTransaction.commitNowAllowingStateLoss()
  }

  override fun injectDependencies(activityComponent: ActivityComponent) {
    activityComponent.inject(this)
  }

  companion object {
    fun createHomeActivity(context: Context): Intent {
      return Intent(context, HomeActivity::class.java)
    }
  }
}
