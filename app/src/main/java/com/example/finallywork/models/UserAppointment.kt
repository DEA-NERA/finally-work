package com.example.finallywork.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

/*
 * Created by Vladyslava Buriakovska on 10.05.2023
 */
data class UserAppointment(
    var date: Date,
    val userId: String,
    val doctorId: String
) {

    companion object {
        val firebaseFirestore: FirebaseFirestore by lazy { Firebase.firestore }
        val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

        const val DATE_FORMAT_PATTERN = "dd.MM.yyyy"
        const val TIME_FORMAT_PATTERN = "H:mm"

        const val collection = "user-appointments"
        const val date = "date"
        const val userId = "userId"
        const val doctorId = "doctorId"

        fun getAll(onSuccess: (ArrayList<UserAppointment>) -> Unit, onFailure: (String) -> Unit) {
            firebaseAuth.currentUser?.let { user ->
                User.firebaseFirestore.collection(collection)
                    .whereEqualTo(userId, user.uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        val result = ArrayList<UserAppointment>()
                        documents.map {
                            val appointmentDate = it.getDate(date)
                            appointmentDate?.let { date ->
                                result.add(
                                    UserAppointment(
                                        date = date,
                                        userId = it.getString(userId).toString(),
                                        doctorId = it.getString(doctorId).toString()
                                    )
                                )
                            }

                        }
                        onSuccess.invoke(result)
                    }
                    .addOnFailureListener { exception ->
                        exception.localizedMessage?.let { onFailure.invoke(it) }
                    }
            }
        }
    }

    fun addToDataBase(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val appointment = hashMapOf(
            UserAppointment.date to date,
            UserAppointment.userId to userId,
            UserAppointment.doctorId to doctorId
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
            .whereEqualTo(UserAppointment.date, date)
            .whereEqualTo(UserAppointment.userId, userId)
            .whereEqualTo(UserAppointment.doctorId, doctorId)
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
}