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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.chat.ChatController
import com.example.domain.constants.LOG_KEY
import com.example.domain.nearby.NearByController
import com.example.utility.GeoUtils
import com.example.domain.profile.ProfileController
import com.example.model.GeoPoint
import com.example.model.Profile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.Fragment.FragmentScope
import com.example.nextgen.R
import com.example.nextgen.databinding.ChatLayoutBinding
import com.example.nextgen.databinding.FragmentHomeBinding
import com.example.nextgen.profile.ProfileFragment
import com.example.nextgen.recyclerview.BaseAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import kotlin.random.Random

class HomeFragment : BaseFragment() {
  @Inject
  lateinit var fragment: Fragment

  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var nearByController: NearByController

  @Inject
  lateinit var profileController: ProfileController

  @Inject
  lateinit var chatController: ChatController


  lateinit var binding: FragmentHomeBinding

  private lateinit var fusedLocationClient: FusedLocationProviderClient

  private val userId by lazy {
    profileController.getUserId()
  }

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {

    // Inflate the layout for this fragment
    binding = FragmentHomeBinding.inflate(inflater, container, false)
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    val homeViewModel =
      HomeViewModel(chatController, userId!!, activity as ChatSummaryClickListener)
    val chatAdapter = BaseAdapter<HomeItemViewModel>()
    val chatLayoutManager = LinearLayoutManager(activity.applicationContext)
    binding.chatsRecyclerview.apply {
      adapter = chatAdapter
      layoutManager = chatLayoutManager
    }
    homeViewModel.chatList.observe(viewLifecycleOwner) {
      chatAdapter.itemList = it as MutableList<HomeItemViewModel>
    }

    chatAdapter.expressionGetViewType = { homeItemViewModel ->
      when (homeItemViewModel) {
        is ChatViewModel -> {
          BaseAdapter.ViewType.CHAT
        }
        else -> {
          throw IllegalArgumentException("Encountered unexpected view model: $homeViewModel")
        }
      }

    }
    chatAdapter.expressionOnCreateViewHolder = { viewGroup, viewType ->
      when (viewType) {
        BaseAdapter.ViewType.CHAT -> {
          ChatLayoutBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }
        else -> {
          throw IllegalArgumentException("Encountered unexpected view type: $viewType")
        }
      }
    }

    chatAdapter.expressionViewHolderBinding = { viewModel, viewBinding ->

      val itemBinding = viewBinding as ChatLayoutBinding
      itemBinding.viewModel = viewModel as ChatViewModel?
    }

    return binding.root
  }


  // Store user data initially after signup
  // Todo: handle this in sign up page
//  fun storeUsers() {
//    // Check if the location permissions are granted, if not, request them
//    if (ActivityCompat.checkSelfPermission(
//        requireContext(),
//        Manifest.permission.ACCESS_FINE_LOCATION
//      ) != PackageManager.PERMISSION_GRANTED &&
//      ActivityCompat.checkSelfPermission(
//        requireContext(),
//        Manifest.permission.ACCESS_COARSE_LOCATION
//      ) != PackageManager.PERMISSION_GRANTED
//    ) {
//      ActivityCompat.requestPermissions(
//        requireActivity(),
//        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//        1
//      )
//      return
//    }
//
//    fusedLocationClient.lastLocation
//      .addOnSuccessListener { location: Location? ->
//        location?.let {
//
//          val profile = Profile.newBuilder().apply {
//            this.userId = Random.nextInt(0, 100).toString()
//            this.userName = Random.nextInt(0, 2000).toString() + " vish "
//            this.firstName = "vish"
//            this.lastName = "shetigar"
//            this.location = GeoPoint.newBuilder().apply {
//              this.latitude = it.latitude
//              this.longitude = it.longitude
//            }.build()
//          }.build()
//
//          profileController.saveUsers(profile)
//        }
//      }
//  }

  companion object {

    const val TAG = "HomeFragment"

    /** Returns instance of [HomeFragment] */
    fun newInstance(): HomeFragment {
      return HomeFragment()
    }
  }
}
