package com.example.finallywork.ui.appointment

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finallywork.databinding.ActivityRateAppointmentBinding
import com.example.finallywork.models.Doctor
import com.example.finallywork.models.Review
import com.example.finallywork.models.UserAppointment
import java.util.UUID

class RateAppointmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRateAppointmentBinding
    private var doctor: Doctor? = null
    private var doctorId: String? = null
    private var appointmentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            doctorId = intent.getStringExtra("doctor")
            doctorId?.let {
                Doctor.getById(
                    it,
                    onSuccess = { result ->
                        doctor = result
                        binding.doctorName.text = result.lastName + " " + result.firstName
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                    })
            }
            appointmentId = intent.getStringExtra("appointment")
        }

        binding.submitButton.setOnClickListener {
            appointmentId?.let { appointment ->
                Review(
                    id = UUID.randomUUID().toString(),
                    userAppointmentId = appointment,
                    rating = binding.ratingBar.numStars
                ).addToDataBase(
                    onSuccess = {
                        getAppointment(appointment)
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                    })
            }
        }
    }

    private fun getAppointment(id: String) {
        UserAppointment.getById(
            id = id,
            onSuccess = { userAppointment ->
                userAppointment.makeRated(
                    onSuccess = {
                        doctorId?.let { getDoctor(it) }
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                    })
            },
            onFailure = { exception ->
                Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
            })
    }


    private fun getDoctor(id: String) {
        Doctor.getById(
            id = id,
            onSuccess = { doctor ->
                doctor.calculateRate(
                    reviewRating = binding.ratingBar.progress,
                    onSuccess = {
                        finish()
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, exception, Toast.LENGTH_LONG)
                            .show()
                    })
                finish()
            },
            onFailure = { exception ->
                Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
            }
        )
    }
}