package com.hylo.stylespace.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class TypeServices(
    @DocumentId
    val id: String = "",
    val name: String = ""
)
