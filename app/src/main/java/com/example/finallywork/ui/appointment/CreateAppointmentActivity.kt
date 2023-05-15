package com.example.finallywork.ui.appointment

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finallywork.databinding.ActivityCreateAppointmentBinding
import com.example.finallywork.models.Doctor
import com.example.finallywork.models.UserAppointment
import com.example.finallywork.ui.adapters.TimeAdapter
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import java.util.Date
import java.util.UUID

class CreateAppointmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAppointmentBinding
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var doctor: Doctor? = null
    private var userAppointment: UserAppointment? = null
    private val timeAdapter: TimeAdapter by lazy { TimeAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            val doctorId = intent.getStringExtra("doctor")
            doctorId?.let {
                Doctor.getById(
                    doctorId,
                    onSuccess = { result ->
                        doctor = result
                        timeAdapter.list =
                            result.appointments.filter { it.date.month == Date().month && it.date.date == Date().date }
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                    })
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val calendar = Calendar.getInstance()
        binding.calendar.minDate = calendar.timeInMillis
        binding.calendar.firstDayOfWeek = Calendar.MONDAY

        binding.calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            doctor?.let { doctor ->
                timeAdapter.list =
                    doctor.appointments.filter { it.date.month == month && it.date.date == dayOfMonth }
            }
        }

        timeAdapter.onItemClick = {
            doctor?.let { doctor ->
                firebaseAuth.currentUser?.uid?.let { userId ->
                    userAppointment = UserAppointment(
                        id = UUID.randomUUID().toString(),
                        date = it.date,
                        userId = userId,
                        doctorId = doctor.id,
                        isRated = false
                    )
                }
            }
        }

        binding.timeList.adapter = timeAdapter

        binding.createAppointmentButton.setOnClickListener {
            doctor?.appointments?.first { it.date == userAppointment?.date }?.isAvailable =
                false
            doctor?.edit(
                onSuccess = {
                    userAppointment?.addToDataBase(
                        onSuccess = {
                            finish()
                        },
                        onFailure = { exception ->
                            Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                onFailure = { exception ->
                    Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}