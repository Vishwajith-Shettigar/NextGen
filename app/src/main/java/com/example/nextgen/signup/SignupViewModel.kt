package com.example.nextgen.signup

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.registration.SignUpLogInController
import com.example.nextgen.Fragment.FragmentScope
import com.example.nextgen.viewmodel.ObservableViewModel


class SignupViewModel(
  private val fragment: RouteToSignupSigninListener,
  private val signUpLogInController: SignUpLogInController,
) : ObservableViewModel() {

  private val _registrationResult = MutableLiveData<com.example.utility.Result>()
  val registrationResult: LiveData<com.example.utility.Result> get() = _registrationResult
  fun onClickSignIn(view: View) {
    fragment.routeToSignupOrSignin()
  }

  fun onClickSignUp(
    activity: AppCompatActivity,
    username: String,
    email: String,
    password: String,
  ) {

    if (validateSignUpDetails(
        username, email, password
      )
    ) {
      signUpLogInController.registerUser(
        activity,
        username, email.trim(), password
      ) { result ->
        _registrationResult.postValue(result)
      }
    }
  }

  private fun validateSignUpDetails(username: String, email: String, password: String): Boolean {

    if (!isUsernameExists(username)) {
      if (email.isBlank() || password.isBlank())
        return false
      return true
    }
    return false
  }

  private fun isUsernameExists(username: String): Boolean {
    return false
  }
}
