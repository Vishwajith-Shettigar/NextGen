package com.example.nextgen.privacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.profile.DISABLE_CHAT_ID
import com.example.domain.profile.DISABLE_LOCATION_ID
import com.example.domain.profile.DISABLE_PROFILE_PICTURE
import com.example.domain.profile.ProfileController
import com.example.model.PrivacyItem
import com.example.model.Profile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.DialogYesNoOptionsBinding
import com.example.nextgen.databinding.FragmentPrivacyBinding
import com.example.nextgen.databinding.PrivacyItemsLayoutBinding
import com.example.nextgen.recyclerview.BaseAdapter
import com.example.utility.getProto
import com.example.utility.putProto
import javax.inject.Inject

class PrivacyFragment : BaseFragment(), OnPrivacyItemClicked {

  lateinit var binding: FragmentPrivacyBinding

  lateinit var profile: Profile

  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var fragment: Fragment

  @Inject
  lateinit var profileController: ProfileController

  private val privacyAdapter = BaseAdapter<PrivacyItemsViewModel>()

  private lateinit var privacyViewModel: PrivacyViewModel

  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    // Inflate the layout for this fragment
    binding = FragmentPrivacyBinding.inflate(inflater, container, false)
    profile = arguments?.getProto(PRIVACY_FRAGMENT_ARGUMENTS_KEY, Profile.getDefaultInstance())!!
    privacyViewModel =
      PrivacyViewModel(profile, fragment, profileController, (fragment as OnPrivacyItemClicked))
    val layoutManager = LinearLayoutManager(activity.applicationContext)
    binding.privacyRecyclerView.apply {
      this.adapter = privacyAdapter
      this.layoutManager = layoutManager
    }

    binding.lifecycleOwner = this

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
      itemBinding.viewModel = viewModel
      itemBinding.lifecycleOwner = this

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

  override fun onPrivacyItemClicked(privacyItem: PrivacyItem, status: Boolean, index: Int) {
    showCustomDialog(privacyItem, status, index)
  }

  private fun showCustomDialog(privacyItem: PrivacyItem, status: Boolean, index: Int) {
    // Inflate the dialog layout
    val dialogbinding = DialogYesNoOptionsBinding.inflate(layoutInflater)

    // Build the AlertDialog
    val builder = AlertDialog.Builder(activity)
    builder.setView(dialogbinding.root)

    var choice = status

    // Create the AlertDialog
    val alertDialog = builder.create()

    alertDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)

    if (status == true)
      dialogbinding.rbYes.isChecked = true
    else
      dialogbinding.rbNo.isChecked = true

    dialogbinding.tvDialogTitle.text = privacyItem.itemName.toString()

    // Set button click listeners
    dialogbinding.rbYes.setOnClickListener {
      // Do something when Yes is clicked
      dialogbinding.rbNo.isChecked = false
      choice = true

    }

    dialogbinding.rbNo.setOnClickListener {
      // Do something when No is clicked
      dialogbinding.rbYes.isChecked = false
      choice = false

    }

    dialogbinding.btnSave.setOnClickListener {
      // Do something when Save is clicked
      handleSave(privacyItem, choice, index)
      alertDialog.dismiss()
    }

    dialogbinding.btnCancel.setOnClickListener {
      // Do something when Cancel is clicked
      alertDialog.dismiss()
    }

    // Display the dialog
    alertDialog.show()
  }

  fun handleSave(privacyItem: PrivacyItem, choice: Boolean, index: Int) {

    if (privacyItem.itemId == DISABLE_CHAT_ID) {
      privacyViewModel.updateDisableChatStatus(choice) {
        if (it is com.example.utility.Result.Success) {
          privacyAdapter.itemList[index].status.set(choice)
        }
      }
    } else if (privacyItem.itemId == DISABLE_LOCATION_ID) {
      privacyViewModel.updatedisableLocationStatus(choice) {
        if (it is com.example.utility.Result.Success) {
          privacyAdapter.itemList[index].status.set(choice)
        }
      }
    } else if (privacyItem.itemId == DISABLE_PROFILE_PICTURE) {
      privacyViewModel.updatedisableProfilePicture(choice) {
        if (it is com.example.utility.Result.Success) {
          privacyAdapter.itemList[index].status.set(choice)
        }
      }
    } else {
    }
  }
}
