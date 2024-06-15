package com.example.nextgen.nearby

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.constants.LOG_KEY
import com.example.domain.constants.NEARBY_DISTANCE
import com.example.domain.nearby.NearByController
import com.example.nextgen.viewmodel.ObservableViewModel
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng

class NearByViewModel(
  private val userId:String,
  private val lifecycleOwner: LifecycleOwner,
  nearByController: NearByController,
  updateMapListener: UpdateMapListener,
) : ObservableViewModel() {

  var location: MutableLiveData<LatLng> = MutableLiveData()

  init {

    location.observe(lifecycleOwner) {
      Log.e(LOG_KEY, it.toString() + "###")
      nearByController.listenToNearbyUsers(userId,GeoLocation(it.latitude, it.longitude), NEARBY_DISTANCE.toDouble()) {profile,outOfBound->
        Log.e(LOG_KEY,  "###"+ profile.userId)
        updateMapListener.updateMap(profile,outOfBound)
      }
    }
  }

}