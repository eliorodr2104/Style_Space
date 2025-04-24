package com.hylo.stylespace.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Services(
    @DocumentId
    val id                : String = "",
    val name              : String = "",
    val description       : String = "",
    val durationInMinutes : Int    = 0,
    val price             : Double = 0.0,
    val type              : String = ""
)
