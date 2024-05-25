package com.example.nextgen.signup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.domain.constants.LOG_KEY
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R

class SignupSigninActivity : BaseActivity(),RouteToSignupSigninActivityListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
      loadFragment(SignupFragment.newInstance(),SignupFragment.tag)
    }

  override fun injectDependencies(activityComponent: ActivityComponent) {
    activityComponent.inject(this)
  }

  override fun routeToSignupSigninActivity(fragment: Fragment,tag: String) {
    loadFragment(fragment,tag)
  }

  private fun loadFragment(fragment: Fragment, tag: String) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()

    // Set custom animations
    fragmentTransaction.setCustomAnimations(
      R.anim.enter_from_right,
      R.anim.exit_to_left
    )
    val currentFragment = supportFragmentManager.primaryNavigationFragment
    if (currentFragment != null) {
      fragmentTransaction.hide(currentFragment)
    }

    var fragmentTemp = supportFragmentManager.findFragmentByTag(tag)
    if (fragmentTemp == null) {
      fragmentTemp = fragment
      fragmentTransaction.add(R.id.frame_layout_signActivity, fragmentTemp, tag)
    } else {
      fragmentTransaction.show(fragmentTemp)
    }

    fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
    fragmentTransaction.setReorderingAllowed(true)
    fragmentTransaction.commitNowAllowingStateLoss()
  }

}