package com.vzardd.xamlyteacher.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginViewModel :  ViewModel(){
    val usernameTextField = mutableStateOf("")
    val passTextField = mutableStateOf("")
    var auth = Firebase.auth
}