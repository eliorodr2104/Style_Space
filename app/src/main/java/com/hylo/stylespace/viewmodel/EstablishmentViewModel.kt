package com.hylo.stylespace.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hylo.stylespace.model.Appointment
import com.hylo.stylespace.model.Establishment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EstablishmentViewModel(
    private val establishmentId: String
) : ViewModel() {

    private fun getDatabaseReference(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    private val _establishments = MutableStateFlow<List<Establishment>>(emptyList())
    val establishments: StateFlow<List<Establishment>> = _establishments

    private val _establishmentUse = MutableStateFlow<Establishment?>(null)
    val establishmentUse: StateFlow<Establishment?> = _establishmentUse

    init {
        viewModelScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.IO) {
                loadEstablishmentUsed()
            }
        }
    }

    suspend fun loadEstablishmentUsed() {
        withContext(Dispatchers.IO) {
            try {

                val db = getDatabaseReference()

                val establishmentUseQuery = db
                    .collection("establishments")
                    .document(establishmentId)

                val querySnapshot = establishmentUseQuery
                                    .get()
                                    .await()

                try {

                    _establishmentUse.value = querySnapshot.toObject(Establishment::class.java)

                } catch (e: Exception) {
                    Log.e("AppointmentViewModel", "Error converting document to Establishment object", e)
                    _establishmentUse.value = null
                }

            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "Error loading establishment", e)
            }

        }
    }

    suspend fun createEstablishment(establishment: Establishment) {
        withContext(Dispatchers.IO) {

        }
    }
}