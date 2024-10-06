package com.example.nextgen.editprofile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.model.Profile
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import com.example.utility.getProtoExtra
import com.example.utility.putProtoExtra

class EditProfileActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit_profile)
    val profile = intent.getProtoExtra(EDIT_PROFILE_ACTIVITY_KEY, Profile.getDefaultInstance())
    supportFragmentManager.beginTransaction()
      .replace(R.id.frame_layout, EditProfileFragment.newInstance(profile = profile!!))
      .commit()
  }

  override fun injectDependencies(activityComponent: ActivityComponent) {
    activityComponent.inject(this)
  }

  companion object {
    private val EDIT_PROFILE_ACTIVITY_KEY = "EditProfileActivity.key"

    fun createEditProfileActivity(context: Context, profile: Profile): Intent {
      val intent = Intent(context, EditProfileActivity::class.java)
      intent.putProtoExtra(EDIT_PROFILE_ACTIVITY_KEY, profile)
      return intent
    }
  }
}