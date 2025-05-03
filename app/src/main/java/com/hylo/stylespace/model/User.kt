package com.hylo.stylespace.model

data class User(
    val id                : String  = "",
    val name              : String  = "",
    val email             : String  = "",
    val phone             : String  = "",
    val type              : String  = "",
    val fcmToken          : String  = "",
    val establishmentUsed : String  = "",
    val photoProfile      : String? = null
)

