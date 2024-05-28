package com.example.nextgen.signup

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.registration.SignUpLogInController
import com.example.nextgen.Fragment.FragmentScope
import com.example.nextgen.viewmodel.ObservableViewModel


class SignInViewModel(
  private val fragment: RouteToSignupSigninListener,
  private val signUpLogInController: SignUpLogInController,
) : ObservableViewModel() {

  private val _loginResult = MutableLiveData<com.example.utility.Result<*>>()
  val loginResult: LiveData<com.example.utility.Result<*>> get() = _loginResult

  fun onClickSignUp(view: View) {
    fragment.routeToSignupOrSignin()
  }

  fun onClickSignIn(
    activity: AppCompatActivity,
    email: String,
    password: String,
  ) {
    if (validateSignUpDetails(
        email, password
      )
    ) {
      signUpLogInController.logIn(
        activity,
        email.trim(), password
      ) { result ->
        _loginResult.postValue(result)
      }
    }
  }

  private fun validateSignUpDetails(email: String, password: String): Boolean {
    if (email.isBlank() || password.isBlank())
      return false
    return true
  }
}
