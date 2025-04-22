package com.hylo.stylespace.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hylo.stylespace.model.Establishment
import com.hylo.stylespace.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class UserViewModel() : ViewModel() {

    private fun getDatabaseReference(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isUserLoaded = MutableStateFlow(false)
    val isUserLoaded: StateFlow<Boolean> = _isUserLoaded

    fun loadUser(user: User) {
        viewModelScope.launch {
            try {

                withTimeoutOrNull(3000L) {
                    val db = getDatabaseReference()

                    val userQuery = db.collection("users")
                        .whereEqualTo("id", user.id)
                        .limit(1)

                    val userQuerySnapshot = userQuery.get().await()

                    if (!userQuerySnapshot.isEmpty) {
                        val document = userQuerySnapshot.documents[0]
                        val existingUser = document.toObject(User::class.java)!!

                        _user.value = existingUser
                        _isUserLoaded.value = true
                    } else {

                        _user.value = user
                        _isUserLoaded.value = true
                    }
                } ?: run {
                    _user.value = user
                    _isUserLoaded.value = true
                }
            } catch (e: Exception) {
                Log.e("LeadUserVM", "Error loading user", e)
                _user.value = user
                _isUserLoaded.value = true
            }
        }
    }

    fun loginUser(user: User, establishment: Establishment) {
        viewModelScope.launch {
            try {
                val db = getDatabaseReference()

                val establishmentQuery = db.collection("establishments")
                    .whereEqualTo("name", establishment.name)
                    .limit(1)

                val establishmentSnapshot = establishmentQuery.get().await()

                val establishmentRef = if (establishmentSnapshot.isEmpty) {

                    val newEstRef = db.collection("establishments").add(establishment).await()
                    db.collection("establishments").document(newEstRef.id)

                } else {
                    db.collection("establishments").document(establishmentSnapshot.documents[0].id)

                }

                val userQuery = db.collection("users")
                    .whereEqualTo("id", user.id)
                    .limit(1)

                val userQuerySnapshot = userQuery.get().await()

                if (!userQuerySnapshot.isEmpty) {
                    val document = userQuerySnapshot.documents[0]
                    val existingUser = document.toObject(User::class.java)!!

                    val updatedEstablishments = existingUser.establishmentUsed.toMutableList()

                    if (!updatedEstablishments.contains(establishmentRef.id)) {
                        updatedEstablishments.add(establishmentRef.id)
                        db.collection("users").document(document.id)
                            .update("establishments", updatedEstablishments)
                            .await()

                    }

                    _user.value = existingUser.copy(
                        id = document.id,
                        establishmentUsed = updatedEstablishments
                    )

                } else {
                    val newUser = user.copy(establishmentUsed = listOf(establishmentRef.id))
                    val newUserRef = db.collection("users").add(newUser).await()

                    _user.value = newUser.copy(id = newUserRef.id)

                }

            } catch (e: Exception) {
                Log.e("LoginVM", "Login error", e)
                _user.value = null
            }
        }
    }
}