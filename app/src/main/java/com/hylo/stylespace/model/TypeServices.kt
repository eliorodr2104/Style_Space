package com.hylo.stylespace.model

import java.util.UUID

data class TypeServices(
    val id: UUID = UUID.randomUUID(),
    val name: String
)
