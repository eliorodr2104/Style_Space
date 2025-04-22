package com.hylo.stylespace.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.hylo.stylespace.model.enums.ProgressCreateAppointment

data class ProgressCreateAppointmentItem(
    val imageVector: ImageVector,
    val imageVectorSelected: ImageVector,
    val text: String,
    val routeStep: ProgressCreateAppointment
)
