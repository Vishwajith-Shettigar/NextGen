package com.example.nextgen.privacy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.constants.LOG_KEY
import com.example.domain.profile.ProfileController
import com.example.model.PrivacyItem
import com.example.model.Profile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.ChatLayoutBinding
import com.example.nextgen.databinding.FragmentPrivacyBinding
import com.example.nextgen.databinding.PrivacyItemsLayoutBinding
import com.example.nextgen.home.ChatViewModel
import com.example.nextgen.recyclerview.BaseAdapter
import com.example.utility.getProto
import com.example.utility.putProto
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrivacyFragment : BaseFragment() {

  lateinit var binding: FragmentPrivacyBinding

  lateinit var profile: Profile

  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var fragment: Fragment

  @Inject
  lateinit var profileController: ProfileController

  val privacyAdapter = BaseAdapter<PrivacyItemsViewModel>()

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    // Inflate the layout for this fragment
    binding = FragmentPrivacyBinding.inflate(inflater, container, false)
    profile = arguments?.getProto(PRIVACY_FRAGMENT_ARGUMENTS_KEY, Profile.getDefaultInstance())!!
    val privacyViewModel = PrivacyViewModel(profile, fragment, profileController)
    val layoutManager = LinearLayoutManager(activity.applicationContext)
    binding.privacyRecyclerView.apply {
      this.adapter = privacyAdapter
      this.layoutManager = layoutManager
    }

    privacyViewModel.privacyitemsList.observe(viewLifecycleOwner) {
      privacyAdapter.itemList = it as MutableList<PrivacyItemsViewModel>
    }

    privacyAdapter.expressionGetViewType = { homeItemViewModel ->

      BaseAdapter.ViewType.PRIVACY_ITEM

    }
    privacyAdapter.expressionOnCreateViewHolder = { viewGroup, viewType ->
      when (viewType) {
        BaseAdapter.ViewType.PRIVACY_ITEM -> {
          PrivacyItemsLayoutBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
          )
        }
        else -> {
          throw IllegalArgumentException("Encountered unexpected view type: $viewType")
        }
      }
    }

    privacyAdapter.expressionViewHolderBinding = { viewModel, viewtype, viewBinding ->

      val itemBinding = viewBinding as PrivacyItemsLayoutBinding
      itemBinding.viewModel = viewModel as PrivacyItemsViewModel?
    }

    return binding.root
  }

  companion object {

    private val PRIVACY_FRAGMENT_ARGUMENTS_KEY = "PrivacyFragment.arguments"

    @JvmStatic
    fun newInstance(profile: Profile) =
      PrivacyFragment().apply {
        arguments = Bundle().apply {
          putProto(PRIVACY_FRAGMENT_ARGUMENTS_KEY, profile)
        }
      }
  }
}
