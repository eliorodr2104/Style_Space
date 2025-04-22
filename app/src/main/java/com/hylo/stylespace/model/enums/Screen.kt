package com.hylo.stylespace.model.enums

sealed class Screen(val route: String) {
    object Home: Screen("home_screen")
    object Profile: Screen("profile_screen")
    object Appointment: Screen("appointment_screen")
    object Setting: Screen("setting_screen")
    object CreateAppointment: Screen("create_appointment")
}