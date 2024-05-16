package com.example.nextgen.home

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.ActivityScope
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import com.example.nextgen.databinding.ActivityHomeBinding
import com.example.nextgen.databinding.FragmentHomeBinding
import javax.inject.Inject

@ActivityScope
class HomeActivity : BaseActivity() {
  @Inject
  lateinit var activity: AppCompatActivity

  lateinit var binding: ActivityHomeBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

    activity.supportFragmentManager.beginTransaction()
      .add(R.id.home_fragment_placeholder, HomeFragment.newInstance())
      .commitNow()
  }

  override fun injectDependencies(activityComponent: ActivityComponent) {
    activityComponent.inject(this)
  }
}
