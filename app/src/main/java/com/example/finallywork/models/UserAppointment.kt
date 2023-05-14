package com.example.finallywork.models

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

/*
 * Created by Vladyslava Buriakovska on 10.05.2023
 */
data class UserAppointment(
    val id: String,
    var date: Date,
    val userId: String,
    val doctorId: String,
    val isRated: Boolean,
) {

    companion object {
        val firebaseFirestore: FirebaseFirestore by lazy { Firebase.firestore }
        val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

        const val DATE_FORMAT_PATTERN = "dd.MM.yyyy"
        const val TIME_FORMAT_PATTERN = "H:mm"

        const val collection = "user-appointments"
        const val id = "id"
        const val date = "date"
        const val userId = "userId"
        const val doctorId = "doctorId"
        const val isRated = "isRated"

        fun getAll(onSuccess: (ArrayList<UserAppointment>) -> Unit, onFailure: (String) -> Unit) {
            firebaseAuth.currentUser?.let { user ->
                User.firebaseFirestore.collection(collection)
                    .orderBy(date, Query.Direction.DESCENDING)
                    .whereEqualTo(userId, user.uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        val result = ArrayList<UserAppointment>()
                        documents.map {
                            val appointmentDate = it.getDate(date)
                            val isRated = it.getBoolean("isRated")
                            appointmentDate?.let { date ->
                                isRated?.let { isRated ->
                                    result.add(
                                        UserAppointment(
                                            id = it.getString(id).toString(),
                                            date = date,
                                            userId = it.getString(userId).toString(),
                                            doctorId = it.getString(doctorId).toString(),
                                            isRated = isRated
                                        )
                                    )
                                }
                            }

                        }
                        onSuccess.invoke(result)
                    }
                    .addOnFailureListener { exception ->
                        exception.localizedMessage?.let {
                            Log.e("TAG", it)
                            onFailure.invoke(it)
                        }
                    }
            }
        }

        fun getById(
            id: String,
            onSuccess: (UserAppointment) -> Unit,
            onFailure: (String) -> Unit
        ) {
            firebaseFirestore.collection(collection)
                .whereEqualTo(UserAppointment.id, id)
                .get()
                .addOnSuccessListener { task ->
                    task.documents.map {
                        val appointmentDate = it.getDate(date)
                        val isRated = it.getBoolean("isRated")
                        appointmentDate?.let { date ->
                            isRated?.let { isRated ->
                                onSuccess.invoke(
                                    UserAppointment(
                                        id = it.getString(UserAppointment.id).toString(),
                                        date = date,
                                        userId = it.getString(userId).toString(),
                                        doctorId = it.getString(doctorId).toString(),
                                        isRated = isRated
                                    )
                                )
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    exception.localizedMessage?.let { onFailure.invoke(it) }
                }
        }
    }

    fun addToDataBase(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val appointment = hashMapOf(
            UserAppointment.id to id,
            UserAppointment.date to date,
            UserAppointment.userId to userId,
            UserAppointment.doctorId to doctorId,
            UserAppointment.isRated to isRated
        )
        firebaseFirestore.collection(UserAppointment.collection)
            .add(appointment)
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { onFailure.invoke(it) }
            }
    }

    fun delete(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firebaseFirestore.collection(collection)
            .whereEqualTo(UserAppointment.id, id)
            .get()
            .addOnSuccessListener { task ->
                task.documents.map { document ->
                    firebaseFirestore.collection(collection)
                        .document(
                            document.id
                        ).delete()
                        .addOnSuccessListener {
                            onSuccess.invoke()
                        }
                        .addOnFailureListener { exception ->
                            exception.localizedMessage?.let { onFailure.invoke(it) }
                        }
                }
            }
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { onFailure.invoke(it) }
            }
    }

    fun makeRated(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firebaseFirestore.collection(collection)
            .whereEqualTo(UserAppointment.id, id)
            .get()
            .addOnSuccessListener { task ->
                task.documents.map { document ->
                    firebaseFirestore.collection(collection)
                        .document(
                            document.id
                        ).update(
                            hashMapOf<String, Any>(
                                UserAppointment.isRated to true
                            )
                        ).addOnSuccessListener {
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
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { onFailure.invoke(it) }
            }
    }
}