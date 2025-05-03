package com.hylo.stylespace.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hylo.stylespace.viewmodel.EstablishmentViewModel

class EstablishmentViewModelFactory(
    private val establishmentId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(EstablishmentViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return EstablishmentViewModel(
                establishmentId = establishmentId
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}