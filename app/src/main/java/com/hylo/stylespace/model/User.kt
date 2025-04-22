package com.hylo.stylespace.model

data class User(
    val id               : String       = "",
    val name             : String       = "",
    val email            : String       = "",
    val phone            : String       = "",
    val type             : String       = "", // Divide by "client | employee | management"
    val fcmToken         : String       = "", // Token for push notification
    val establishmentUsed: List<String> = emptyList(),
    val photoProfile     : String?      = null
)
