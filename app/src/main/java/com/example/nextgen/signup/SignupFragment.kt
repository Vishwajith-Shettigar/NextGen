package com.example.nextgen.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.domain.registration.SignUpLogInController
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.databinding.FragmentSignupBinding
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class SignupFragment : BaseFragment(), RouteToSignupSigninListener {

  @Inject
  lateinit var fragment: Fragment

  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var signUpLogInController: SignUpLogInController

  private lateinit var binding: FragmentSignupBinding
  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    // Inflate the layout for this fragment
    binding = FragmentSignupBinding.inflate(inflater, container, false)

    val signupViewModel =
      SignupViewModel(fragment as RouteToSignupSigninListener, signUpLogInController)

    binding.let {
      it.lifecycleOwner = fragment
      it.viewModel = signupViewModel
    }

    binding.signupbtn.setOnClickListener {
      binding.progressBar.visibility = View.VISIBLE
      signupViewModel.onClickSignUp(
        activity,
        binding.username.text.toString(),
        binding.email.text.toString(),
        binding.password.text.toString()
      )
    }
    signupViewModel.registrationResult.observe(viewLifecycleOwner) {
      when (it) {
        is com.example.utility.Result.Success<*> -> {
          binding.progressBar.visibility = View.GONE
          (activity as RouteToHomeActivity).routeToHome()
        }

        is com.example.utility.Result.Failure -> {
          binding.progressBar.visibility = View.GONE
          Toast.makeText(activity, "Please enter correct details.", Toast.LENGTH_LONG).show()
        }

        else -> Unit
      }
    }
    return binding.root
  }

  companion object {
    val tag: String = "SignUpFragment"

    fun newInstance(): SignupFragment {
      return SignupFragment()
    }
  }

  override fun routeToSignupOrSignin() {
    (activity as RouteToSignupSigninActivityListener).routeToSignupSigninActivity(
      SignInFragment.newInstance(),
      SignInFragment.tag
    )
  }
}
