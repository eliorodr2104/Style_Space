package com.hylo.stylespace.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Establishment(
    @DocumentId
    val id          : String              = "",
    val name        : String              = "",
    val address     : String              = "",
    val openingHours: Map<String, String> = emptyMap(),
    val closingDates: List<String>        = emptyList()
)
