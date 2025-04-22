package com.hylo.stylespace.model

data class Employee(
    val id            : String       = "",
    val servicesEnable: List<String> = emptyList(), // ID Services
    val available     : Boolean      = true
)
