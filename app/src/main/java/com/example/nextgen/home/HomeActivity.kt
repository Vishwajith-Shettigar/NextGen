package com.example.nextgen.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.domain.profile.ProfileController
import com.example.model.Chat
import com.example.model.Profile
import com.example.nextgen.Activity.ActivityComponent
import com.example.nextgen.Activity.BaseActivity
import com.example.nextgen.R
import com.example.nextgen.databinding.ActivityHomeBinding
import com.example.nextgen.editprofile.EditProfileActivity
import com.example.nextgen.editprofile.RouteToEditProfileActivity
import com.example.nextgen.message.MessageActivity
import com.example.nextgen.nearby.NearByFragment
import com.example.nextgen.notification.NotificationFragment
import com.example.nextgen.privacy.PrivacyActivity
import com.example.nextgen.privacy.RouteToPrivacyActivity
import com.example.nextgen.profile.ProfileFragment
import com.example.nextgen.service.LocationService
import com.example.nextgen.videocall.VideoCallActivity
import com.example.nextgen.viewprofile.RouteToViewProfile
import com.example.nextgen.viewprofile.ViewProfileActivity
import com.example.nextgen.webrtc.WebSocketManager
import com.example.videocallapp.MessageModel
import com.example.videocallapp.TYPE
import com.example.videocallapp.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import com.permissionx.guolindev.PermissionX
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

const val DEFAULT_LOCATION_REQUEST_CODE = 1
const val NEARBY_FRAGMENT_LOCATION_REQUEST_CODE = 2

class HomeActivity : BaseActivity(), ChatSummaryClickListener, RouteToEditProfileActivity,
  RouteToPrivacyActivity, RouteToViewProfile {
  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var firebaseFirestore: FirebaseFirestore

  lateinit var binding: ActivityHomeBinding

  lateinit var profile: Profile

  private val userId: String? by lazy {
    profileController.getUserId()
  }

  @Inject
  lateinit var webSocketManager: WebSocketManager

  @Inject
  lateinit var profileController: ProfileController
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
    checkLocationPermission(DEFAULT_LOCATION_REQUEST_CODE)
    CoroutineScope(Dispatchers.IO).launch {
      profile = profileController.getLocalUserProfile(profileController.getUserId()!!)
        ?: Profile.getDefaultInstance()
    }

    webSocketManager.initSocket(profileController.getUserId()!!)

    webSocketManager.message.observe(this) { messageModel ->
      if (messageModel?.type == null)
        return@observe
      else {
        when (messageModel.type) {
          TYPE.OFFER_RECIEVED -> {
            binding.callNotificationLayout.visibility = View.VISIBLE
            binding.username.text = "${messageModel.userName} is calling you"

            if (!messageModel.imageUrl.isNullOrBlank())
              Picasso.get().load(messageModel.imageUrl).into(binding.imageView)

            binding.acceptBtn.setOnClickListener {
              PermissionX.init(this)
                .permissions(
                  Manifest.permission.RECORD_AUDIO,
                  Manifest.permission.CAMERA
                ).request { allGranted, _, _ ->
                  if (allGranted) {

                    binding.callNotificationLayout.visibility = View.GONE
                    startActivity(
                      VideoCallActivity.createVideoCallActivity(
                        this,
                        messageModel.name!!,
                        messageModel.data.toString()!!,
                        UserRole.CALLEE,
                        profile.userId,
                        profile.imageUrl,
                        profile.userName
                      )
                    )
                  } else {
                    Toast.makeText(this, "you should accept all permissions", Toast.LENGTH_LONG)
                      .show()
                  }
                }
            }
            binding.rejectBtn.setOnClickListener {
              binding.callNotificationLayout.visibility = View.GONE
              // Todo :changes name into targetuserId
              val message = MessageModel(TYPE.CALL_ENDED, profile.userId, messageModel.name, null)
              webSocketManager.sendMessageToSocket(message)

            }
          }
          TYPE.CALL_ENDED -> {
            binding.callNotificationLayout.visibility = View.GONE
          }
          else -> {}
        }
      }
    }

// Start foreground service for location
    if (userId != null)
      ContextCompat.startForegroundService(this, Intent(this, LocationService::class.java))


    // Todo: Build call notification
    // Start foreground service for video call
    //   if (userId != null) {
    //        val intent = Intent(this, VideoCallService::class.java)
    //        intent.putExtra("USER_ID", userId)
    //        ContextCompat.startForegroundService(this, intent)
    //    }

    loadFragment(HomeFragment.newInstance(), HomeFragment.TAG)

    binding.bottomNavigation.setOnNavigationItemSelectedListener { menu ->
      when (menu.itemId) {
        R.id.home -> {
          loadFragment(HomeFragment.newInstance(), HomeFragment.TAG)
          true
        }
        R.id.nearby -> {
          if (isLocationPermissionGranted()) {
            loadFragment(NearByFragment.newInstance(profile), NearByFragment.TAG)
          } else {
            checkLocationPermission(NEARBY_FRAGMENT_LOCATION_REQUEST_CODE)
          }
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

  private fun isLocationPermissionGranted(): Boolean {
    return ActivityCompat.checkSelfPermission(
      this,
      Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
      ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
  }

  @Deprecated("Deprecated in Java")
  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      DEFAULT_LOCATION_REQUEST_CODE -> {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
        } else {
          Toast.makeText(
            this,
            "Please grant permission for a better experience.",
            Toast.LENGTH_SHORT
          ).show()
        }
      }
      NEARBY_FRAGMENT_LOCATION_REQUEST_CODE -> {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          loadFragment(NearByFragment.newInstance(profile), NearByFragment.TAG)
        } else {
          Toast.makeText(
            this,
            "Please grant permission to use nearby services.",
            Toast.LENGTH_SHORT
          ).show()
        }
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

  override fun onChatSummaryClicked(chat: Chat) {
    startActivity(MessageActivity.createMessageActivity(this, chat))
  }

  override fun routeToEditProfileActivity(profile: Profile) {
    startActivity(EditProfileActivity.createEditProfileActivity(this, profile))
  }

  override fun routeToPrivacyActivity(profile: Profile) {
    startActivity(PrivacyActivity.createPrivacyActivity(this, profile = profile))
  }

  override fun routeToViewProfile(profile: Profile) {
    startActivity(ViewProfileActivity.createViewProfileActivity(this, profile))
  }

  private fun checkLocationPermission(requestCode: Int) {
    if (ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED &&
      ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        requestCode
      )
    }
  }
}
