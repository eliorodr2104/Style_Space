package com.hylo.stylespace.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hylo.stylespace.model.Services
import com.hylo.stylespace.model.TypeServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ServicesViewModel() : ViewModel() {

    private fun getDatabaseReference(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    private val _services = MutableStateFlow<List<Services>>(emptyList())
    val services: StateFlow<List<Services>> = _services

    private val _typeServices = MutableStateFlow<List<TypeServices>>(emptyList())
    val typeServices: StateFlow<List<TypeServices>> = _typeServices

    private val _isCreatingService = MutableStateFlow(false)
    val isCreatingService: StateFlow<Boolean> = _isCreatingService

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadTypeServices(
        establishmentId: String
    ) {
        viewModelScope.launch {
            try {
                val db = getDatabaseReference()

                val typeServicesQuery = db.collection("establishments")
                    .document(establishmentId)
                    .collection("typeServices")

                val typeServicesQuerySnapshot = typeServicesQuery.get().await()

                if (!typeServicesQuerySnapshot.isEmpty) {
                    val typeServicesList = typeServicesQuerySnapshot.documents.mapNotNull { document ->
                        try {
                            val name = document.getString("name") ?: ""

                            TypeServices(
                                name = name
                            )

                        } catch (e: Exception) {
                            Log.e("ServicesViewModel", "Error converting document to Services object", e)
                            null
                        }
                    }

                    _typeServices.value = typeServicesList
                } else {
                    _typeServices.value = emptyList()
                }

            } catch (e: Exception) {
                Log.e("ServicesViewModel", "Error loading services", e)
                _services.value = emptyList()
            }
        }
    }

    fun loadServices(
        establishmentId: String
    ) {
        viewModelScope.launch {
            try {
                val db = getDatabaseReference()

                val servicesQuery = db.collection("establishments")
                    .document(establishmentId)
                    .collection("services")

                val servicesQuerySnapshot = servicesQuery.get().await()

                if (!servicesQuerySnapshot.isEmpty) {
                    val servicesList = servicesQuerySnapshot.documents.mapNotNull { document ->
                        try {
                            val id = document.id
                            val name = document.getString("name") ?: ""
                            val description = document.getString("description") ?: ""
                            val durationInMinutes = document.getLong("durationInMinutes")?.toInt() ?: 0
                            val price = document.getDouble("price") ?: 0.0
                            val type = document.getString("type") ?: ""

                            Services(
                                id = id,
                                name = name,
                                description = description,
                                price = price,
                                durationInMinutes = durationInMinutes,
                                type = type
                            )

                        } catch (e: Exception) {
                            Log.e("ServicesViewModel", "Error converting document to Services object", e)
                            null
                        }
                    }

                    _services.value = servicesList
                } else {
                    _services.value = emptyList()
                }

            } catch (e: Exception) {
                Log.e("ServicesViewModel", "Error loading services", e)
                _services.value = emptyList()
            }
        }
    }

    fun createService(
        establishmentId: String,
        newService: Services
    ) {
        _isCreatingService.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val db = getDatabaseReference()

                val serviceCollection = db.collection("establishments")
                    .document(establishmentId)
                    .collection("services")

                val addedDocRef = serviceCollection.add(newService).await()

                val createdService = newService.copy(id = addedDocRef.id)
                _services.value = _services.value + createdService

                Log.d("ServicesViewModel", "Service created successfully with ID: ${addedDocRef.id}")

            } catch (e: Exception) {
                Log.e("ServicesViewModel", "Error creating service", e)
                _errorMessage.value = "Errore nella creazione del servizio: ${e.message}"

            } finally {
                _isCreatingService.value = false
            }
        }
    }

    fun resetError() {
        _errorMessage.value = null
    }
}