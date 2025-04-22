package com.hylo.stylespace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hylo.stylespace.model.Employee
import com.hylo.stylespace.repository.FirebaseRepository
import kotlinx.coroutines.launch

class EmployeeViewModel constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _employee = MutableLiveData<List<Employee>>()
    val employee: LiveData<List<Employee>> = _employee

    fun loadEmployee(establishmentId: String) {
        viewModelScope.launch {
            val list = repository.getEmployeeForEstablishment(establishmentId)
            _employee.value = list
        }
    }
}