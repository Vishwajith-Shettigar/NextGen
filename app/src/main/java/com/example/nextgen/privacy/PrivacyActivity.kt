package com.example.nextgen.privacy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.model.Profile
import com.example.nextgen.R
import com.example.nextgen.message.MessageActivity
import com.example.utility.getProtoExtra
import com.example.utility.putProtoExtra

class PrivacyActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_privacy)
    val profile =
      intent.getProtoExtra(PRIVACYACTIVITY_INTENT_EXTRAS_KEY, Profile.getDefaultInstance())!!
    supportFragmentManager.beginTransaction()
      .replace(R.id.frame_layout, PrivacyFragment.newInstance(profile))
      .commit()
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