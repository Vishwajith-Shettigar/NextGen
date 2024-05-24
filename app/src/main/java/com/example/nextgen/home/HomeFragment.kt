package com.example.nextgen.home

import android.Manifest
import android.app.Person
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.domain.nearby.NearByController
import com.example.domain.nearby.utility.GeoUtils
import com.example.domain.profile.ProfileController
import com.example.model.GeoPoint
import com.example.model.Profile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.Fragment.FragmentScope
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentHomeBinding
import com.example.nextgen.profile.ProfileFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import kotlin.random.Random

class HomeFragment : BaseFragment() {
  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var fragment: Fragment

  @Inject
  lateinit var nearByController: NearByController
  @Inject
  lateinit var profileController: ProfileController


  lateinit var binding: FragmentHomeBinding

  private lateinit var fusedLocationClient: FusedLocationProviderClient

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {

    // Inflate the layout for this fragment
    binding = FragmentHomeBinding.inflate(inflater,container,false)
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


storeUsers()

    return binding.root
  }


  // Store user data initially after signup
  // Todo: handle this in sign up page
  fun storeUsers(){
    // Check if the location permissions are granted, if not, request them
    if (ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED &&
      ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        requireActivity(),
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        1
      )
      return
    }

    fusedLocationClient.lastLocation
      .addOnSuccessListener { location: Location? ->
        location?.let {

          val profile = Profile.newBuilder().apply {
            this.userId = Random.nextInt(0, 100).toString()
            this.userName = Random.nextInt(0,2000).toString()+" vish "
            this.firstName = "vish"
            this.lastName = "shetigar"
            this.location = GeoPoint.newBuilder().apply {
              this.latitude =it.latitude
                this.longitude = it.longitude
            }.build()
          }.build()

          profileController.saveUsers(profile)
        }
      }
  }

  companion object {
    /** Returns instance of [HomeFragment] */
    fun newInstance(): HomeFragment {
      return HomeFragment()
    }
  }
}
