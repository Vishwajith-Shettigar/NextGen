package com.example.nextgen.privacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.profile.DISABLE_CHAT_ID
import com.example.domain.profile.DISABLE_LOCATION_ID
import com.example.domain.profile.DISABLE_PROFILE_PICTURE
import com.example.domain.profile.ProfileController
import com.example.model.PrivacyItem
import com.example.model.Profile
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.databinding.DialogYesNoOptionsBinding
import com.example.nextgen.databinding.FragmentPrivacyBinding
import com.example.nextgen.databinding.PrivacyItemsLayoutBinding
import com.example.nextgen.recyclerview.BaseAdapter
import com.example.utility.getProto
import com.example.utility.putProto
import kotlinx.coroutines.launch
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
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrivacyBinding.inflate(inflater, container, false)

        profile =
            arguments?.getProto(PRIVACY_FRAGMENT_ARGUMENTS_KEY, Profile.getDefaultInstance())!!

        privacyViewModel = PrivacyViewModel(
            profile,
            fragment,
            profileController,
            this
        )

        // Setup RecyclerView
        val layoutManager = LinearLayoutManager(activity.applicationContext)
        binding.privacyRecyclerView.apply {
            adapter = privacyAdapter
            this.layoutManager = layoutManager
        }
        binding.lifecycleOwner = this

        // Observe the list of PrivacyItemsViewModel
        privacyViewModel.privacyitemsList.observe(viewLifecycleOwner) { items ->
            privacyAdapter.itemList = items as MutableList<PrivacyItemsViewModel>
        }

        // RecyclerView bindings
        privacyAdapter.expressionGetViewType = { BaseAdapter.ViewType.PRIVACY_ITEM }
        privacyAdapter.expressionOnCreateViewHolder = { viewGroup, viewType ->
            when (viewType) {
                BaseAdapter.ViewType.PRIVACY_ITEM -> PrivacyItemsLayoutBinding.inflate(
                    LayoutInflater.from(viewGroup.context), viewGroup, false
                )

                else -> throw IllegalArgumentException("Unexpected view type: $viewType")
            }
        }

        privacyAdapter.expressionViewHolderBinding = { viewModel, _, viewBinding ->
            val itemBinding = viewBinding as PrivacyItemsLayoutBinding
            itemBinding.viewModel = viewModel
            itemBinding.lifecycleOwner = this

            // Observe the status change using Flow
            lifecycleScope.launch {
                viewModel.status.collect { newStatus ->
                    itemBinding.itemStatus.text = if (newStatus) "Yes" else "No"
                }
            }
        }
        return binding.root
    }

    override fun onPrivacyItemClicked(privacyItem: PrivacyItem, status: Boolean, index: Int) {
        showCustomDialog(privacyItem, status, index)
    }

    private fun showCustomDialog(privacyItem: PrivacyItem, status: Boolean, index: Int) {
        val dialogBinding = DialogYesNoOptionsBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(activity).apply { setView(dialogBinding.root) }
        var choice = status
        val alertDialog = builder.create()

        dialogBinding.apply {
            tvDialogTitle.text = privacyItem.itemName.toString()

            // Set initial state of radio buttons
            rbYes.isChecked = status
            rbNo.isChecked = !status

            rbYes.setOnClickListener {
                choice = true
                rbNo.isChecked = false // Deselect 'No' when 'Yes' is clicked
            }

            rbNo.setOnClickListener {
                choice = false
                rbYes.isChecked = false // Deselect 'Yes' when 'No' is clicked
            }

            btnSave.setOnClickListener {
                handleSave(privacyItem, choice, index)
                alertDialog.dismiss()
            }

            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }

    private fun handleSave(privacyItem: PrivacyItem, choice: Boolean, index: Int) {
        when (privacyItem.itemId) {
            DISABLE_CHAT_ID -> privacyViewModel.updateDisableChatStatus(choice) {
                if (it is com.example.utility.Result.Success) {
                    lifecycleScope.launch {
                        privacyAdapter.itemList[index].updateStatus(choice)
                    }
                }
            }

            DISABLE_LOCATION_ID -> privacyViewModel.updatedisableLocationStatus(choice) {
                if (it is com.example.utility.Result.Success) {
                    lifecycleScope.launch {
                        privacyAdapter.itemList[index].updateStatus(choice)
                    }
                }
            }

            DISABLE_PROFILE_PICTURE -> privacyViewModel.updatedisableProfilePicture(choice) {
                if (it is com.example.utility.Result.Success) {
                    lifecycleScope.launch {
                        privacyAdapter.itemList[index].updateStatus(choice)
                    }
                }
            }

            else -> {}
        }
    }

    companion object {
        private const val PRIVACY_FRAGMENT_ARGUMENTS_KEY = "PrivacyFragment.arguments"

        @JvmStatic
        fun newInstance(profile: Profile) = PrivacyFragment().apply {
            arguments = Bundle().apply {
                putProto(PRIVACY_FRAGMENT_ARGUMENTS_KEY, profile)
            }
        }
    }
}