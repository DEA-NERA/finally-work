package com.example.finallywork.models

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class Doctor(
    val id: String,
    val lastName: String,
    val firstName: String,
    val dateOfBirth: Date,
    val dateStartWork: Date,
    val rating: Double,
    val specializations: ArrayList<String>,
    val appointments: ArrayList<Appointment>
) {
    companion object {
        val firebaseFirestore: FirebaseFirestore by lazy { Firebase.firestore }

        const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
        const val collection = "doctors"
        const val id = "id"
        const val lastName = "lastName"
        const val firstName = "firstName"
        const val dateOfBirth = "dateOfBirth"
        const val dateStartWork = "dateStartWork"
        const val specializations = "specializations"
        const val rating = "rating"
        const val appointments = "appointments"
    }

    fun addToDataBase(onSuccess: () -> Unit, onFailure: (String) -> Unit) {

        val appointment = ArrayList<HashMap<String, Any>>()
        appointments.map {
            appointment.add(
                hashMapOf(
                    Appointment.isAvailable to it.isAvailable,
                    Appointment.date to it.date
                )
            )
        }

        val doctor = hashMapOf(
            Doctor.id to id,
            Doctor.lastName to lastName,
            Doctor.firstName to firstName,
            Doctor.dateOfBirth to dateOfBirth,
            Doctor.dateStartWork to dateStartWork,
            Doctor.rating to rating,
            Doctor.specializations to specializations,
            Doctor.appointments to appointment
        )
        firebaseFirestore.collection(collection)
            .add(doctor)
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { onFailure.invoke(it) }
            }


    }
}