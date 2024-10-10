package com.example.nextgen.privacy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import com.example.model.Profile
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import com.example.utility.getProtoExtra
import com.example.utility.putProtoExtra

class PrivacyActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_privacy)

    val agreeButton = findViewById<Button>(R.id.accept_button)
    val readCheckBox = findViewById<CheckBox>(R.id.read_checkbox)

    agreeButton.isEnabled = false

    val profile =
      intent.getProtoExtra(PRIVACYACTIVITY_INTENT_EXTRAS_KEY, Profile.getDefaultInstance())!!
    supportFragmentManager.beginTransaction()
      .replace(R.id.frame_layout, PrivacyFragment.newInstance(profile))
      .commit()

    readCheckBox.setOnCheckedChangeListener { _, isChecked ->
      agreeButton.isEnabled = isChecked
    }
  }

  override fun injectDependencies(activityComponent: ActivityComponent) {
    activityComponent.inject(this)
  }

  companion object {

    /** Key for PrivacyActivity extras */
    val PRIVACYACTIVITY_INTENT_EXTRAS_KEY = "PrivacyActivity.extras"

    fun createPrivacyActivity(context: Context, profile: Profile): Intent {
      val intent = Intent(context, PrivacyActivity::class.java)
      intent.putProtoExtra(PRIVACYACTIVITY_INTENT_EXTRAS_KEY, profile)
      return intent
    }
  }
}