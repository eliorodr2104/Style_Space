package com.hylo.stylespace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hylo.stylespace.model.Employee
import kotlinx.coroutines.launch

class EmployeeViewModel : ViewModel() {

    private val _employee = MutableLiveData<List<Employee>>()
    val employee: LiveData<List<Employee>> = _employee

    fun loadEmployee(establishmentId: String) {
        viewModelScope.launch {

        }
    }
}