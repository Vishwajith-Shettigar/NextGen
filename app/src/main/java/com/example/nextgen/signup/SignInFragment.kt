package com.example.nextgen.signup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.constants.LOG_KEY
import com.example.domain.registration.SignUpLogInController
import com.example.nextgen.Fragment.BaseFragment
import com.example.nextgen.Fragment.FragmentComponent
import com.example.nextgen.R
import com.example.nextgen.databinding.FragmentSignInBinding
import com.example.nextgen.databinding.FragmentSignupBinding
import javax.inject.Inject


class SignInFragment : BaseFragment(), RouteToSignupSigninListener {

  @Inject
  lateinit var fragment: Fragment

  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var signUpLogInController: SignUpLogInController

  private lateinit var binding: FragmentSignInBinding
  override fun injectDependencies(fragmentComponent: FragmentComponent) {
    fragmentComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    // Inflate the layout for this fragment
    binding = FragmentSignInBinding.inflate(inflater, container, false)

    val signInViewModel =
      SignInViewModel(fragment as RouteToSignupSigninListener, signUpLogInController)

    binding.let {
      it.lifecycleOwner = fragment
      it.viewModel = signInViewModel
    }
    binding.signinbtn.setOnClickListener {
      binding.progressBar.visibility = View.VISIBLE
      signInViewModel.onClickSignIn(
        activity,
        binding.email.text.toString(),
        binding.password.text.toString()
      )
    }

    signInViewModel.loginResult.observe(viewLifecycleOwner) {
      when (it) {
        is com.example.utility.Result.Success<*> -> {
          binding.progressBar.visibility = View.GONE
          (activity as? RouteToHomeActivity)?.routeToHome()
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
    val tag: String = "SignInFragment"

    fun newInstance(): SignInFragment {
      return SignInFragment()
    }
  }

  override fun routeToSignupOrSignin() {
    (activity as RouteToSignupSigninActivityListener).routeToSignupSigninActivity(
      SignupFragment.newInstance(),
      SignupFragment.tag
    )
  }
}
