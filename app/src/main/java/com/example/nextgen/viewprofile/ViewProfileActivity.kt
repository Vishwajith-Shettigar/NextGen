package com.example.nextgen.viewprofile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.model.Profile
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import com.example.utility.getProtoExtra
import com.example.utility.putProtoExtra

class ViewProfileActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_view_profile)
    val profile =
      intent.getProtoExtra(VIEWPROFILEACTIVITY_INTENT_EXTRAS_KEY, Profile.getDefaultInstance())
    supportFragmentManager.beginTransaction()
      .replace(R.id.frame_layout, ViewProfileFragment.newInstance(profile!!))
      .commit()
  }

  override fun injectDependencies(activityComponent: ActivityComponent) {
    activityComponent.inject(this)
  }

  companion object {

    /** Key for ViewProfileActivity extras. */
    val VIEWPROFILEACTIVITY_INTENT_EXTRAS_KEY = "ViewProfileActivity.extras"

    fun createViewProfileActivity(context: Context, profile: Profile): Intent {
      val intent = Intent(context, ViewProfileActivity::class.java)
      intent.putProtoExtra(VIEWPROFILEACTIVITY_INTENT_EXTRAS_KEY, profile)
      return intent
    }
  }
}
