package com.hylo.stylespace.model.SignIn

import com.hylo.stylespace.model.User

data class SignInResult(
    val data: User?,
    val errorMessage: String?
)