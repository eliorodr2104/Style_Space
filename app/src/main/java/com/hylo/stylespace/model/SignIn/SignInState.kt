package com.hylo.stylespace.model.SignIn

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)