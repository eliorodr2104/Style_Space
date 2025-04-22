package com.hylo.stylespace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hylo.stylespace.model.Establishment
import kotlinx.coroutines.launch

class EstablishmentViewModel : ViewModel() {

    private val _establishment = MutableLiveData<List<Establishment>>()
    val establishment: LiveData<List<Establishment>> = _establishment

    private val _establishmentUse = MutableLiveData<Establishment?>()
    val establishmentUse: LiveData<Establishment?> = _establishmentUse

    fun loadEstablishmentForUser(userId: String) {
        viewModelScope.launch {

        }
    }

    fun selectEstablishment(establishment: Establishment) {
        _establishmentUse.value = establishment
    }

    fun createEstablishment(establishment: Establishment) {
        viewModelScope.launch {

        }
    }
}