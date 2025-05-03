package com.hylo.stylespace.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hylo.stylespace.model.Appointment
import com.hylo.stylespace.model.enums.StateAppointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar

private const val TIME_WINDOW_HOUR = 96

class AppointmentViewModel(
    private val establishmentId: String,
    private val userId: String
) : ViewModel() {

    private fun getDatabaseReference(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    private val _appointment = MutableStateFlow<List<Appointment>>(emptyList())
    val appointment: StateFlow<List<Appointment>> = _appointment

    private val _nextAppointment = MutableStateFlow<Appointment?>(null)
    val nextAppointment: StateFlow<Appointment?> = _nextAppointment

    init {

        viewModelScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.IO) {
                loadNextAppointment()
            }
        }

    }

    suspend fun loadAppointment() {
        withContext(Dispatchers.IO) {
            try {
                val db = getDatabaseReference()

                val typeServicesQuery = db.collection("establishments")
                    .document(establishmentId)
                    .collection("appointment")
                    .whereEqualTo(
                        "userId",
                        userId
                    )

                val appointmentQuerySnapshot = typeServicesQuery.get().await()

                if (!appointmentQuerySnapshot.isEmpty) {
                    val appointmentList = appointmentQuerySnapshot.documents.mapNotNull { document ->
                        try {
                            val id = document.getString("id") ?: ""
                            val userId = document.getString("userId") ?: ""
                            val employeeId = document.getString("employeeId") ?: ""
                            val servicesId = document.getString("servicesId") ?: ""
                            val dataOra = document.getTimestamp("dataOra") ?: Timestamp.now()
                            val state = document.getString("state") ?: ""
                            val notes = document.getString("notes") ?: ""

                            Appointment(
                                id = id,
                                userId = userId,
                                employeeId = employeeId,
                                servicesId = servicesId,
                                dataOra = dataOra,
                                state = state,
                                notes = notes
                            )

                        } catch (e: Exception) {
                            Log.e("ServicesViewModel", "Error converting document to Services object", e)
                            null
                        }
                    }

                    _appointment.value = appointmentList
                } else {
                    _appointment.value = emptyList()
                }

            } catch (e: Exception) {
                Log.e("ServicesViewModel", "Error loading services", e)
                _appointment.value = emptyList()
            }
        }
    }

    suspend fun createAppointment(
        newAppointment: Appointment,
        establishmentId: String
    ) {
        withContext(Dispatchers.IO) {
            try {
                val db = getDatabaseReference()

                val serviceCollection = db.collection("establishments")
                    .document(establishmentId)
                    .collection("appointment")

                val addedDocRef = serviceCollection.add(newAppointment).await()

                val createdService = newAppointment.copy(id = addedDocRef.id)
                _appointment.value = _appointment.value + createdService

                Log.d("ServicesViewModel", "Service created successfully with ID: ${addedDocRef.id}")

            } catch (e: Exception) {
                Log.e("ServicesViewModel", "Error creating service", e)

            }
        }
    }

    suspend fun loadNextAppointment() {

        withContext(Dispatchers.IO) {
            try {
                val db = getDatabaseReference()
                val now = Timestamp.now()

                val calendar = Calendar.getInstance()
                calendar.time = now.toDate()

                calendar.add(Calendar.HOUR, TIME_WINDOW_HOUR)
                val endTimeWindow = Timestamp(calendar.time)

                val nextAppointmentQuery = db
                    .collection("establishments")
                    .document(establishmentId)
                    .collection("appointment")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("state", StateAppointment.ACTIVE.state)
                    .whereGreaterThan("dataOra", now)
                    .whereLessThan("dataOra", endTimeWindow)
                    .orderBy("dataOra", Query.Direction.ASCENDING)
                    .limit(1)

                val querySnapshot = nextAppointmentQuery
                    .get()
                    .await()

                if (!querySnapshot.isEmpty) {
                    //val document = querySnapshot.documents.first()

                    try {
                        /*val id = document.getString("id") ?: document.id
                        val userIdField = document.getString("userId") ?: ""
                        val employeeId = document.getString("employeeId") ?: ""
                        val servicesId = document.getString("servicesId") ?: ""
                        val dataOra = document.getTimestamp("dataOra") ?: Timestamp.now()
                        val state = document.getString("state") ?: ""
                        val notes = document.getString("notes") ?: ""

                        val appointment = Appointment(
                            id = id,
                            userId = userIdField,
                            employeeId = employeeId,
                            servicesId = servicesId,
                            dataOra = dataOra,
                            state = state,
                            notes = notes
                        )

                        _nextAppointment.value = appointment*/

                        _nextAppointment.value = querySnapshot.documents.first().toObject(Appointment::class.java)

                    } catch (e: Exception) {

                        Log.e("AppointmentViewModel", "Error converting document to Appointment object", e)
                        _nextAppointment.value = null
                    }

                } else {
                    _nextAppointment.value = null
                }

            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "Error loading next appointment", e)
                _nextAppointment.value = null

            }
        }
    }
}