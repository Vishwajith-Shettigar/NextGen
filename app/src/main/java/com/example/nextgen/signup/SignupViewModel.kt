package com.example.nextgen.signup

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nextgen.Fragment.FragmentScope
import com.example.nextgen.viewmodel.ObservableViewModel


class SignupViewModel(
  private val fragment: RouteToSignupSigninListener
  ): ObservableViewModel() {

   val emailLiveData:LiveData<String> = MutableLiveData<String>()
  val passwordLiveData:LiveData<String> = MutableLiveData<String>()
  var usernameLiveData:LiveData<String> = MutableLiveData<String>()


fun onClickSignIn(view: View){
  fragment.routeToSignupOrSignin()
}

}