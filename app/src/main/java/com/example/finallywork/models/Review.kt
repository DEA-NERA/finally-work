package com.example.finallywork.models

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/*
 * Created by Vladyslava Buriakovska on 13.05.2023
 */
class Review(
    val id: String,
    val userAppointmentId: String,
    val rating: Int
) {
    companion object {
        val firebaseFirestore: FirebaseFirestore by lazy { Firebase.firestore }
        const val collection = "reviews"
        const val id = "id"
        const val userAppointmentId = "userAppointmentId"
        const val rating = "rating"
    }

    fun addToDataBase(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val review = hashMapOf(
            Review.id to id,
            Review.userAppointmentId to userAppointmentId,
            Review.rating to rating
        )
        firebaseFirestore.collection(collection)
            .add(review)
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { onFailure.invoke(it) }
            }
    }
}