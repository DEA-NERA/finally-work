package com.example.finallywork.ui.appointment

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finallywork.databinding.ActivityRateAppointmentBinding
import com.example.finallywork.models.Doctor
import com.example.finallywork.models.Review
import java.util.UUID

class RateAppointmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRateAppointmentBinding
    private var doctor: Doctor? = null
    private var appointmentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            val doctorId = intent.getStringExtra("doctor")
            doctorId?.let {
                Doctor.getById(
                    doctorId,
                    onSuccess = { result ->
                        doctor = result
                        binding.doctorName.text = result.lastName + " " + result.firstName
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                    })
            }
            val appointment = intent.getStringExtra("appointment")
            appointment?.let {
                appointmentId = it
            }
        }

        binding.submitButton.setOnClickListener {
            appointmentId?.let { appointment ->
                Review(
                    id = UUID.randomUUID().toString(),
                    userAppointmentId = appointment,
                    rating = binding.ratingBar.numStars
                ).addToDataBase(
                    onSuccess = {
                        finish()
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                    })
            }
        }

    }
}