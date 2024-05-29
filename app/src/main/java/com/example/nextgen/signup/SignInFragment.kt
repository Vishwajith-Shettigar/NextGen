package com.example.nextgen.signup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    val signInViewModel = SignInViewModel(fragment as RouteToSignupSigninListener,signUpLogInController)

    binding.let {
      it.lifecycleOwner = fragment
      it.viewModel = signInViewModel
    }
    binding.signinbtn.setOnClickListener {
      signInViewModel.onClickSignIn(
        activity,
        binding.email.text.toString(),
        binding.password.text.toString()
      )
    }

    signInViewModel.loginResult.observe(viewLifecycleOwner){
      when (it) {
        is com.example.utility.Result.Success<*> -> {
          (activity as? RouteToHomeActivity)?.routeToHome()
        }
        is com.example.utility.Result.Failure -> {
          Log.e(LOG_KEY,it.message)
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
    Log.e(LOG_KEY, "sign in Fragment called")
    (activity as RouteToSignupSigninActivityListener).routeToSignupSigninActivity(
      SignupFragment.newInstance(),
      SignupFragment.tag
    )
  }
}
