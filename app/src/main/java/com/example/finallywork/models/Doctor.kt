package com.example.finallywork.models

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class Doctor(
    val id: String,
    val lastName: String,
    val firstName: String,
    val roomNumber: String,
    val dateOfBirth: Date,
    val dateStartWork: Date,
    val rating: Double,
    val specializations: ArrayList<String>,
    val appointments: ArrayList<Appointment>
) {
    companion object {
        val firebaseFirestore: FirebaseFirestore by lazy { Firebase.firestore }

        const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
        const val TIME_FORMAT_PATTERN = "H:mm"
        const val collection = "doctors"
        const val id = "id"
        const val lastName = "lastName"
        const val firstName = "firstName"
        const val roomNumber = "roomNumber"
        const val dateOfBirth = "dateOfBirth"
        const val dateStartWork = "dateStartWork"
        const val specializations = "specializations"
        const val rating = "rating"
        const val appointments = "appointments"

        fun getAll(
            onSuccess: (ArrayList<Doctor>) -> Unit,
            onFailure: (String) -> Unit
        ) {
            firebaseFirestore.collection(collection)
                .orderBy(lastName, Query.Direction.ASCENDING)
                .orderBy(firstName, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    val result = ArrayList<Doctor>()
                    documents.map {
                        val dateOfBirth = it.getDate(dateOfBirth)
                        val dateStartWork = it.getDate(dateStartWork)
                        dateOfBirth?.let { dateBirth ->
                            dateStartWork?.let { dateStart ->
                                val doctor = Doctor(
                                    id = it.getString(id).toString(),
                                    lastName = it.getString(lastName).toString(),
                                    firstName = it.getString(firstName).toString(),
                                    roomNumber = it.getString(roomNumber).toString(),
                                    dateOfBirth = dateBirth,
                                    dateStartWork = dateStart,
                                    specializations = it.get(specializations) as ArrayList<String>,
                                    rating = it.getDouble(rating) ?: 5.0,
                                    appointments = it.get(appointments) as ArrayList<Appointment>
                                )
                                result.add(doctor)
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

        fun getById(
            id: String,
            onSuccess: (Doctor) -> Unit,
            onFailure: (String) -> Unit
        ) {
            firebaseFirestore.collection(collection)
                .whereEqualTo(Doctor.id, id)
                .get()
                .addOnSuccessListener { task ->
                    task.documents.map {
                        val dateOfBirth = it.getDate(dateOfBirth)
                        val dateStartWork = it.getDate(dateStartWork)
                        val appointments = it.get(appointments) as ArrayList<HashMap<String, Any>>
                        val resultAppointments = ArrayList<Appointment>()
                        appointments.map { appointment ->
                            resultAppointments.add(
                                Appointment(
                                    appointment[Appointment.isAvailable] as Boolean,
                                    (appointment[Appointment.date] as Timestamp).toDate()
                                )
                            )
                        }
                        dateOfBirth?.let { dateBirth ->
                            dateStartWork?.let { dateStart ->
                                val doctor = Doctor(
                                    id = it.getString(Companion.id).toString(),
                                    lastName = it.getString(lastName).toString(),
                                    firstName = it.getString(firstName).toString(),
                                    roomNumber = it.getString(roomNumber).toString(),
                                    dateOfBirth = dateBirth,
                                    dateStartWork = dateStart,
                                    specializations = it.get(specializations) as ArrayList<String>,
                                    rating = it.getDouble(rating) ?: 5.0,
                                    appointments = resultAppointments
                                )
                                onSuccess.invoke(doctor)
                            }
                        }
                    }

                }
                .addOnFailureListener { exception ->
                    exception.localizedMessage?.let { onFailure.invoke(it) }
                }

        }

        fun getByName(
            value: String,
            onSuccess: (ArrayList<Doctor>) -> Unit,
            onFailure: (String) -> Unit
        ) {
            firebaseFirestore.collection(collection)
                .where(
                    Filter.or(
                        Filter.equalTo(lastName, value),
                        Filter.equalTo(firstName, value)
                    )
                )
                .get()
                .addOnSuccessListener { documents ->
                    val result = ArrayList<Doctor>()
                    documents.map {
                        val dateOfBirth = it.getDate(dateOfBirth)
                        val dateStartWork = it.getDate(dateStartWork)
                        dateOfBirth?.let { dateBirth ->
                            dateStartWork?.let { dateStart ->
                                val doctor = Doctor(
                                    id = it.getString(id).toString(),
                                    lastName = it.getString(lastName).toString(),
                                    firstName = it.getString(firstName).toString(),
                                    roomNumber = it.getString(roomNumber).toString(),
                                    dateOfBirth = dateBirth,
                                    dateStartWork = dateStart,
                                    specializations = it.get(specializations) as ArrayList<String>,
                                    rating = it.getDouble(rating) ?: 5.0,
                                    appointments = it.get(appointments) as ArrayList<Appointment>
                                )
                                result.add(doctor)
                            }
                        }
                    }
                    onSuccess.invoke(result)

                }
                .addOnFailureListener { exception ->
                    exception.localizedMessage?.let { onFailure.invoke(it) }
                }
        }
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
            Doctor.roomNumber to roomNumber,
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

    fun delete(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firebaseFirestore.collection(collection)
            .whereEqualTo(Doctor.id, id)
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


    fun edit(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val appointment = ArrayList<HashMap<String, Any>>()
        appointments.map {
            appointment.add(
                hashMapOf(
                    Appointment.isAvailable to it.isAvailable,
                    Appointment.date to it.date
                )
            )
        }
        firebaseFirestore.collection(collection)
            .whereEqualTo(Doctor.id, id)
            .get()
            .addOnSuccessListener { task ->
                task.documents.map { document ->
                    firebaseFirestore.collection(collection)
                        .document(
                            document.id
                        ).update(
                            hashMapOf<String, Any>(
                                Doctor.lastName to lastName,
                                Doctor.firstName to firstName,
                                Doctor.roomNumber to roomNumber,
                                Doctor.dateOfBirth to dateOfBirth,
                                Doctor.dateStartWork to dateStartWork,
                                Doctor.specializations to specializations,
                                Doctor.appointments to appointment
                            )
                        )
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