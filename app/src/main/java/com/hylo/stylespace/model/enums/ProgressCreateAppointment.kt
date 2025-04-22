package com.hylo.stylespace.model.enums

sealed class ProgressCreateAppointment(val route: String) {
    object SelectService: ProgressCreateAppointment("select_service")
    object SelectDate: ProgressCreateAppointment("select_date")
    object SelectHour: ProgressCreateAppointment("select_hour")
    object SelectEmployee: ProgressCreateAppointment("select_employee")
    object ConfirmAppointment: ProgressCreateAppointment("confirm_appointment")
}
