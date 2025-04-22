package com.hylo.stylespace.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hylo.stylespace.model.Appointment
import com.hylo.stylespace.model.Employee
import com.hylo.stylespace.model.Establishment
import com.hylo.stylespace.model.Services
import com.hylo.stylespace.model.User
import kotlinx.coroutines.tasks.await

class FirebaseRepository constructor(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getUser(
        user: User,
        establishmentId: String
    ): User? {

        return try {
            val result = firestore.collection("establishments")
                .document(establishmentId)
                .collection("user")
                .whereEqualTo("email", user.email)
                .get()
                .await()

            result.documents.firstNotNullOfOrNull { it.toObject(User::class.java) }

        } catch (e: Exception) {
            null
        }
    }

    suspend fun createUser(
        user: User,
        establishmentId: String
    ): User? {

        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
                ?: throw IllegalStateException("User not authenticated")

            val userWithId = user.copy(id = uid)

            firestore
                .collection("establishments")
                .document(establishmentId)
                .collection("user")
                .document(uid)
                .set(userWithId)
                .await()

            userWithId

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getEstablishmentForUser(userId: String): List<Establishment> {
        return try {
            val result = firestore.collection("establishments")
                .whereEqualTo(
                    "userId",
                    userId
                )
                .get()
                .await()

            result.documents.mapNotNull { document ->
                document.toObject(Establishment::class.java)
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createEstablishment(establishment: Establishment): Establishment? {
        return try {
            val querySnapshot = firestore.collection("establishments")
                .whereEqualTo("name", establishment.name)
                .get()
                .await()

            if (querySnapshot.documents.isNotEmpty()) {
                throw IllegalStateException("Establishment exist!")
            }

            val documentRef = firestore.collection("establishments")
                .add(establishment)
                .await()

            establishment.copy(id = documentRef.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getServicesForEstablishment(establishmentId: String): List<Services> {

        return try {
            val result = firestore.collection("establishments/services")
                .whereEqualTo(
                    "establishmentId",
                    establishmentId
                )
                .get()
                .await()

            result.documents.mapNotNull { document ->
                document.toObject(Services::class.java)

            }

        } catch (e: Exception) {
            emptyList()
        }

    }

    suspend fun getEmployeeForEstablishment(establishmentId: String): List<Employee> {
        return try {
            val result = firestore.collection("establishments")
                .document(establishmentId)
                .collection("employee")
                .get()
                .await()

            result.documents.mapNotNull { document ->
                document.toObject(Employee::class.java)

            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAppointment(
        establishmentId: String,
        userId: String = ""
    ): List<Appointment> {

        return try {
            val query = firestore.collection("establishments")
                .document(establishmentId)
                .collection("appointments")
                .let {
                    if (userId.isNotEmpty()) it.whereEqualTo("userId", userId)
                    else it
                }

            val result = query.get().await()
            result.documents.mapNotNull { document ->
                document.toObject(Appointment::class.java)?.copy(
                    id = document.id
                )
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addAppointment(
        establishmentId: String,
        appointment: Appointment,
        onResult: (Boolean, String?) -> Unit
    ) {

        try {
            val documentRef = firestore.collection("establishments")
                .document(establishmentId)
                .collection("appointments")
                .document()

            val newAppointment = appointment.copy(
                id = documentRef.id
            )

            documentRef.set(newAppointment).await()
            onResult(true, null)

        } catch (e: Exception) {
            onResult(false, e.message)
        }
    }

    suspend fun deleteAppointment(
        establishmentId: String,
        appointmentId: String
    ): Boolean {

        return try {
            firestore.collection("establishments")
                .document(establishmentId)
                .collection("appointments")
                .document(appointmentId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}