package com.example.finallywork.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

data class User(
    val authId: String,
    val lastName: String,
    val firstName: String,
    val dateOfBirth: Date,
//    private val city: City
) {
    companion object {
        val firebaseFirestore: FirebaseFirestore by lazy { Firebase.firestore }
        val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

        const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
        const val collection = "users"
        const val authId = "authId"
        const val lastName = "lastName"
        const val firstName = "firstName"
        const val dateOfBirth = "dateOfBirth"

        fun getUser(authId: String, onSuccess: (User) -> Unit, onFailure: (String) -> Unit) {
            firebaseFirestore.collection(collection)
                .whereEqualTo(User.authId, authId)
                .get()
                .addOnSuccessListener { documents ->
                    documents.map {
                        val dateOfBirth = it.getDate(
                            dateOfBirth
                        )

                        dateOfBirth?.let { date ->
                            val user = User(
                                authId,
                                lastName = it.getString(lastName).toString(),
                                firstName = it.getString(
                                    firstName
                                ).toString(),
                                dateOfBirth = date
                            )
                            onSuccess.invoke(user)
                        }

                    }
                }
                .addOnFailureListener { exception ->
                    exception.localizedMessage?.let { onFailure.invoke(it) }
                }
        }

        fun delete(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
            firebaseAuth.currentUser?.let {
                firebaseFirestore.collection(collection)
                    .whereEqualTo(authId, it.uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        documents.map {
                            it.reference.delete()
                                .addOnSuccessListener {
                                    deleteFromAuth(onSuccess, onFailure)
                                }
                                .addOnFailureListener { exception ->
                                    exception.localizedMessage?.let { message ->
                                        onFailure.invoke(
                                            message
                                        )
                                    }
                                }
                        }

                    }
            }
        }

        fun deleteFromAuth(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
            firebaseAuth.currentUser?.let {
                it.delete()
                    .addOnSuccessListener {
                        onSuccess.invoke()
                    }.addOnFailureListener { exception ->
                        exception.localizedMessage?.let { message -> onFailure.invoke(message) }
                    }
            }
        }
    }

    fun addToDataBase(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = hashMapOf(
            User.authId to authId,
            User.lastName to lastName,
            User.firstName to firstName,
            User.dateOfBirth to dateOfBirth,
        )
        firebaseFirestore.collection(collection)
            .add(user)
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { onFailure.invoke(it) }
            }


    }
}