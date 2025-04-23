package com.hylo.stylespace.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hylo.stylespace.viewmodel.ServicesViewModel

class ServicesViewModelFactory(private val establishmentId: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ServicesViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return ServicesViewModel(establishmentId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}