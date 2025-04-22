package com.hylo.stylespace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hylo.stylespace.model.Establishment
import com.hylo.stylespace.repository.FirebaseRepository
import kotlinx.coroutines.launch

class EstablishmentViewModel constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _establishment = MutableLiveData<List<Establishment>>()
    val establishment: LiveData<List<Establishment>> = _establishment

    private val _establishmentUse = MutableLiveData<Establishment?>()
    val establishmentUse: LiveData<Establishment?> = _establishmentUse

    fun loadEstablishmentForUser(userId: String) {
        viewModelScope.launch {
            val establishmentUser = repository.getEstablishmentForUser(userId)
            _establishment.value = establishmentUser

        }
    }

    fun selectEstablishment(establishment: Establishment) {
        _establishmentUse.value = establishment
    }

    fun createEstablishment(establishment: Establishment) {
        viewModelScope.launch {
            _establishmentUse.value = repository.createEstablishment(establishment)
        }
    }
}