package com.example.finallywork.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

data class User(
    val authId: String? = null,
    val lastName: String? = null,
    val firstName: String? = null,
    val dateOfBirth: Date? = null,
    val role: Role? = Role.USER,
    var photoUrl: String? = null,
    var phoneNumber: String? = null
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
        const val role = "role"
        const val photoUrl = "photoUrl"
        const val phoneNumber = "phoneNumber"

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
                                lastName = it.getString(lastName),
                                firstName = it.getString(
                                    firstName
                                ),
                                dateOfBirth = date,
                                role = Role.valueOf(
                                    it.getString(
                                        role
                                    ).toString()
                                ),
                                photoUrl = it.getString(photoUrl),
                                phoneNumber = it.getString(phoneNumber)
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

        private fun deleteFromAuth(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
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
            User.role to role,
            User.photoUrl to photoUrl,
            User.phoneNumber to phoneNumber
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

    fun edit(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firebaseFirestore.collection(collection)
            .whereEqualTo(User.authId, authId)
            .get()
            .addOnSuccessListener { task ->
                task.documents.map { document ->
                    lastName?.let { lastName ->
                        firstName?.let { firstName ->
                            phoneNumber?.let { phone ->
                                dateOfBirth?.let { date ->
                                    photoUrl?.let { url ->
                                        firebaseFirestore.collection(collection)
                                            .document(
                                                document.id
                                            ).update(
                                                hashMapOf<String, Any>(
                                                    User.lastName to lastName,
                                                    User.firstName to firstName,
                                                    User.phoneNumber to phone,
                                                    User.dateOfBirth to dateOfBirth,
                                                    User.photoUrl to url
                                                )
                                            )
                                            .addOnSuccessListener {
                                                onSuccess.invoke()
                                            }
                                            .addOnFailureListener { exception ->
                                                exception.localizedMessage?.let {
                                                    onFailure.invoke(
                                                        it
                                                    )
                                                }
                                            }
                                    }
                                }

                            }

                        }
                    }

                }
            }
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { onFailure.invoke(it) }
            }
    }
}