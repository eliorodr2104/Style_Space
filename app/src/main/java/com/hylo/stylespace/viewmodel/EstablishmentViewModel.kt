package com.hylo.stylespace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hylo.stylespace.model.Establishment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EstablishmentViewModel : ViewModel() {

    private val _establishment = MutableLiveData<List<Establishment>>()
    val establishment: LiveData<List<Establishment>> = _establishment

    private val _establishmentUse = MutableLiveData<Establishment?>()
    val establishmentUse: LiveData<Establishment?> = _establishmentUse

    suspend fun loadEstablishmentForUser(userId: String) {
        withContext(Dispatchers.IO) {

        }
    }

    fun selectEstablishment(establishment: Establishment) {
        _establishmentUse.value = establishment
    }

    suspend fun createEstablishment(establishment: Establishment) {
        withContext(Dispatchers.IO) {

        }
    }
}