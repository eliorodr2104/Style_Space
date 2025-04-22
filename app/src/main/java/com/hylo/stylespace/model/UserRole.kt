package com.hylo.stylespace.model

sealed class UserRole(val role: String) {
    object Client: UserRole("client")
    object Employee: UserRole("employee")
    object Manager: UserRole("manager")
}