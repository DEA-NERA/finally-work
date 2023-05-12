package com.example.finallywork.ui.appointment

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finallywork.databinding.ActivityCreateAppointmentBinding
import com.example.finallywork.models.Doctor
import com.example.finallywork.ui.adapters.TimeAdapter
import java.util.Calendar

class CreateAppointmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAppointmentBinding
    private var doctor: Doctor? = null
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
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, exception, Toast.LENGTH_LONG).show()
                    })
            }
        }
        val calendar = Calendar.getInstance()
        binding.calendar.minDate = calendar.timeInMillis

        binding.calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            doctor?.let {

            }
        }

        binding.timeList.adapter = timeAdapter

    }
}