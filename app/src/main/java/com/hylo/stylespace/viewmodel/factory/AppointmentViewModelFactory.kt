package com.hylo.stylespace.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hylo.stylespace.viewmodel.AppointmentViewModel
import com.hylo.stylespace.viewmodel.ServicesViewModel

class AppointmentViewModelFactory(
    private val establishmentId: String,
    private val userId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return AppointmentViewModel(
                establishmentId = establishmentId,
                userId = userId
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}