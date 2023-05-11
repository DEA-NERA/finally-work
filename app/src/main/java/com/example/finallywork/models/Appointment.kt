package com.example.finallywork.models

import java.util.Date

/*
 * Created by Vladyslava Buriakovska on 10.05.2023
 */
data class Appointment(
    val isAvailable: Boolean,
    val date: Date,
) {

    companion object {
        const val isAvailable = "isAvailable"
        const val date = "date"
    }
}