package com.hylo.stylespace.model

import com.google.firebase.Timestamp

data class Appointment(
    val id: String = "",
    val userId: String = "",
    val employeeId: String = "",
    val servicesId: String = "",
    val dataOra: Timestamp = Timestamp.now(),
    val state: String = "active", // active | deleted | complete
    val notes: String = ""
)
