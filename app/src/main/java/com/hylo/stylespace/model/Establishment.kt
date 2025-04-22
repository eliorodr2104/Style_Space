package com.hylo.stylespace.model

data class Establishment(
    val id          : String              = "",
    val name        : String              = "",
    val address     : String              = "",
    val openingHours: Map<String, String> = emptyMap(), // example: "mon" -> "9-18"
    val closingDates: List<String>        = emptyList() // ISO date "2025-04-01"
)
