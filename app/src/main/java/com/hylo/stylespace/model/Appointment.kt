package com.hylo.stylespace.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Appointment(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val employeeId: String = "",
    val servicesId: String = "",
    val dataOra: Timestamp = Timestamp.now(),
    val state: String = "active", // active | deleted | complete
    val notes: String = ""
)
